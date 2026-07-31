---
name: run-designsystem-compose-ui-test
description: This skill should be used when the user asks to run or verify Nevera `core:designsystem` Compose UI instrumentation tests on an Android emulator or connected device, especially with phrases like "designsystem Compose 테스트 실행", "core:designsystem UI 테스트 돌려줘", "디자인시스템 Compose 테스트 검증", "designsystem connectedDebugAndroidTest 실행", "run-designsystem-compose-tests.sh 실행", or "Claude Code로 디자인시스템 에뮬레이터 테스트".
---

# core:designsystem Compose UI 테스트 실행 가이드

Nevera Android 프로젝트에서 `core:designsystem` Compose UI 계측 테스트를 Android 에뮬레이터 또는 연결된 기기로 검증한다. Android Studio UI를 조작하지 않고 CLI에서 실행 가능한 경로를 우선 사용한다.

## 기준 파일

작업 전에 다음 파일의 존재를 확인한다.

- `scripts/android/run-designsystem-compose-tests.sh`
- `core/designsystem/build.gradle.kts`
- `core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/test/NeveraComposeTest.kt`

상세 배경이나 테스트 작성 기준이 필요하면 다음 문서를 읽는다.

- `docs/execplan-designsystem-compose-tests.md`

## 기본 실행 원칙

- 저장소 루트에서 실행한다.
- `core:designsystem` 모듈만 검증한다.
- 에뮬레이터/ADB 실행은 로컬 포트, device daemon, 가상화 프로세스를 사용하므로 권한 승인이 필요할 수 있음을 감안한다.
- 이미 연결된 Android 기기 또는 에뮬레이터가 있으면 재사용한다.
- 직접 시작한 에뮬레이터만 종료한다. 사용자가 미리 켜 둔 기기는 종료하지 않는다.
- 테스트 실패를 곧바로 flaky로 판단하지 말고, 노드 탐색 실패, 콜백 실패, 입력 상태 연결 실패, idle timeout, 실행 환경 문제로 분류한다.

## 빠른 실행

사용자가 `core:designsystem` Compose UI 테스트 검증을 요청하면 먼저 다음 명령을 실행한다.

```bash
scripts/android/run-designsystem-compose-tests.sh
```

이 스크립트는 다음 순서로 동작한다.

1. `$ANDROID_HOME/platform-tools/adb`를 우선 찾고, 없으면 PATH의 `adb`를 찾는다.
2. `adb devices`에서 `device` 상태의 첫 번째 기기가 있으면 그 serial을 대상으로 재사용한다.
3. 연결 기기가 없을 때만 `$ANDROID_HOME/emulator/emulator`(없으면 PATH의 `emulator`)를 찾는다.
4. `NEVERA_TEST_AVD` 또는 첫 번째 AVD를 선택한다.
5. 기존 `emulator-*` serial 목록을 기록한 뒤 에뮬레이터를 백그라운드로 시작한다. 로그는 `mktemp` 파일에 남기고 경로를 출력한다.
6. 5번에서 기록한 목록에 **없는** 새 `emulator-*` serial이 올라오기를 기다린 뒤,
   `adb -s <serial> shell getprop sys.boot_completed`로 부팅 완료를 확인한다.
7. `adb -s <serial> shell input keyevent 82`로 잠금 화면 해제를 시도한다.
8. `ANDROID_SERIAL=<serial> ./gradlew :core:designsystem:connectedDebugAndroidTest`를 실행한다.
9. 스크립트가 직접 시작한 에뮬레이터만 `adb -s <serial> emu kill`로 종료한다. 종료되지 않으면 추적한 PID로 정리한다.
10. 에뮬레이터 로그는 성공 시 삭제하고, 실패 시 경로를 출력해 남긴다.

기기가 여러 대 붙어 있어도 모든 `adb` 호출과 Gradle 실행이 2번에서 고른 한 대만 대상으로 한다.
특정 기기를 쓰려면 `adb devices` 목록에서 확인한 뒤 해당 기기만 연결한 상태로 실행한다.
부팅 대기가 새 serial만 대상으로 하므로, 사용자가 미리 켜 둔(또는 부팅 중인) 에뮬레이터를 잡아 종료하지 않는다.

## 설치가 INSTALL_FAILED_VERIFICATION_FAILURE로 실패할 때

Google Play/Google APIs 이미지 AVD에서는 패키지 검증기가 `Integrity verification timed out`으로 APK 설치를
막을 수 있다. 테스트 코드 문제가 아니므로 대상 기기에서 검증기를 끄고 다시 실행한다.

```bash
adb -s <serial> shell settings put global verifier_verify_adb_installs 0
adb -s <serial> shell settings put global package_verifier_enable 0
```

## 특정 AVD 지정

사용자가 특정 에뮬레이터를 지정했거나 기본 AVD가 부적절하면 `NEVERA_TEST_AVD`를 사용한다.

```bash
NEVERA_TEST_AVD=Pixel_6_API_34 scripts/android/run-designsystem-compose-tests.sh
```

AVD 목록 확인이 필요하면 다음 명령을 실행한다.

```bash
$ANDROID_HOME/emulator/emulator -list-avds
```

`$ANDROID_HOME`이 없거나 실패하면 PATH의 emulator를 사용한다.

```bash
emulator -list-avds
```

AVD가 하나도 없으면 테스트를 계속하지 말고, Android Studio Device Manager에서 API 30 이상 AVD를 만들 필요가 있다고 보고한다. 이 프로젝트의 `minSdk`는 30이다.

## 헤드리스 실행

창 없이 실행해야 하는 환경이면 다음 명령을 사용한다.

```bash
NEVERA_HEADLESS_EMULATOR=true scripts/android/run-designsystem-compose-tests.sh
```

헤드리스 실행은 로컬 GPU, emulator image, macOS 권한 상태에 따라 실패할 수 있다. 실패하면 일반 창 모드 실행 또는 사용자가 Android Studio에서 에뮬레이터를 직접 켠 뒤 Gradle 명령만 실행하는 경로로 전환한다.

## 이미 에뮬레이터가 켜져 있을 때

사용자가 이미 Android Studio나 CLI로 에뮬레이터를 켜 둔 상태라면 Gradle 명령만 실행해도 된다.

```bash
./gradlew :core:designsystem:connectedDebugAndroidTest
```

다만 사용자가 "Claude Code가 알아서 실행"을 요청한 경우에는 스크립트를 우선 사용한다. 스크립트가 연결 기기를 감지해 같은 Gradle 명령으로 이어진다.

## 사전 컴파일 확인

테스트 작성 직후이거나 컴파일 실패 가능성이 있으면 에뮬레이터 실행 전에 androidTest Kotlin 컴파일을 먼저 확인한다.

```bash
./gradlew :core:designsystem:compileDebugAndroidTestKotlin
```

앱 호출부까지 변경된 경우에는 다음 명령도 확인한다.

```bash
./gradlew :app:compileDebugKotlin
```

컴파일이 실패하면 에뮬레이터를 띄우지 말고 compile error를 먼저 수정한다.

## 권한 또는 샌드박스 실패 처리

`adb devices`, `emulator`, `connectedDebugAndroidTest` 실행이 다음 유형으로 실패하면 실행 환경 문제로 분류한다.

- `Operation not permitted`
- `could not install *smartsocket* listener`
- ADB daemon start 실패
- emulator process launch 실패
- local port listener 관련 오류

이 경우 같은 명령을 사용자 승인 또는 권한 상승 경로로 다시 실행한다. 승인을 받을 수 없는 환경이면 사용자가 Android Studio에서 에뮬레이터를 직접 켜도록 안내한 뒤, 연결된 기기를 대상으로 다음 명령만 실행한다.

```bash
./gradlew :core:designsystem:connectedDebugAndroidTest
```

## 실패 분석 순서

테스트 실패가 발생하면 다음 순서로 확인한다.

1. **컴파일 실패**: import, 함수 signature, experimental opt-in, Gradle 의존성을 확인한다.
2. **노드 탐색 실패**: `onNodeWithText`, `onNodeWithContentDescription`, semantics matcher가 실제 UI 의미 정보와 맞는지 확인한다.
3. **클릭 콜백 실패**: `enabled` 상태, callback wiring, 중복 node matcher를 확인한다.
4. **입력 실패**: 테스트 composition 안에서 `remember { mutableStateOf(...) }`로 hoisted state를 연결했는지 확인한다.
5. **idle timeout**: 무한 animation, 반복 coroutine, 계속 변경되는 state가 있는지 확인한다.
6. **에뮬레이터 실패**: AVD 존재 여부, boot 완료, Android SDK emulator binary 경로를 확인한다.

`ComposeNotIdleException`이 발생하면 테스트 코드만 완화하지 말고 프로덕션 composable이 불필요한 animation이나 계속되는 side effect를 만들고 있는지 먼저 확인한다.

## 성공 결과 보고 형식

성공하면 다음 정보를 짧게 보고한다.

- 실행 명령
- 사용한 기기 또는 AVD 이름
- 테스트 개수와 실패/에러/스킵 수
- 주요 결과 파일 위치

예시:

```text
scripts/android/run-designsystem-compose-tests.sh로 Pixel_6_API_34 AVD에서 실행했고,
core:designsystem connectedDebugAndroidTest 48개가 모두 통과했습니다.
결과 XML은 core/designsystem/build/outputs/androidTest-results/connected/debug/ 아래에 생성되었습니다.
```

실패하면 실패한 테스트명, 에러 유형, 다음 조치만 먼저 요약한다. 긴 Gradle 로그 전체를 붙이지 말고 핵심 원인 라인과 관련 파일을 인용한다.
