# core:designsystem Compose 테스트 구축

이 ExecPlan은 살아있는 문서다. `Progress`, `Surprises & Discoveries`, `Decision Log`, `Outcomes & Retrospective` 섹션은 작업이 진행되는 동안 반드시 최신 상태로 유지해야 한다.

이 문서는 저장소 루트의 `PLANS.md`에 정의된 규칙에 따라 작성되고 유지되어야 한다.


## Purpose / Big Picture


`core:designsystem` 모듈은 Nevera 앱에서 공통으로 쓰는 버튼, 입력 필드, AppBar, Dialog, BottomSheet, Switch, Stepper를 제공한다. 이 모듈의 컴포넌트가 클릭 가능 여부, 비활성 상태, 접근성 설명, 입력 값 전달 같은 기본 계약을 잃으면 여러 feature 화면이 동시에 깨질 수 있다. 지금은 `src/androidTest`와 `src/test`에 Android Studio가 만든 샘플 테스트만 있어 그런 회귀를 자동으로 잡지 못한다.

이 계획을 완료하면 에뮬레이터나 연결된 Android 기기에서 `./gradlew :core:designsystem:connectedDebugAndroidTest`를 실행해 핵심 디자인 시스템 컴포넌트의 실제 Compose 동작을 검증할 수 있다. 또한 Claude Code나 Codex 같은 CLI 기반 AI Agent가 Android Studio를 직접 조작하지 않고도, 사용 가능한 Android Virtual Device(AVD)를 찾아 에뮬레이터를 CLI로 켜고, 부팅 완료를 기다린 뒤 테스트를 실행하는 경로를 갖게 된다. 테스트는 스크린샷 비교가 아니라 사용자가 실제로 누르고 입력하는 관찰 가능한 동작을 검증한다. 예를 들어 Stepper의 최솟값에서 감소 버튼을 누르면 콜백이 호출되지 않고, PasswordTextField의 eye 아이콘을 누르면 접근성 설명과 표시 상태가 바뀌며, ConfirmDialog의 취소 버튼은 negative 콜백으로 이어져야 한다.

이 계획은 `core:designsystem` 모듈의 Compose UI 계측 테스트만 다룬다. JVM 단위 테스트, Roborazzi/Paparazzi 같은 스크린샷 테스트, feature 화면 테스트, CI 워크플로 변경은 범위 밖이다. 다만 이 계획의 결과물이 안정화되면 후속 계획에서 CI에 계측 테스트 실행 조건을 붙일 수 있다.


## Progress


- [x] (2026-07-28 Asia/Seoul) 사전 조사 완료. `core:designsystem`의 테스트가 샘플 파일뿐이고, `nevera.android.compose`가 Compose UI test 의존성을 이미 제공한다는 점을 확인했다.
- [x] (2026-07-28 Asia/Seoul) 실행 계획 문서 작성. 테스트 대상, 우선순위, 파일별 케이스, 검증 명령, 접근성 보강 범위를 이 문서에 정리했다.
- [x] (2026-07-28 Asia/Seoul) 계획 확장. AI Agent가 CLI에서 에뮬레이터를 부팅하고 Compose 계측 테스트를 실행할 수 있도록 AVD 확인, 부팅 대기, 테스트 실행, 복구 절차를 추가했다.
- [x] (2026-07-28 Asia/Seoul) 마일스톤 1 완료. `NeveraComposeTest.kt` 공통 헬퍼를 추가하고 샘플 `ExampleInstrumentedTest.kt`, `ExampleUnitTest.kt`를 제거했다.
- [x] (2026-07-28 Asia/Seoul) 마일스톤 2 완료. Button, IconButton, QuantityStepper, Switch Compose 계측 테스트를 추가했다.
- [x] (2026-07-28 Asia/Seoul) 마일스톤 3 완료. TextField, EmailTextField, PasswordTextField Compose 계측 테스트를 추가했다.
- [x] (2026-07-28 Asia/Seoul) 마일스톤 4 완료. AppBar, SearchAppBar, NavigationBar 테스트를 추가하고 NavigationBar item의 `contentDescription` 접근성 필드를 추가했다.
- [x] (2026-07-28 Asia/Seoul) 마일스톤 5 완료. ConfirmDialog, StepContentBottomSheet, IllustrationBottomSheet, DatePickerDialog, TimePickerDialog 테스트를 추가했다.
- [x] (2026-07-28 Asia/Seoul) 마일스톤 6 완료. `scripts/android/run-designsystem-compose-tests.sh`를 추가해 CLI에서 AVD 부팅, boot 완료 대기, connected test 실행을 자동화했다.
- [x] (2026-07-28 Asia/Seoul) 마일스톤 7 완료. `Pixel_6_API_34` AVD에서 48개 Compose 계측 테스트가 모두 통과했다.


## Surprises & Discoveries


- 관찰: `core/designsystem/build.gradle.kts`는 `nevera.android.compose`만 적용하지만, 이 플러그인이 `androidTestImplementation(androidx-compose-ui-test-junit4)`와 `debugImplementation(androidx-compose-ui-test-manifest)`를 이미 추가한다. 따라서 Compose 테스트를 시작하기 위해 별도 의존성 추가가 필요하지 않다.
  증거: `build-logic/src/main/kotlin/ComposeConfig.kt`의 `dependencies` 블록에 Compose BOM, `ui-test-junit4`, `ui-test-manifest`가 선언되어 있다.

- 관찰: `core:designsystem`에는 `Modifier.testTag`가 사실상 없다. 테스트는 우선 사용자가 볼 수 있는 텍스트, `contentDescription`, Compose semantics를 기준으로 작성해야 한다. 이런 기준으로 찾기 어려운 컴포넌트만 최소한의 테스트 태그나 접근성 속성을 추가한다.
  증거: 저장소 검색 결과 `Modifier.testTag`는 `infra/permission` 테스트 예시에서만 발견되었고, `core/designsystem` 프로덕션 코드에서는 발견되지 않았다.

- 관찰: `NeveraNavigationBarItem`은 아이콘의 `contentDescription`을 `null`로 렌더링한다. 현재 구조에서는 어느 탭을 눌렀는지 Compose 테스트가 안정적으로 찾기 어렵고, 보조 기술 사용자에게도 탭 의미가 전달되지 않는다.
  증거: `core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/component/navigationbar/NeveraNavigationBarItem.kt`의 `Icon(contentDescription = null)`.

- 관찰: 현재 로컬 환경에는 Android SDK의 `adb`와 `emulator` 실행 파일이 설치되어 있다. 그러나 관리형 샌드박스 안에서 `adb devices`를 실행하면 ADB daemon이 포트 리스너를 열지 못해 실패할 수 있다. 따라서 AI Agent가 CLI로 에뮬레이터와 ADB를 다루려면 실행 환경에 따라 권한 승인이 필요하다.
  증거: `which adb`는 `$ANDROID_HOME/platform-tools/adb`를, `which emulator`는 구버전 경로인 `$ANDROID_HOME/tools/emulator`를 반환했다. 샌드박스 안의 `adb devices`는 `could not install *smartsocket* listener: Operation not permitted`로 실패했다.

- 관찰: `PATH`의 `emulator`가 오래된 `$ANDROID_HOME/tools/emulator`를 가리키면 최신 SDK 구조에서 Qt/qemu 상대 경로를 잘못 해석해 실행에 실패할 수 있다. 스크립트는 `$ANDROID_HOME/emulator/emulator`를 우선 사용해야 한다.
  증거: 첫 스크립트 실행은 `/tmp/nevera-emulator.log`에 `Qt library not found at ../emulator/lib64/qt/lib`와 `Could not launch .../../emulator/qemu/darwin-x86_64/qemu-system-aarch64`를 남기고 timeout 되었다. `$ANDROID_HOME/emulator/emulator` 우선 사용으로 수정한 뒤에는 AVD가 정상 부팅되고 테스트가 실행되었다.

- 관찰: `NeveraTimePickerDialog`의 `PickerColumn`은 이미 원하는 위치에 있어도 `animateScrollToItem(currentIndex)`를 예약할 수 있어 Compose 테스트에서 idle timeout을 만들었다. 현재 index와 offset이 이미 정렬되어 있으면 animate를 건너뛰도록 보강하니 TimePicker 테스트까지 통과했다.
  증거: 첫 connected 실행에서 `NeveraTimePickerDialogTest`가 `ComposeNotIdleException: Idling resource timed out`으로 실패했다. `targetIndex != firstVisibleItemIndex || firstVisible != 0`일 때만 animate하도록 바꾼 뒤 48개 테스트가 통과했다.


## Decision Log


- 결정: 첫 도입은 스크린샷 테스트가 아니라 AndroidX Compose UI 계측 테스트로 한다.
  근거: 지금 필요한 것은 픽셀 단위 외형 고정이 아니라 클릭 가능 여부, disabled 차단, 입력 값 전달, 접근성 노출 같은 상호작용 계약이다. 이 계약은 `createComposeRule()`, `onNodeWithText`, `onNodeWithContentDescription`, `performClick`, `performTextInput`, `assertIsEnabled`, `assertIsNotEnabled`로 직접 검증할 수 있다. 스크린샷 테스트는 별도 라이브러리와 기준 이미지 관리가 필요하므로 첫 단계로는 비용이 크다.
  날짜/작성자: 2026-07-28 / Codex

- 결정: 테스트는 `src/androidTest/kotlin/com/anddd/nevera/core/designsystem/...` 아래에 프로덕션 패키지와 같은 하위 구조로 둔다.
  근거: Compose 테스트는 Android 런타임과 리소스가 필요하므로 `src/androidTest`가 맞다. 프로덕션 패키지 구조를 따라가면 `component/stepper/NeveraQuantityStepperTest.kt`처럼 테스트 대상이 즉시 보이고, 나중에 파일을 찾기 쉽다.
  날짜/작성자: 2026-07-28 / Codex

- 결정: 샘플 테스트 파일은 해당 모듈에 의미 있는 테스트가 들어가는 시점에 제거한다.
  근거: `ExampleInstrumentedTest`와 `ExampleUnitTest`는 항상 통과하는 껍데기라 품질 신호를 흐린다. 하지만 테스트가 하나도 없는 상태에서 먼저 지우면 모듈의 테스트 리포트가 비어 혼란을 줄 수 있으므로 마일스톤 1에서 실제 헬퍼와 첫 테스트를 추가하면서 제거한다.
  날짜/작성자: 2026-07-28 / Codex

- 결정: 프로덕션 코드 변경은 테스트 가능성과 접근성 개선에 필요한 최소 범위로 제한한다.
  근거: 테스트 때문에 공개 API를 크게 바꾸면 디자인 시스템 사용자에게 불필요한 변경 비용이 생긴다. 다만 `NavigationBarItem`의 탭 설명처럼 테스트 가능성과 접근성 개선이 같은 방향인 경우에는 공개 모델에 `contentDescription`을 추가하거나 기본 설명을 받을 수 있는 구조를 도입한다.
  날짜/작성자: 2026-07-28 / Codex

- 결정: 에뮬레이터 실행은 Gradle Managed Device를 바로 도입하지 않고, 먼저 로컬 AVD를 CLI로 부팅하는 스크립트 경로를 만든다.
  근거: Gradle Managed Device는 CI 자동화에 좋지만 system image 설치와 Gradle 설정 변경이 필요하다. 이번 계획의 목적은 로컬 AI Agent가 지금 있는 Android SDK와 AVD를 활용해 `connectedDebugAndroidTest`를 실행할 수 있게 하는 것이다. 따라서 첫 단계는 `emulator -list-avds`, `emulator -avd <name>`, `adb shell getprop sys.boot_completed`, `./gradlew :core:designsystem:connectedDebugAndroidTest`를 묶는 로컬 실행 경로로 한다. 이후 CI에서 안정성이 필요해지면 별도 ExecPlan으로 Gradle Managed Device를 도입한다.
  날짜/작성자: 2026-07-28 / Codex

- 결정: AI Agent가 에뮬레이터를 CLI로 실행할 때는 사용자 승인이 필요할 수 있음을 계획에 명시한다.
  근거: 에뮬레이터는 GUI/가상화 프로세스를 시작하고, ADB는 로컬 포트 리스너와 디바이스 제어 daemon을 사용한다. Codex Desktop 같은 관리형 환경에서는 이런 작업이 샌드박스 권한에 막힐 수 있다. 실패를 코드 문제로 오해하지 않도록 실행 계획에 권한 승인과 복구 절차를 포함한다.
  날짜/작성자: 2026-07-28 / Codex


## Outcomes & Retrospective


구현 완료 (2026-07-28 Asia/Seoul).

`core:designsystem`에 15개 Compose 계측 테스트 파일과 48개 테스트 케이스를 추가했다. 샘플 테스트는 제거했고, 공통 `NeveraTheme` 테스트 헬퍼를 추가했다. NavigationBar는 테스트 가능성과 접근성을 함께 개선하기 위해 `NeveraNavigationBarItem.contentDescription` 필드를 추가했고, 앱의 `TopLevelDestination.toNavigationBarItem()` 호출부도 설명 문자열을 채우도록 갱신했다.

CLI Agent 실행 경로도 구현했다. `scripts/android/run-designsystem-compose-tests.sh`는 이미 연결된 기기가 있으면 재사용하고, 없으면 `NEVERA_TEST_AVD` 또는 첫 번째 AVD를 CLI로 띄운 뒤 boot 완료를 기다려 `:core:designsystem:connectedDebugAndroidTest`를 실행한다. `PATH`의 오래된 emulator 문제를 피하기 위해 `$ANDROID_HOME/emulator/emulator`를 우선 사용한다.

검증 결과는 다음과 같다.

    ./gradlew :core:designsystem:compileDebugAndroidTestKotlin
    BUILD SUCCESSFUL

    ./gradlew :app:compileDebugKotlin
    BUILD SUCCESSFUL

    scripts/android/run-designsystem-compose-tests.sh
    Finished 48 tests on Pixel_6_API_34(AVD) - 14
    BUILD SUCCESSFUL in 9m 26s

배운 점은 두 가지다. 첫째, AI Agent가 CLI로 에뮬레이터를 실행하려면 ADB 권한과 정확한 emulator 바이너리 선택이 중요하다. 둘째, Compose 계측 테스트는 실제 컴포넌트의 idle/animation 문제를 드러내므로 단순 테스트 추가를 넘어 컴포넌트 안정성 개선에도 도움이 된다.


## Context and Orientation


이 문서의 모든 경로는 저장소 루트 기준 상대 경로다. Gradle 모듈 `:core:designsystem`은 `core/designsystem` 디렉터리에 있고, 네임스페이스는 `com.anddd.nevera.core.designsystem`이다. 모듈 설정 파일은 `core/designsystem/build.gradle.kts`이며 `nevera.android.compose` Convention Plugin을 적용한다. 이 플러그인은 Android library 설정, Compose 활성화, Material3/UI 의존성, Compose UI test 의존성을 제공한다.

Compose UI 계측 테스트란 Android 기기나 에뮬레이터에서 실제 Compose 트리를 띄우고 노드를 찾아 클릭, 입력, 단언을 수행하는 테스트다. 이 저장소에서는 `androidx.compose.ui.test.junit4.createComposeRule`을 사용한다. 테스트는 JUnit4 스타일의 `@get:Rule`과 `org.junit.Test`를 사용한다. JVM 단위 테스트가 사용하는 JUnit5와 다르지만, `src/androidTest`에서는 AndroidX 테스트 러너가 JUnit4 테스트를 실행하므로 문제가 없다.

AVD(Android Virtual Device)는 Android Emulator가 부팅하는 가상 기기 설정이다. Android Studio의 Device Manager에서 만든 Pixel 기기 같은 항목이 AVD다. CLI Agent는 Android Studio UI를 열지 않고 `emulator -list-avds`로 AVD 이름 목록을 읽고, `emulator -avd <AVD_NAME>`으로 해당 가상 기기를 켤 수 있다. 에뮬레이터가 완전히 켜졌는지는 `adb shell getprop sys.boot_completed`가 `1`을 반환하는지로 확인한다. 이 확인이 끝난 뒤에만 `connectedDebugAndroidTest`를 실행한다.

계획 착수 시점의 관련 파일은 다음과 같다. 아래 두 샘플 테스트는 이 계획에서 제거했으므로 현재 저장소에는 없다.

    core/designsystem/build.gradle.kts
    build-logic/src/main/kotlin/ComposeConfig.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/ExampleInstrumentedTest.kt  (제거됨)
    core/designsystem/src/test/kotlin/com/anddd/nevera/ExampleUnitTest.kt  (제거됨)
    core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/ui/theme/Theme.kt
    core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/component/button/
    core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/component/stepper/NeveraQuantityStepper.kt
    core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/component/toggle/NeveraSwitch.kt
    core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/component/textfield/
    core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/component/appbar/
    core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/component/navigationbar/
    core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/component/dialog/
    core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/component/bottomsheet/
    core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/component/datepicker/
    core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/component/timepicker/

테스트는 모든 컴포넌트를 한 번에 얕게 덮지 않는다. 우선 상호작용 계약이 뚜렷하고 실패 시 앱 화면에 직접 영향을 주는 컴포넌트부터 시작한다. 여기서 계약이란 "이 컴포넌트가 외부에 약속하는 동작"이라는 뜻이다. 예를 들어 `NeveraQuantityStepper`의 계약은 `quantity`가 `minQuantity`보다 작거나 같으면 감소 콜백을 호출하지 않는 것이다. 이 계약이 깨지면 냉장고 재료 수량 같은 화면에서 잘못된 값이 내려갈 수 있다.


## Plan of Work


### 마일스톤 1: Compose 테스트 공통 헬퍼와 샘플 테스트 정리

이 마일스톤이 끝나면 `core:designsystem`의 Compose 테스트가 모두 같은 방식으로 `NeveraTheme`를 적용하고, 샘플 테스트 파일이 실제 테스트 기반으로 교체된다. 새 테스트 헬퍼 파일은 `core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/test/NeveraComposeTest.kt`로 만든다. 이 파일에는 `ComposeContentTestRule.setNeveraContent(content)` 확장 함수를 둔다. 함수는 내부에서 `setContent { NeveraTheme { content() } }`를 호출한다.

`core/designsystem/src/androidTest/kotlin/com/anddd/nevera/ExampleInstrumentedTest.kt`는 제거한다. `core/designsystem/src/test/kotlin/com/anddd/nevera/ExampleUnitTest.kt`도 이 계획 범위 안에서는 제거한다. `core:designsystem`에 JVM 단위 테스트가 없고 이 계획은 `src/androidTest`만 사용하므로, 항상 통과하는 JVM 샘플은 남겨 둘 이유가 없다.

첫 헬퍼 검증용으로 가장 작은 실제 테스트를 함께 추가한다. 추천 파일은 `core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/button/NeveraButtonTest.kt`다. `NeveraFilledButton`을 렌더링하고 `onNodeWithText("확인").assertIsDisplayed()`가 통과하는지 확인한다.


### 마일스톤 2: 클릭과 disabled 계약이 명확한 원자 컴포넌트 테스트

이 마일스톤이 끝나면 가장 작은 상호작용 컴포넌트인 Button, Stepper, Switch가 테스트로 보호된다. 이 세 영역은 외부 상태나 Android 시스템 UI에 의존하지 않아 안정적인 첫 묶음이다.

`core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/button/NeveraButtonTest.kt`에 텍스트 버튼 계열 테스트를 작성한다. `NeveraFilledButton`, `NeveraOutlinedButton`, `NeveraGhostButton`, `NeveraWeakButton`을 각각 렌더링해 label 표시와 클릭 콜백 호출을 검증한다. disabled 상태에서는 `assertIsNotEnabled()`와 `performClick()` 후 콜백 미호출을 검증한다. 같은 패턴을 반복하되, 테스트 이름은 한글 문장형으로 쓴다. 예를 들어 `Filled 버튼을 누르면 클릭 콜백을 호출한다`, `disabled Filled 버튼은 클릭 콜백을 호출하지 않는다`처럼 작성한다.

아이콘 버튼 계열은 별도 파일 `NeveraIconButtonTest.kt`를 만든다. `NeveraFilledIconButton`, `NeveraOutlinedIconButton`, `NeveraGhostIconButton`, `NeveraWeakIconButton`을 대상으로 `contentDescription`으로 노드를 찾고 enabled/disabled 클릭 계약을 검증한다. painter는 테스트 안에서 `rememberVectorPainter(Icons.Default.Add)`를 쓰면 된다. 만약 Material Icons 의존성이 테스트 컴파일에서 보이지 않으면 `NeveraIcons.Plus`처럼 디자인 시스템 리소스 painter를 사용한다.

`core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/stepper/NeveraQuantityStepperTest.kt`를 만든다. 테스트 케이스는 최소 다섯 개다. 첫째, 현재 수량 텍스트가 표시된다. 둘째, `quantity = minQuantity`일 때 `"수량 1개 감소"` 노드는 비활성이다. 셋째, `quantity = maxQuantity`일 때 `"수량 1개 증가"` 노드는 비활성이다. 넷째, 중간값에서 감소 버튼을 누르면 `onDecrease`가 한 번 호출된다. 다섯째, 중간값에서 증가 버튼을 누르면 `onIncrease`가 한 번 호출된다. 추가로 `quantity`가 `minQuantity`보다 작은 비정상 입력이어도 감소 콜백이 호출되지 않고, `maxQuantity`보다 큰 비정상 입력이어도 증가 콜백이 호출되지 않는 계약을 고정한다.

`core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/toggle/NeveraSwitchTest.kt`를 만든다. Switch는 텍스트나 contentDescription이 없으므로 semantics matcher를 사용한다. `onNode(hasClickAction() and isToggleable())` 또는 role 기반 matcher로 찾는다. 케이스는 `checked = false`에서 클릭하면 `true`가 전달된다, `checked = true`에서 클릭하면 `false`가 전달된다, disabled이면 클릭 콜백이 호출되지 않는다, checked 상태 semantics가 반영된다 네 가지다. 이 테스트가 노드를 안정적으로 찾지 못하면 `NeveraSwitch`에 선택적 `modifier`를 이용해 테스트에서 `Modifier.testTag("switch")`를 넘기고 `onNodeWithTag("switch")`로 찾는다. 프로덕션 코드 변경은 필요 없다.


### 마일스톤 3: TextField 계열 테스트

이 마일스톤이 끝나면 일반 입력, 이메일 입력, 비밀번호 입력의 사용자가 체감하는 계약이 테스트로 고정된다. TextField는 내부에 `String` 오버로드와 `TextFieldValue` 오버로드가 있고, unfocused/disabled일 때 정적 텍스트를 보여 주는 분기와 focused일 때 실제 입력 필드를 보여 주는 분기가 있어 회귀 위험이 높다.

`core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/textfield/NeveraTextFieldTest.kt`를 만든다. `NeveraTextField(value = "", config = NeveraTextFieldConfig(heading = "이름", placeholder = "입력", description = "도움말"))`를 렌더링하고 heading, placeholder, description이 보이는지 검증한다. 입력 테스트는 테스트 컴포저블 내부에서 `var value by remember { mutableStateOf("") }`를 두고 `onValueChange = { value = it }`로 연결한 뒤, placeholder 노드에 클릭하고 `performTextInput("abc")`를 실행해 `"abc"`가 표시되는지 확인한다. disabled 상태에서는 `enabled = false`로 렌더링하고 입력 시도 후 값이 바뀌지 않는지 확인한다.

상태 아이콘 테스트도 같은 파일에 둔다. `NeveraTextFieldState.Positive`와 non-empty value에서는 `"입력이 올바릅니다"` contentDescription이 표시되어야 한다. `NeveraTextFieldState.Positive`이지만 value가 빈 문자열이면 check 아이콘이 표시되지 않아야 한다. `NeveraTextFieldState.Negative`에서는 value가 비어 있어도 `"입력을 확인하세요"`가 표시되어야 한다. 이 계약은 `NeveraBaseTextField`의 `TrailingIcons` 분기와 직접 연결된다.

`core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/textfield/NeveraEmailTextFieldTest.kt`를 만든다. 일반 입력 동작은 `NeveraTextFieldTest`와 같으므로 중복을 줄이고, 이메일 전용 컴포넌트가 `config`의 heading/placeholder/description을 유지하면서 입력 값을 전달하는지만 확인한다. 키보드 타입은 Compose UI 테스트에서 안정적으로 단언하기 어렵기 때문에 이 계획에서는 직접 검증하지 않는다. 이 결정은 구현 중 다른 방법이 발견되면 `Decision Log`에 기록하고 갱신한다.

`core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/textfield/NeveraPasswordTextFieldTest.kt`를 만든다. 첫 테스트는 기본 상태에서 `"비밀번호 표시"` 아이콘이 보이는지 확인한다. 두 번째 테스트는 이 아이콘을 클릭하면 `"비밀번호 숨기기"` 아이콘으로 바뀌는지 확인한다. 세 번째 테스트는 `enabled = false`일 때 eye 아이콘 클릭으로 상태가 바뀌지 않는지 확인한다. 네 번째 테스트는 비밀번호 값이 unfocused 상태에서 평문으로 보이지 않고, eye 아이콘 클릭 후 평문이 보이는지 확인한다. Password visual transformation의 마스킹 문자는 Compose/플랫폼에 따라 달라질 수 있으므로, 숨김 상태는 `onNodeWithText("secret").assertDoesNotExist()`로 검증하고 표시 상태는 `onNodeWithText("secret").assertIsDisplayed()`로 검증한다.


### 마일스톤 4: AppBar와 NavigationBar 테스트

이 마일스톤이 끝나면 상단/하단 공통 네비게이션 컴포넌트의 클릭 계약과 접근성 노출이 테스트로 보호된다. AppBar는 이미 contentDescription이 있으므로 바로 테스트 가능하다. NavigationBar는 접근성 설명이 없어 작은 프로덕션 변경이 필요하다.

`core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/appbar/NeveraAppBarTest.kt`를 만든다. `NeveraAppBar(title = "타이틀")`가 제목을 표시하는지 확인한다. `NeveraAppBarNavigation.Back`, `Close`, `Menu` 각각은 `"뒤로가기"`, `"닫기"`, `"메뉴"` contentDescription으로 찾고 클릭 콜백이 호출되는지 검증한다. `NeveraAppBarAction.Text(label = "완료")`는 텍스트로 찾고 클릭 콜백을 검증한다. `NeveraAppBarAction.Icons.of(...)`는 item의 contentDescription으로 찾고 클릭 콜백을 검증한다. 긴 제목 말줄임은 픽셀 검증 없이 텍스트 노드가 존재하고 앱바가 크래시 없이 렌더링되는 정도로만 확인한다.

`core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/appbar/NeveraSearchAppBarTest.kt`를 만든다. 검색 슬롯에 `Text("검색 영역")`을 넣고 표시되는지 확인한다. navigation과 action이 동시에 있을 때 둘 다 클릭 가능한지 확인한다. 이 테스트는 `NeveraSearchAppBar`가 슬롯을 잃거나 action을 가리는 회귀를 잡는 용도다.

NavigationBar는 먼저 프로덕션 모델을 보강한다. `core/designsystem/src/main/kotlin/com/anddd/nevera/core/designsystem/component/navigationbar/NeveraNavigationBarItem.kt`의 `NeveraNavigationBarItem<T>`에 `contentDescription: String` 필드를 추가한다. `NavigationBarItem`의 `Icon`에 이 값을 전달한다. 기존 호출부는 `NeveraNavigationBar` preview와 feature 모듈의 실제 사용처다. `rg "NeveraNavigationBarItem\\("`으로 모두 찾아 각 탭에 `"홈"`, `"냉장고"`, `"마이페이지"`처럼 사람이 이해할 수 있는 설명을 넘긴다. 이 변경은 테스트 가능성뿐 아니라 접근성 개선이다.

그 뒤 `core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/navigationbar/NeveraNavigationBarTest.kt`를 만든다. 테스트용 enum `Tab { HOME, FRIDGE, MY }`와 세 아이템을 렌더링한다. `"홈"` 노드를 클릭하면 `Tab.HOME`이 전달되고, `"냉장고"` 노드를 클릭하면 `Tab.FRIDGE`가 전달되는지 확인한다. 선택된 아이템이 selectedIcon을 쓰는지는 contentDescription만으로는 직접 단언하기 어려우므로 이 계획에서는 제외한다. 픽셀 또는 painter 비교가 필요한 영역은 후속 스크린샷 테스트 계획에서 다룬다.


### 마일스톤 5: Dialog, BottomSheet, Picker 계열 테스트

이 마일스톤이 끝나면 화면 위에 뜨는 조합 컴포넌트의 기본 표시와 버튼 계약이 테스트로 보호된다. 이 영역은 Material3 Dialog, ModalBottomSheet, DatePicker, LazyColumn 스크롤을 포함하므로 앞선 마일스톤보다 불안정할 수 있다. 따라서 먼저 버튼 클릭과 콜백처럼 안정적인 부분만 덮는다.

`core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/dialog/NeveraConfirmDialogTest.kt`를 만든다. title, subtitle, positive label, negative label이 표시되는지 확인한다. positive 버튼 클릭은 `onPositive`만 호출하고, negative 버튼 클릭은 `onNegative`만 호출하는지 확인한다. 바깥 클릭 dismiss는 기기 환경과 Dialog semantics에 따라 불안정할 수 있으므로 첫 구현에서는 버튼 dismiss만 검증한다. 만약 `performClick`으로 dialog 외부 dismiss를 안정적으로 만들 수 있으면 별도 테스트를 추가하고 증거를 `Surprises & Discoveries`에 기록한다.

BottomSheet 계열은 Material3 `SheetState`가 필요하다. 테스트에서 `@OptIn(ExperimentalMaterial3Api::class)`를 사용하고 `rememberModalBottomSheetState(skipPartiallyExpanded = true)` 또는 현재 프로덕션 `NeveraBottomSheet`가 요구하는 방식의 `SheetState`를 만든다. 먼저 `NeveraStepContentBottomSheetTest.kt`를 작성한다. stepIndicator, title, subtitle, CTA label이 표시되는지 확인하고, `ctaEnabled = false`이면 CTA가 비활성이고 클릭 콜백을 호출하지 않는지 확인한다. `backLabel`이 있을 때 back 버튼 클릭 콜백을 검증한다.

`NeveraIllustrationBottomSheetTest.kt`는 primary와 ghost 버튼의 조건부 표시를 검증한다. `ghostLabel`과 `onGhostClick`이 모두 있을 때 ghost 버튼이 표시되고 클릭된다. `actionLayout = Row`라도 ghost가 없으면 primary만 표시되어야 한다. illustration 슬롯에는 `Text("일러스트")`를 넣어 슬롯이 렌더링되는지 확인한다.

`NeveraDatePickerDialogTest.kt`는 초기 `selectedDate`가 있을 때 확인 버튼을 누르면 같은 `LocalDate`가 `onDateSelected`로 전달되고 `onDismiss`가 호출되는지 검증한다. `selectedDate = null`이면 확인 버튼이 disabled인지 확인한다. 날짜 셀 선택 자체는 Material3 내부 구현 의존성이 커서 첫 구현 범위에서 제외한다.

`NeveraTimePickerDialogTest.kt`는 초기값 변환 계약을 검증한다. `initialHour = 0, initialMinute = 0`에서 완료를 누르면 `(0, 0)`이 전달되어야 한다. `initialHour = 12, initialMinute = 0`에서는 `(12, 0)`, `initialHour = 18, initialMinute = 30`에서는 `(18, 30)`이 전달되어야 한다. 스크롤해서 다른 시간을 고르는 테스트는 LazyColumn snap 애니메이션과 item padding 구조 때문에 불안정할 수 있으므로, 안정화 전에는 넣지 않는다.


### 마일스톤 6: CLI 에뮬레이터 실행 경로 추가

이 마일스톤이 끝나면 Claude Code나 Codex 같은 CLI 기반 AI Agent가 Android Studio를 직접 조작하지 않고도 로컬 AVD를 사용해 `core:designsystem` Compose 계측 테스트를 실행할 수 있다. 새 파일은 `scripts/android/run-designsystem-compose-tests.sh`로 만든다. 저장소에 `scripts/android` 디렉터리가 없으면 새로 만든다.

스크립트는 멱등적으로 동작해야 한다. 먼저 `adb devices`를 확인해 이미 `device` 상태인 기기나 에뮬레이터가 있으면 새 에뮬레이터를 띄우지 않고 바로 테스트를 실행한다. 연결된 기기가 없으면 `emulator -list-avds`로 사용 가능한 AVD 목록을 읽는다. AVD가 하나도 없으면 명확한 메시지와 함께 종료한다. AVD가 하나 이상 있으면 첫 번째 AVD를 기본값으로 사용하되, 환경 변수 `NEVERA_TEST_AVD`가 있으면 그 값을 우선 사용한다. 예를 들어 `NEVERA_TEST_AVD=Pixel_8_API_35 scripts/android/run-designsystem-compose-tests.sh`처럼 실행할 수 있어야 한다.

에뮬레이터 실행 명령은 로컬 개발 환경에서 안정적인 옵션을 사용한다. 기본 형태는 `emulator -avd "$AVD_NAME" -no-snapshot-save -no-audio`다. CI가 아니라 로컬에서 눈으로 확인할 수도 있어야 하므로 첫 구현에서는 `-no-window`를 기본값으로 넣지 않는다. 헤드리스 실행이 필요하면 환경 변수 `NEVERA_HEADLESS_EMULATOR=true`일 때만 `-no-window -gpu swiftshader_indirect`를 추가한다.

스크립트는 에뮬레이터를 백그라운드로 시작한 뒤 최대 180초 동안 `adb get-state`가 `device`이고 `adb shell getprop sys.boot_completed`가 `1`인지 반복 확인한다. `adb wait-for-device`는 자체 timeout 없이 오래 멈출 수 있으므로 사용하지 않는다. 부팅이 끝나면 잠금 화면이 테스트를 가리지 않도록 `adb shell input keyevent 82`를 한 번 보낸다. 그 다음 `./gradlew :core:designsystem:connectedDebugAndroidTest`를 실행한다.

스크립트가 직접 시작한 에뮬레이터는 테스트 종료 후 `adb emu kill`로 종료한다. 다만 스크립트 시작 시점에 이미 연결되어 있던 기기나 에뮬레이터는 종료하지 않는다. 사용자가 직접 켜 둔 환경을 AI Agent가 임의로 닫지 않기 위해서다.

샌드박스 권한 때문에 `adb`나 `emulator` 실행이 실패할 수 있다. 그런 경우 AI Agent는 같은 명령을 승인 요청과 함께 재실행해야 한다. 승인을 받아도 실패하면 이 마일스톤은 코드 문제가 아니라 실행 환경 문제로 표시하고, Android Studio에서 에뮬레이터를 수동으로 켠 뒤 `./gradlew :core:designsystem:connectedDebugAndroidTest`만 실행하는 대체 경로를 사용한다.


### 마일스톤 7: 전체 실행과 불안정성 정리

이 마일스톤이 끝나면 모든 새 테스트가 실제 Android 런타임에서 통과하고, 불안정하거나 보류된 케이스가 문서에 기록된다. 저장소 루트에서 `./gradlew :core:designsystem:connectedDebugAndroidTest`를 실행한다. 에뮬레이터가 없으면 먼저 Android Studio에서 API 30 이상 에뮬레이터를 켜거나 실제 기기를 연결한다. 이 프로젝트의 `minSdk`는 30이고 `compileSdk`는 36이다.

마일스톤 6의 스크립트가 추가된 뒤에는 수동 실행보다 다음 명령을 우선 사용한다.

    scripts/android/run-designsystem-compose-tests.sh

이 명령이 성공하면 에뮬레이터 준비와 테스트 실행이 모두 검증된 것이다. 실패하면 스크립트 출력에서 실패 위치를 확인한다. AVD 없음, ADB daemon 권한 문제, 부팅 timeout, 테스트 실패는 서로 다른 문제이므로 출력 메시지가 이 네 경우를 구분해야 한다.

테스트가 실패하면 실패한 테스트가 프로덕션 버그를 드러내는지, 테스트가 불안정한 노드 탐색을 하는지 먼저 구분한다. 프로덕션 버그라면 컴포넌트 코드를 고치고 해당 테스트를 유지한다. 노드 탐색 문제라면 사용자가 볼 수 있는 텍스트나 contentDescription으로 찾을 수 있는지 먼저 바꾸고, 그것도 어렵다면 최소한의 `Modifier.testTag`를 테스트에서 주입할 수 있는 구조로 해결한다. 프로덕션 코드에 하드코딩된 testTag를 추가하는 것은 마지막 선택지다.


## Concrete Steps


모든 명령은 저장소 루트에서 실행한다.

현재 모듈이 Compose 테스트 의존성을 갖는지 확인한다.

    ./gradlew :core:designsystem:dependencies --configuration debugAndroidTestRuntimeClasspath

예상 관찰은 출력 어딘가에 `androidx.compose.ui:ui-test-junit4`가 보이는 것이다. 이미 `ComposeConfig.kt`에 의존성이 있으므로 추가 작업은 없어야 한다.

마일스톤 1 이후 첫 컴파일을 확인한다.

    ./gradlew :core:designsystem:compileDebugAndroidTestKotlin

예상 관찰은 마지막에 `BUILD SUCCESSFUL`이다. 이 명령은 에뮬레이터 없이도 androidTest Kotlin 컴파일 문제를 잡는다.

연결된 기기나 에뮬레이터가 있는지 확인한다.

    adb devices

예상 관찰은 `device` 상태의 항목이 하나 이상 있는 것이다. 항목이 없으면 마일스톤 6의 CLI 에뮬레이터 경로를 사용한다. 우선 사용 가능한 AVD 목록을 확인한다.

    emulator -list-avds

예상 관찰은 한 줄에 하나씩 AVD 이름이 출력되는 것이다. 아무것도 출력되지 않으면 Android Studio Device Manager에서 API 30 이상 AVD를 만든 뒤 다시 실행한다. AVD가 있으면 다음 형태로 에뮬레이터를 CLI에서 실행할 수 있다. `<AVD_NAME>`은 `emulator -list-avds` 출력 중 하나로 바꾼다.

    emulator -avd <AVD_NAME> -no-snapshot-save -no-audio

위 명령은 foreground에서 계속 실행되므로, AI Agent가 자동으로 테스트까지 이어가려면 별도 터미널 세션이나 백그라운드 실행이 필요하다. 그래서 마일스톤 6에서는 다음 스크립트를 추가한다.

    scripts/android/run-designsystem-compose-tests.sh

스크립트 추가 후에는 수동으로 `emulator`를 띄우는 대신 이 명령 하나를 우선 실행한다.

    scripts/android/run-designsystem-compose-tests.sh

특정 AVD를 지정하려면 다음처럼 실행한다.

    NEVERA_TEST_AVD=Pixel_8_API_35 scripts/android/run-designsystem-compose-tests.sh

헤드리스 실행이 필요하면 다음처럼 실행한다. 헤드리스는 화면 창을 띄우지 않는 방식이다.

    NEVERA_HEADLESS_EMULATOR=true NEVERA_TEST_AVD=Pixel_8_API_35 scripts/android/run-designsystem-compose-tests.sh

마일스톤별 테스트를 실행한다.

    ./gradlew :core:designsystem:connectedDebugAndroidTest

성공하면 마지막에 `BUILD SUCCESSFUL`이 보이고, 상세 리포트는 `core/designsystem/build/reports/androidTests/connected/debug/index.html` 아래에 생성된다. 특정 테스트만 빠르게 확인하려면 다음처럼 instrumentation runner arguments를 사용할 수 있다.

    ./gradlew :core:designsystem:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.anddd.nevera.core.designsystem.component.stepper.NeveraQuantityStepperTest

이 프로젝트의 Gradle/AGP 버전에서 위 `-Pandroid.testInstrumentationRunnerArguments.class` 전달이 동작하지 않으면 전체 `connectedDebugAndroidTest`를 실행한다. 실패 메시지에 테스트 클래스명이 나오므로 원인 추적은 가능하다.

CLI 에뮬레이터 스크립트는 내부적으로 다음 순서를 수행해야 한다. 이 순서는 스크립트 구현자가 빠뜨리면 안 되는 계약이다.

    1. `adb devices`로 이미 연결된 `device` 상태의 대상이 있는지 확인한다.
    2. 대상이 있으면 새 에뮬레이터를 시작하지 않는다.
    3. 대상이 없으면 `emulator -list-avds`로 AVD 목록을 읽는다.
    4. `NEVERA_TEST_AVD`가 있으면 그 AVD를 사용하고, 없으면 첫 번째 AVD를 사용한다.
    5. 선택한 AVD를 `emulator -avd "$AVD_NAME" -no-snapshot-save -no-audio`로 백그라운드 실행한다.
    6. 최대 180초 동안 `adb get-state`가 `device`이고 `adb shell getprop sys.boot_completed`가 `1`이 될 때까지 기다린다.
    7. ADB에 기기가 나타나지 않아도 같은 timeout 안에서 실패해야 하며, timeout 없는 `adb wait-for-device`는 사용하지 않는다.
    8. `adb shell input keyevent 82`로 잠금 화면을 해제한다.
    9. `./gradlew :core:designsystem:connectedDebugAndroidTest`를 실행한다.
    10. 스크립트가 직접 시작한 에뮬레이터만 `adb emu kill`로 종료한다.


## Validation and Acceptance


이 계획의 최종 수용 기준은 다음과 같다.

`./gradlew :core:designsystem:compileDebugAndroidTestKotlin`이 `BUILD SUCCESSFUL`로 끝난다. 이 기준은 테스트 코드가 컴파일되고, 필요한 Compose 테스트 API와 리소스를 모두 찾을 수 있음을 증명한다.

Android 기기나 에뮬레이터가 연결된 상태에서 `./gradlew :core:designsystem:connectedDebugAndroidTest`가 `BUILD SUCCESSFUL`로 끝난다. 이 기준은 새 Compose 테스트가 실제 Android 런타임에서 통과함을 증명한다.

마일스톤 6 이후에는 `scripts/android/run-designsystem-compose-tests.sh`가 `BUILD SUCCESSFUL`로 끝난다. 이 기준은 AI Agent가 Android Studio UI 없이도 AVD를 준비하고, 에뮬레이터 부팅 완료를 기다리고, 같은 `connectedDebugAndroidTest`를 실행할 수 있음을 증명한다. 이미 연결된 기기가 있는 경우에는 스크립트가 새 에뮬레이터를 띄우지 않고 그 기기를 재사용해야 한다. 스크립트가 직접 시작한 에뮬레이터가 있다면 테스트 종료 후 닫혀야 한다.

샘플 테스트 `ExampleInstrumentedTest.kt`와 `ExampleUnitTest.kt`가 제거되어도 `core:designsystem` 테스트 리포트에는 실제 컴포넌트 테스트가 남아 있다.

다음 테스트 파일이 존재하고, 각 파일은 최소 한 개 이상의 의미 있는 테스트를 포함한다.

    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/button/NeveraButtonTest.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/button/NeveraIconButtonTest.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/stepper/NeveraQuantityStepperTest.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/toggle/NeveraSwitchTest.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/textfield/NeveraTextFieldTest.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/textfield/NeveraEmailTextFieldTest.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/textfield/NeveraPasswordTextFieldTest.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/appbar/NeveraAppBarTest.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/appbar/NeveraSearchAppBarTest.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/navigationbar/NeveraNavigationBarTest.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/dialog/NeveraConfirmDialogTest.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/bottomsheet/NeveraStepContentBottomSheetTest.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/bottomsheet/NeveraIllustrationBottomSheetTest.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/datepicker/NeveraDatePickerDialogTest.kt
    core/designsystem/src/androidTest/kotlin/com/anddd/nevera/core/designsystem/component/timepicker/NeveraTimePickerDialogTest.kt

각 테스트는 단순히 렌더링만 확인하지 않고 적어도 하나 이상의 동작 계약을 검증해야 한다. 예를 들어 버튼 테스트는 클릭 콜백을 확인하고, TextField 테스트는 입력 값 전달을 확인하고, Dialog 테스트는 버튼 클릭이 올바른 콜백으로 이어지는지 확인한다.


## Idempotence and Recovery


이 계획의 작업은 대부분 새 테스트 파일 추가와 작은 접근성 보강이므로 여러 번 실행해도 안전하다. 같은 파일을 다시 편집할 때는 기존 테스트를 지우지 말고 실패 원인에 맞춰 케이스를 수정하거나 새 케이스를 추가한다.

계측 테스트가 기기 문제로 실패하면 먼저 `adb devices`로 연결 상태를 확인한다. `INSTALL_FAILED`나 기기 오프라인 오류는 코드 문제가 아니므로 에뮬레이터를 재시작한 뒤 같은 명령을 다시 실행한다. 특정 테스트만 실패하고 실패 메시지가 `No node found`라면 UI가 깨진 것인지, 테스트가 잘못된 노드를 찾는 것인지 구분한다. 사용자가 볼 수 있는 텍스트나 contentDescription이 실제로 렌더링되는지 `printToLog`를 일시적으로 사용해 확인할 수 있다. 이 임시 로그 코드는 최종 커밋 전에 제거한다.

CLI 에뮬레이터 스크립트는 반복 실행해도 안전해야 한다. 이미 기기가 연결되어 있으면 새 에뮬레이터를 추가로 띄우지 않는다. 스크립트가 에뮬레이터를 직접 시작한 경우에만 종료한다. 사용자가 Android Studio에서 켜 둔 에뮬레이터나 실제 기기는 스크립트가 닫지 않는다.

`adb devices`가 `could not install *smartsocket* listener`, `Operation not permitted`, `cannot connect to daemon` 같은 메시지로 실패하면 샌드박스 권한 문제일 수 있다. AI Agent는 같은 명령을 권한 승인과 함께 재실행한다. 승인이 불가능한 환경이라면 사용자가 Android Studio에서 에뮬레이터를 직접 켠 뒤, AI Agent는 `./gradlew :core:designsystem:connectedDebugAndroidTest`만 실행한다. 이 대체 경로를 사용했다면 `Outcomes & Retrospective`에 기록한다.

`emulator -list-avds`가 비어 있으면 AVD가 없다는 뜻이다. 이 경우 스크립트는 실패해야 하며, 임의로 system image를 다운로드하거나 AVD를 생성하지 않는다. AVD 생성은 Android SDK 설치 상태와 라이선스 동의가 얽힌 별도 작업이므로 이 계획의 자동 작업 범위 밖이다. 필요하면 후속 ExecPlan에서 `sdkmanager`와 `avdmanager`를 이용한 AVD 생성까지 다룬다.

NavigationBar의 `contentDescription` 필드 추가처럼 공개 모델을 바꾸는 작업은 컴파일 오류로 모든 호출부가 드러난다. `rg "NeveraNavigationBarItem\\("`로 호출부를 직접 찾고, 컴파일 오류가 남지 않을 때까지 설명 문자열을 채운다. 이 변경은 파괴적 마이그레이션이 아니며, 모든 호출부가 같은 저장소 안에 있으므로 Gradle 컴파일로 회복 가능하다.

테스트가 불안정하면 바로 삭제하지 않는다. 먼저 안정적인 관찰 대상으로 바꿀 수 있는지 본다. 그래도 불안정하면 해당 케이스를 보류하고 `Surprises & Discoveries`에 이유와 실패 증거를 기록한다.


## Artifacts and Notes


공통 헬퍼의 형태는 다음과 같다. 실제 구현 시 import는 IDE나 컴파일러가 요구하는 대로 정리한다.

    package com.anddd.nevera.core.designsystem.test

    import androidx.compose.runtime.Composable
    import androidx.compose.ui.test.junit4.ComposeContentTestRule
    import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

    fun ComposeContentTestRule.setNeveraContent(content: @Composable () -> Unit) {
        setContent {
            NeveraTheme {
                content()
            }
        }
    }

Stepper 테스트의 핵심 형태는 다음과 같다.

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `최솟값에서는 감소 버튼이 비활성이다`() {
        composeRule.setNeveraContent {
            NeveraQuantityStepper(
                quantity = 1,
                onDecrease = {},
                onIncrease = {},
            )
        }

        composeRule
            .onNodeWithContentDescription("수량 1개 감소")
            .assertIsNotEnabled()
    }

Switch 테스트는 contentDescription이 없으므로 테스트에서 modifier를 주입하는 형태를 우선 사용한다.

    NeveraSwitch(
        checked = false,
        onCheckedChange = { changedValue = it },
        modifier = Modifier.testTag("switch"),
    )

    composeRule.onNodeWithTag("switch").performClick()
    assertThat(changedValue).isTrue()

이 예시는 AssertJ를 쓰고 있지만 `core:designsystem`의 androidTest classpath에 AssertJ가 없다. 실제 테스트에서는 `org.junit.Assert.assertTrue`, `assertFalse`, `assertEquals`를 사용하거나, AssertJ를 추가할 명확한 이유가 있을 때만 Gradle 의존성을 추가한다. 첫 구현은 새 의존성 없이 JUnit assertion으로 작성한다.

CLI 에뮬레이터 실행 스크립트의 형태는 다음과 같다. 실제 구현은 이 흐름을 유지하되, zsh/bash 호환성과 에러 메시지를 저장소 스타일에 맞게 정리한다.

> 아래 스케치는 계획 시점 초안이다. 최종 구현은 `scripts/android/run-designsystem-compose-tests.sh`가 기준이며 다음 세 가지가 다르다.
> 기기 serial을 캡처해 모든 `adb` 호출과 Gradle 실행에 고정하고(다중 기기 대응), 에뮬레이터 로그를 예측 가능한
> `/tmp` 경로 대신 `mktemp` 파일에 남기며, 부팅 대기는 이 스크립트가 직접 띄운 에뮬레이터만 대상으로 한다.

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


## Interfaces and Dependencies


사용할 주요 테스트 API는 AndroidX Compose UI Test다. `createComposeRule()`은 테스트마다 Compose 콘텐츠를 띄울 수 있는 rule을 만든다. `onNodeWithText`, `onNodeWithContentDescription`, `onNodeWithTag`는 테스트 대상 노드를 찾는다. `performClick`, `performTextInput`은 사용자 행동을 흉내 낸다. `assertIsDisplayed`, `assertIsEnabled`, `assertIsNotEnabled`, `assertDoesNotExist`, `assertIsOn`, `assertIsOff`는 관찰 가능한 상태를 검증한다.

새 테스트 파일은 JUnit4를 사용한다.

    import androidx.compose.ui.test.junit4.createComposeRule
    import org.junit.Rule
    import org.junit.Test

    @get:Rule
    val composeRule = createComposeRule()

기존 의존성은 다음 파일에서 제공된다.

    build-logic/src/main/kotlin/ComposeConfig.kt

여기에는 Compose BOM, `androidx.compose.ui:ui`, `androidx.compose.material3:material3`, `androidx.compose.ui:ui-test-junit4`, `androidx.compose.ui:ui-test-manifest`가 포함되어 있다. 따라서 이 계획의 기본 경로에서는 `gradle/libs.versions.toml`이나 `core/designsystem/build.gradle.kts`를 변경하지 않는다.

CLI 에뮬레이터 실행 경로는 Android SDK의 표준 도구인 `adb`와 `emulator`에 의존한다. 이 도구들은 보통 `$ANDROID_HOME/platform-tools/adb`와 `$ANDROID_HOME/emulator/emulator`에 있다. 오래된 SDK에는 `$ANDROID_HOME/tools/emulator`도 있을 수 있지만, 이 경로의 emulator는 최신 SDK 구조에서 상대 경로를 잘못 해석할 수 있다. 따라서 스크립트는 `PATH`보다 `$ANDROID_HOME/emulator/emulator`를 우선 사용한다. 발견되지 않으면 명확한 메시지로 실패하고, 실행자는 Android Studio 또는 shell profile에서 Android SDK 경로를 PATH에 추가한다. 이 계획은 `sdkmanager`나 `avdmanager`로 새 system image를 설치하지 않는다.

프로덕션 코드에서 변경이 예상되는 안정적인 인터페이스는 하나다.

    data class NeveraNavigationBarItem<T>(
        val tag: T,
        val selectedIcon: Painter,
        val unselectedIcon: Painter,
        val selected: Boolean,
        val contentDescription: String,
    )

`NavigationBarItem`은 이 값을 `Icon(contentDescription = item.contentDescription, ...)`에 전달한다. 이 변경 후 feature 모듈에서 `NeveraNavigationBarItem`을 생성하는 모든 곳에 설명 문자열을 추가해야 한다. 문자열은 테스트 전용이 아니라 접근성 사용자에게 읽히는 실제 설명이어야 한다.
