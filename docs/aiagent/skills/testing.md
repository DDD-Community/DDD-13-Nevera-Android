# 테스트 스킬

계측 테스트 실행처럼 로컬 환경(에뮬레이터·ADB) 준비가 필요한 작업을 자동화하는 스킬입니다. `/스킬명` 직접 호출 또는 자연어 요청으로 트리거합니다.

## 목차

- [/run-designsystem-compose-ui-test](#run-designsystem-compose-ui-test) — `core:designsystem` Compose UI 계측 테스트 실행

---

### run-designsystem-compose-ui-test

**`core:designsystem`의 Compose UI 계측 테스트를 에뮬레이터에서 실행합니다.**

Android Studio를 열지 않고 CLI에서 `:core:designsystem:connectedDebugAndroidTest`까지 도달하도록, 에뮬레이터 탐색·부팅 대기·실행·정리를 한 번에 처리합니다.

**트리거 예시**

```text
/run-designsystem-compose-ui-test
designsystem Compose 테스트 실행
core:designsystem UI 테스트 돌려줘
디자인시스템 Compose 테스트 검증
```

**동작 방식**

내부적으로 [`scripts/android/run-designsystem-compose-tests.sh`](../../../scripts/android/run-designsystem-compose-tests.sh)를 실행하며, 스크립트는 다음 순서로 동작합니다.

1. `$ANDROID_HOME/platform-tools/adb`를 우선 찾고, 없으면 PATH의 `adb`를 사용
2. `adb devices`에 `device` 상태 기기가 있으면 그 중 첫 번째를 **재사용** (새로 띄우지 않음)
3. 연결 기기가 없을 때만 `NEVERA_TEST_AVD` 또는 첫 번째 AVD로 에뮬레이터 시작
4. 시작 **전** `emulator-*` serial 목록을 기록하고, 그 목록에 **없는** 새 serial만 부팅 대기 대상으로 선택
5. `sys.boot_completed` 확인 후 잠금 해제(`keyevent 82`)
6. `ANDROID_SERIAL=<serial> ./gradlew :core:designsystem:connectedDebugAndroidTest` 실행
7. **직접 시작한 에뮬레이터만** `emu kill`로 종료. 종료되지 않으면 추적한 PID로 정리
8. 에뮬레이터 로그는 성공 시 삭제하고, 실패 시 경로를 출력

**사용자가 미리 켜 둔 기기는 종료하지 않습니다.** 4단계에서 기존 serial 목록을 제외하기 때문입니다.

> ⚠️ **물리 기기가 연결돼 있으면 그 기기에서 실행됩니다.**
> 2단계의 재사용 판단은 `adb devices`의 상태가 `device`인지만 보고 serial 종류를 가리지 않습니다. 따라서 USB로 연결된 실제 휴대전화가 있으면 에뮬레이터를 띄우지 않고 그 기기를 대상으로 삼으며, 부팅 대기(5단계)도 이미 준비된 기기이므로 건너뜁니다.
> 에뮬레이터에서 돌리려면 실행 전에 물리 기기 연결을 해제하세요.

**검사 범위는 `core:designsystem`뿐입니다.** 6단계에서 보듯 `:core:designsystem:connectedDebugAndroidTest`만 실행하므로, `infra:permission`의 계측 테스트는 포함되지 않습니다. 필요하면 별도로 실행합니다.

```bash
./gradlew :infra:permission:connectedDebugAndroidTest
```

**특정 AVD 지정**

```bash
NEVERA_TEST_AVD=Pixel_6_API_34 scripts/android/run-designsystem-compose-tests.sh
```

AVD가 하나도 없으면 테스트를 진행하지 않고 보고합니다. 이 프로젝트의 `minSdk`는 30이라 API 30 이상 AVD가 필요합니다.

**자주 겪는 실패**

| 증상 | 원인 | 조치 |
|------|------|------|
| `INSTALL_FAILED_VERIFICATION_FAILURE` | Google Play/APIs 이미지의 패키지 검증기가 설치를 차단 | 대상 기기에서 `verifier_verify_adb_installs`·`package_verifier_enable`을 0으로 설정 후 재실행 |
| 부팅 타임아웃 | 에뮬레이터 기동 지연 | 출력된 로그 경로와 마지막 20줄을 확인 |

테스트 실패는 곧바로 flaky로 판단하지 않고 노드 탐색 실패 · 콜백 실패 · 입력 상태 연결 실패 · idle timeout · 실행 환경 문제로 분류합니다.

**관련 문서**

- [디자인 시스템 Compose 테스트 설계 기록](../../execplan-designsystem-compose-tests.md)
