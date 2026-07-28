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

EMULATOR_BIN="${ANDROID_HOME:-}/emulator/emulator"
if [ ! -x "$EMULATOR_BIN" ]; then
    EMULATOR_BIN="$(command -v emulator || true)"
fi

if [ -z "$EMULATOR_BIN" ]; then
    echo "emulator was not found on PATH. Add Android SDK emulator tools to PATH first."
    exit 1
fi

STARTED_EMULATOR=false

cleanup() {
    if [ "$STARTED_EMULATOR" = "true" ]; then
        "$ADB_BIN" emu kill >/dev/null 2>&1 || true
    fi
}
trap cleanup EXIT

ADB_DEVICES_OUTPUT="$("$ADB_BIN" devices)" || {
    echo "adb devices failed. Check ADB permissions or rerun with elevated privileges."
    exit 1
}

if printf '%s\n' "$ADB_DEVICES_OUTPUT" | awk 'NR > 1 && $2 == "device" { found = 1 } END { exit found ? 0 : 1 }'; then
    echo "Using already connected Android device or emulator."
else
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
fi

BOOT_DEADLINE=$((SECONDS + 180))
until [ "$("$ADB_BIN" get-state 2>/dev/null)" = "device" ] &&
    [ "$("$ADB_BIN" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]; do
    if [ "$SECONDS" -ge "$BOOT_DEADLINE" ]; then
        echo "Timed out waiting for emulator boot."
        exit 1
    fi
    sleep 2
done

"$ADB_BIN" shell input keyevent 82 >/dev/null 2>&1 || true

./gradlew :core:designsystem:connectedDebugAndroidTest
