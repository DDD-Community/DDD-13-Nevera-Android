#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT_DIR"

ADB_BIN="${ANDROID_HOME:-}/platform-tools/adb"
if [ ! -x "$ADB_BIN" ]; then
    ADB_BIN="$(command -v adb || true)"
fi

if [ -z "$ADB_BIN" ]; then
    echo "adb was not found on PATH. Add Android SDK platform-tools to PATH first."
    exit 1
fi

STARTED_EMULATOR=false
EMULATOR_PID=""
EMULATOR_LOG=""
DEVICE_SERIAL=""

cleanup() {
    local exit_code=$?

    if [ "$STARTED_EMULATOR" = "true" ]; then
        # 부팅에 성공해 serial을 얻었으면 정상 경로로 종료한다.
        if [ -n "$DEVICE_SERIAL" ]; then
            "$ADB_BIN" -s "$DEVICE_SERIAL" emu kill >/dev/null 2>&1 || true
        fi

        # emu kill이 반영될 시간을 준다.
        if [ -n "$EMULATOR_PID" ] && [ -n "$DEVICE_SERIAL" ]; then
            for _ in 1 2 3 4 5 6 7 8 9 10; do
                kill -0 "$EMULATOR_PID" 2>/dev/null || break
                sleep 1
            done
        fi

        # 부팅 실패로 serial을 얻지 못했거나 emu kill이 듣지 않으면 프로세스를 직접 정리한다.
        if [ -n "$EMULATOR_PID" ] && kill -0 "$EMULATOR_PID" 2>/dev/null; then
            kill "$EMULATOR_PID" 2>/dev/null || true
            for _ in 1 2 3 4 5; do
                kill -0 "$EMULATOR_PID" 2>/dev/null || break
                sleep 1
            done
            kill -9 "$EMULATOR_PID" 2>/dev/null || true
        fi
    fi

    # 로그는 실패 원인 파악에 필요하므로 성공했을 때만 지운다.
    if [ -n "$EMULATOR_LOG" ] && [ -f "$EMULATOR_LOG" ]; then
        if [ "$exit_code" -eq 0 ]; then
            rm -f "$EMULATOR_LOG"
        else
            echo "Emulator log kept at: $EMULATOR_LOG"
        fi
    fi
}
trap cleanup EXIT

# device 상태인 serial 목록. serial에는 공백이 없어 단어 분리로 순회해도 안전하다.
list_ready_serials() {
    "$ADB_BIN" devices 2>/dev/null | awk 'NR > 1 && $2 == "device" { print $1 }' || true
}

# 상태와 무관한 전체 emulator serial 목록. 부팅 중(offline)인 것도 포함한다.
list_emulator_serials() {
    "$ADB_BIN" devices 2>/dev/null | awk 'NR > 1 && $1 ~ /^emulator-/ { print $1 }' || true
}

if ! "$ADB_BIN" devices >/dev/null 2>&1; then
    echo "adb devices failed. Check ADB permissions or rerun with elevated privileges."
    exit 1
fi

for serial in $(list_ready_serials); do
    DEVICE_SERIAL="$serial"
    break
done

if [ -n "$DEVICE_SERIAL" ]; then
    echo "Using already connected Android device or emulator: $DEVICE_SERIAL"
else
    EMULATOR_BIN="${ANDROID_HOME:-}/emulator/emulator"
    if [ ! -x "$EMULATOR_BIN" ]; then
        EMULATOR_BIN="$(command -v emulator || true)"
    fi

    if [ -z "$EMULATOR_BIN" ]; then
        echo "emulator was not found on PATH. Add Android SDK emulator tools to PATH first."
        exit 1
    fi

    AVD_NAME="${NEVERA_TEST_AVD:-$("$EMULATOR_BIN" -list-avds | sed -n '1p')}"
    if [ -z "$AVD_NAME" ]; then
        echo "No Android Virtual Device found. Create an API 30+ AVD first."
        exit 1
    fi

    EMULATOR_FLAGS=(-avd "$AVD_NAME" -no-snapshot-save -no-audio)
    if [ "${NEVERA_HEADLESS_EMULATOR:-false}" = "true" ]; then
        EMULATOR_FLAGS+=(-no-window -gpu swiftshader_indirect)
    fi

    # 아래 폴링이 "이 스크립트가 띄운" 에뮬레이터만 고르도록, 기존 emulator serial을 미리 기록한다.
    # 부팅 중이라 아직 device 상태가 아닌 기존 에뮬레이터를 잡아 사용자 기기를 종료하는 것을 막는다.
    PREEXISTING_EMULATORS="$(list_emulator_serials)"

    # BSD mktemp은 X를 템플릿 끝에서만 치환한다. `.log` 접미사를 붙이면 이름이 그대로 남아
    # 예측 가능한 경로가 되므로 X를 끝에 둔다.
    EMULATOR_LOG="$(mktemp "${TMPDIR:-/tmp}/nevera-emulator-log.XXXXXX")"

    echo "Starting Android emulator: $AVD_NAME"
    echo "Emulator log: $EMULATOR_LOG"
    "$EMULATOR_BIN" "${EMULATOR_FLAGS[@]}" >"$EMULATOR_LOG" 2>&1 &
    EMULATOR_PID=$!
    # 정리는 cleanup이 PID로 직접 수행한다. job 테이블에서 빼서 종료 알림이 출력에 섞이지 않게 한다.
    disown "$EMULATOR_PID" 2>/dev/null || true
    STARTED_EMULATOR=true

    BOOT_DEADLINE=$((SECONDS + 180))
    while true; do
        CANDIDATE=""
        for serial in $(list_ready_serials); do
            case "$serial" in
                emulator-*) ;;
                *) continue ;;
            esac
            if printf '%s\n' "$PREEXISTING_EMULATORS" | grep -qxF -- "$serial"; then
                continue
            fi
            CANDIDATE="$serial"
            break
        done

        if [ -n "$CANDIDATE" ] && [ "$(
            "$ADB_BIN" -s "$CANDIDATE" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r'
        )" = "1" ]; then
            DEVICE_SERIAL="$CANDIDATE"
            break
        fi

        if [ "$SECONDS" -ge "$BOOT_DEADLINE" ]; then
            echo "Timed out waiting for emulator boot."
            echo "--- last 20 lines of emulator log ---"
            tail -n 20 "$EMULATOR_LOG" 2>/dev/null || true
            exit 1
        fi
        sleep 2
    done

    echo "Started emulator is ready: $DEVICE_SERIAL"
fi

"$ADB_BIN" -s "$DEVICE_SERIAL" shell input keyevent 82 >/dev/null 2>&1 || true

# 여러 기기가 붙어 있어도 위에서 고른 기기에서만 테스트를 실행한다.
ANDROID_SERIAL="$DEVICE_SERIAL" ./gradlew :core:designsystem:connectedDebugAndroidTest
