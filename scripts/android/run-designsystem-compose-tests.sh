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
DEVICE_SERIAL=""

cleanup() {
    if [ "$STARTED_EMULATOR" = "true" ] && [ -n "$DEVICE_SERIAL" ]; then
        "$ADB_BIN" -s "$DEVICE_SERIAL" emu kill >/dev/null 2>&1 || true
    fi
}
trap cleanup EXIT

# 첫 번째 사용 가능한 기기의 serial을 출력한다. $1으로 serial 패턴을 제한할 수 있다.
first_ready_serial() {
    local pattern="${1:-.}"
    "$ADB_BIN" devices 2>/dev/null |
        awk -v pattern="$pattern" 'NR > 1 && $2 == "device" && $1 ~ pattern { print $1; exit }' ||
        true
}

if ! "$ADB_BIN" devices >/dev/null 2>&1; then
    echo "adb devices failed. Check ADB permissions or rerun with elevated privileges."
    exit 1
fi

DEVICE_SERIAL="$(first_ready_serial)"

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

    echo "Starting Android emulator: $AVD_NAME"
    "$EMULATOR_BIN" "${EMULATOR_FLAGS[@]}" >/tmp/nevera-emulator.log 2>&1 &
    STARTED_EMULATOR=true

    BOOT_DEADLINE=$((SECONDS + 180))
    while true; do
        DEVICE_SERIAL="$(first_ready_serial '^emulator-')"
        if [ -n "$DEVICE_SERIAL" ] && [ "$(
            "$ADB_BIN" -s "$DEVICE_SERIAL" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r'
        )" = "1" ]; then
            break
        fi

        if [ "$SECONDS" -ge "$BOOT_DEADLINE" ]; then
            echo "Timed out waiting for emulator boot."
            exit 1
        fi
        sleep 2
    done
fi

"$ADB_BIN" -s "$DEVICE_SERIAL" shell input keyevent 82 >/dev/null 2>&1 || true

# 여러 기기가 붙어 있어도 위에서 고른 기기에서만 테스트를 실행한다.
ANDROID_SERIAL="$DEVICE_SERIAL" ./gradlew :core:designsystem:connectedDebugAndroidTest
