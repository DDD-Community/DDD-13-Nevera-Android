# 단위 테스트 기반 구축: Domain → Data → Presentation 3단계 도입

이 ExecPlan은 살아있는 문서다. `Progress`, `Surprises & Discoveries`, `Decision Log`, `Outcomes & Retrospective` 섹션은 작업이 진행되는 동안 반드시 최신 상태로 유지해야 한다.

이 문서는 저장소 루트의 `PLANS.md`에 정의된 규칙에 따라 작성되고 유지되어야 한다.


## Purpose / Big Picture


Nevera Android 저장소에는 지금 사실상 단위 테스트가 없다. 각 모듈의 `src/test` 디렉터리는 존재하지만 대부분 Android Studio가 모듈 생성 시 만들어 준 `ExampleUnitTest.kt`(항상 통과하는 껍데기)만 들어 있다. 의미 있는 테스트는 `domain`에 2개, `data`에 1개, `infra/notification`에 2개, `infra/permission`에 1개(그 밖에 에뮬레이터가 필요한 계측 테스트 1개), 그리고 detekt 커스텀 규칙 모듈에 9개가 전부다.

이 상태에서 코드를 고치면 "이 변경이 다른 곳을 망가뜨리지 않았는가"를 확인할 방법이 앱을 직접 실행해 손으로 눌러 보는 것밖에 없다. 특히 이 저장소에는 손으로 확인하기 매우 어려운 종류의 로직이 두 군데 몰려 있다. 하나는 서버 응답의 숫자 코드를 도메인 에러로 바꾸는 매퍼들이고(예: `data/src/main/kotlin/com/anddd/nevera/data/mapper/error/LoginErrorMapper.kt`에서 서버 코드 `2008`을 `LoginError.InvalidCredentials`로 변환), 다른 하나는 메모리 안에 목록 캐시를 들고 있다가 편집·처리 결과에 따라 그 캐시를 조작하는 저장소 구현체들이다(`data/src/main/kotlin/com/anddd/nevera/data/repository/IngredientRepositoryImpl.kt`). 두 곳 모두 틀려도 컴파일은 통과하고, 증상은 한참 뒤 엉뚱한 화면에서 나타난다.

이 ExecPlan을 완료하면 다음 세 가지가 가능해진다.

첫째, 저장소 루트에서 명령 하나를 실행하면 도메인 규칙·데이터 변환·화면 상태 전이가 한 번에 검증된다. 완료 시점의 명령은 다음과 같고, 마지막 줄에 `BUILD SUCCESSFUL`이 출력되어야 한다.

    ./gradlew testDebugUnitTest :domain:test :core:common:test :quality:detekt-rules:test

둘째, 이 검증이 GitHub Actions CI에서 Pull Request마다 자동으로 돌아간다. 지금은 순수 Kotlin 모듈인 `domain`과 `core:common`의 테스트가 **CI에서 한 번도 실행된 적이 없다**(근거는 `Surprises & Discoveries` 첫 항목). 이 계획의 첫 마일스톤이 그 구멍을 막는다.

셋째, 코드를 읽어야만 알 수 있던 계약이 테스트 이름으로 문서화된다. 예를 들어 `domain/src/main/kotlin/com/anddd/nevera/domain/usecase/ingredient/ProcessIngredientUseCase.kt`는 재료 처리에 성공했을 때만 홈 요약과 처리 목록을 다시 불러오는데, 지금은 그 "성공했을 때만"이 코드의 `if` 문에만 존재한다. 계획 완료 후에는 `처리에 실패하면 홈 요약을 다시 불러오지 않는다`라는 이름의 테스트가 그 계약을 지킨다.

이 계획은 단위 테스트(JVM에서 실행되며 에뮬레이터가 필요 없는 테스트)만 다룬다. Compose UI 테스트나 계측 테스트(`src/androidTest`, 에뮬레이터 필요)는 범위 밖이며, 그 이유는 `Decision Log`에 기록되어 있다.


## Progress


- [x] (2026-07-22) 사전 조사 완료 — 모듈별 테스트 현황, CI 실행 범위, 레이어별 로직 밀도, `orbit-test` 라이브러리 존재 여부와 공개 API를 확인했다. 결과는 `Surprises & Discoveries`와 `Context and Orientation`에 기록되어 있다.
- [x] (2026-07-22) 마일스톤 0: `.github/workflows/ci.yml`의 테스트 스텝에 `:domain:test`와 `:core:common:test`를 추가했다. 로컬에서 새 명령이 `BUILD SUCCESSFUL`로 끝나고 `ValidatePasswordUseCaseTest` 12개가 실제로 실행됨을 테스트 결과 XML로 확인했다. 이어서 단언 하나를 일부러 틀리게 만들어 같은 명령이 `BUILD FAILED`가 되는 것까지 확인한 뒤 되돌렸다.
- [x] (2026-07-22) 마일스톤 1: `ResolveDeeplinkUseCaseTest` 7개, `ValidateEmailUseCaseTest` 10개를 추가했다. `./gradlew :domain:test`가 통과하며 도메인 모듈의 테스트는 총 36개가 되었다. `ResolveDeeplinkUseCase`의 공백 식별자 검사를 일부러 제거해 해당 테스트 2개가 실패하는 것을 확인한 뒤 되돌렸다.
- [x] (2026-07-22) 마일스톤 2: `domain/src/test/kotlin/com/anddd/nevera/domain/testutil/`에 Fake 6종을 만들고 오케스트레이션 UseCase 4종에 테스트 28개를 추가했다. 도메인 모듈 테스트는 총 64개가 되었다. `ProcessIngredientUseCase`의 성공 여부 검사를 일부러 제거해 관련 테스트 2개가 실패하는 것을 확인한 뒤 되돌렸다.
- [x] (2026-07-22) 마일스톤 3: `data`에 `testImplementation(libs.coroutines.test)`를 추가하고 매퍼 테스트 120개를 작성했다. 에러 매퍼는 18종 전부를 기능 영역별 6개 파일로 묶어 덮었고, 값 매퍼는 `IngredientMapper`·`ProcessIngredientMapper`·`HomeMapper`·`OcrProgressMapper`·`FridgeIngredientMapper` 5종을 덮었다. `LoginErrorMapper`의 서버 코드와 `IngredientMapper`의 카테고리 별칭을 각각 바꿔 3개 테스트가 실패하는 것을 확인한 뒤 되돌렸다.
- [x] (2026-07-22) 마일스톤 4: `data/src/test/kotlin/com/anddd/nevera/data/testutil/FakeDataSources.kt`에 DataSource 대역 5종과 응답 생성 도우미를 만들고, `IngredientRepositoryImpl` 11개·`HomeRepositoryImpl` 7개 테스트를 작성했다. `data` 모듈 테스트는 총 147개가 되었다. `editIngredient`의 카테고리 변경 검사를 제거해 해당 테스트가 실패하는 것을 확인한 뒤 되돌렸다.
- [x] (2026-07-22) 마일스톤 5 (스파이크): `orbit-test`를 카탈로그와 `NeveraFeaturePlugin`에 추가하고 `AppInfoViewModel`로 검증했다. 결론은 "조건부 가능"이다. `init {}`에서 시작된 로딩은 원리상 검증할 수 없지만(아래 발견 참조), `test(initialState = ...)`로 상태를 주입하면 Intent → SideEffect 계약은 안정적으로 검증된다. 이 패턴으로 테스트 5개를 작성했고 전부 통과한다.
- [x] (2026-07-23) 마일스톤 6: `HomeViewModel` 17개, `FridgeViewModel` 18개, `SignupViewModel` 24개를 작성했다. `HomeViewModel`의 더 불러오기 조건, `FridgeViewModel`의 처리 비율 경계값, `SignupViewModel`의 가입 전 방어 로직을 각각 일부러 망가뜨려 해당 테스트가 실패하는 것을 확인한 뒤 되돌렸다.
- [x] (2026-07-23) 마일스톤 7: 실제 테스트가 들어간 5개 모듈(`domain`, `data`, `feature/main`, `feature/mypage`, `feature/fridge`)에서 `ExampleUnitTest.kt`를 제거했다. 아직 실제 테스트가 없는 모듈은 그대로 두었다. 전체 검증 명령이 통과하며 저장소 전체 단위 테스트는 347개다.
- [x] (2026-07-23) 후속 리팩터링 (병합 전 코드 리뷰 대응, 동작·테스트 개수 불변): (1) 에러 매퍼 테스트 6개 파일에 복제돼 있던 `httpError` 헬퍼를 `data/src/test/.../testutil/NetworkErrorFixtures.kt`로 추출했다. (2) `FakeDataSources.kt` 한 파일에 섞여 있던 DataSource 대역과 응답 빌더를 성격·도메인별로 분리했다 — 대역은 클래스별 1파일(`FakeHomeRemoteDataSource.kt` 등, 작고 항상 함께 쓰는 OCR 2종만 `FakeOcrDataSources.kt`로 묶음), 응답 빌더는 도메인별 `*ResponseFixtures.kt`. 두 작업 모두 `testutil` 패키지 안에서의 파일 재배치라 테스트 import가 바뀌지 않았고 `:data:testDebugUnitTest`와 `detekt`가 그대로 통과한다.


## Surprises & Discoveries


- 관찰: `domain`과 `core:common`의 단위 테스트는 CI에서 한 번도 실행된 적이 없다. 두 모듈은 `build-logic/src/main/kotlin/NeveraKotlinJvmPlugin.kt`를 적용하는 순수 Kotlin(JVM) 모듈이라 Gradle 테스트 태스크 이름이 `test`인데, `.github/workflows/ci.yml`의 테스트 스텝은 `./gradlew testDebugUnitTest :quality:detekt-rules:test`만 실행한다. `testDebugUnitTest`는 Android 플러그인이 만드는 태스크라 순수 Kotlin 모듈에는 존재하지 않는다. 따라서 이미 작성되어 있는 `ValidatePasswordUseCaseTest`(12개 테스트)와 `FcmTokenErrorLogMessageTest`는 Pull Request에서 검증된 적이 없다.
  증거: 저장소 루트에서 실행한 결과.

        $ ./gradlew :domain:testDebugUnitTest --dry-run
        FAILURE: Build failed with an exception.
        * What went wrong:
        Cannot locate tasks that match ':domain:testDebugUnitTest' as task
        'testDebugUnitTest' not found in project ':domain'.

- 관찰: Orbit MVI ViewModel을 테스트하는 공식 라이브러리 `org.orbit-mvi:orbit-test`는 이 프로젝트가 쓰는 버전 11.0.0으로 Maven Central에 존재하지만, `gradle/libs.versions.toml`에는 등록되어 있지 않다. 카탈로그에는 `orbit-core`, `orbit-viewmodel`, `orbit-compose` 세 개만 있다.
  증거: `https://repo1.maven.org/maven2/org/orbit-mvi/orbit-test/11.0.0/orbit-test-11.0.0.pom` 요청이 HTTP 200을 반환했고, `gradle/libs.versions.toml`의 111~113행에 세 개 좌표만 존재함을 확인했다.

- 관찰: `orbit-test` 11.0.0의 테스트 API는 "테스트 블록이 끝날 때까지 발생한 모든 상태 변경과 SideEffect를 소비하지 않으면 테스트가 실패한다"는 규칙을 갖는다. 라이브러리 소스의 `test` 함수 문서 주석에 "During a test, all of the emitted states and side effects must be consumed - otherwise the test fails"라고 명시되어 있다. 이 규칙은 이 프로젝트의 ViewModel과 정면으로 충돌할 위험이 있다. `feature/main/src/main/kotlin/com/anddd/nevera/feature/main/home/HomeViewModel.kt`의 `init {}` 블록은 생성 즉시 다섯 개의 작업(알림 뱃지 구독, 홈 요약 구독, 구조 재료 구독, 폐기 재료 구독, 초기 로드)을 시작하므로, 테스트가 의도한 것 외에 예측하기 어려운 수의 상태 변경이 흘러나온다.
  증거: `orbit-test-jvm-11.0.0-sources.jar`의 `org/orbitmvi/orbit/test/Test.kt` 주석, 그리고 같은 jar의 `OrbitTestContext.kt`가 제공하는 소비용 함수 목록(`expectState`, `expectSideEffect`, `awaitState`, `awaitSideEffect`, `skipItems`, `expectNoItems`, `cancelAndIgnoreRemainingItems`).

- 관찰: `orbit-test`가 제공하는 `runOnCreate()` 함수는 이 프로젝트의 ViewModel에 효과가 없다. 이 함수는 Orbit 컨테이너를 만들 때 `container(initialState, onCreate = { ... })` 형태로 넘긴 람다를 실행하는데, `core/mvi/src/main/kotlin/com/anddd/nevera/core/mvi/NeveraViewModel.kt`는 `onCreate` 인자 없이 컨테이너를 만들고 각 feature ViewModel이 Kotlin의 `init {}` 블록에서 초기 로딩을 시작한다. `init {}`은 Orbit이 관여하지 않는 순수 Kotlin 생성자 코드이므로 `runOnCreate()`로 지연시킬 수 없다.
  증거: `NeveraViewModel.kt` 29~36행의 `container<STATE, SIDE_EFFECT>(initialState = initialState, buildSettings = { ... })` 호출에 `onCreate` 인자가 없음. `HomeViewModel.kt` 56~62행이 `init { observeBadge(); observeSummary(); ... }` 형태임.

- 관찰: `orbit-test`는 JUnit 4를 런타임 스코프 의존성으로 함께 가져온다. 이 프로젝트는 JUnit 5(`build-logic/src/main/kotlin/NeveraTestUnitPlugin.kt`가 `useJUnitPlatform()`을 설정)를 쓰므로, JUnit 4가 클래스패스에 있어도 JUnit Platform은 Vintage 엔진이 없으면 JUnit 4 테스트를 발견하지 않는다. 따라서 충돌은 나지 않지만, 클래스패스에 예상치 못한 라이브러리가 늘어난다는 점은 알고 있어야 한다.
  증거: `orbit-test-jvm-11.0.0.pom`의 의존성 목록에 `kotlinx-coroutines-test-jvm`(compile), `orbit-core-jvm`(compile), `turbine-jvm`(runtime), `kotlin-test`(runtime), `junit`(runtime)이 들어 있다.

- 관찰: `data` 모듈의 저장소 구현체를 테스트할 때 협력 객체를 흉내 낼 필요가 거의 없다. `core/network/src/main/kotlin/com/anddd/nevera/core/network/auth/ApiCallExecutor.kt`는 인터페이스가 아니라 `Gson` 하나만 받는 구체 클래스이고, 하는 일은 응답 객체를 검사해 성공/실패로 나누는 결정적 변환뿐이다. 따라서 테스트에서 `ApiCallExecutor(Gson())`을 그대로 만들어 쓰면 실제 프로덕션 경로가 그대로 검증된다.
  증거: `ApiCallExecutor.kt` 23행의 `class ApiCallExecutor @Inject constructor(private val gson: Gson)` 선언과, 25~93행이 네트워크 호출 없이 전달받은 람다의 결과만 검사하는 구조임.

- 관찰: `data` 모듈의 DataSource 인터페이스와 저장소 구현체는 대부분 `internal`로 선언되어 있지만, 같은 Gradle 모듈의 `src/test`에서는 접근할 수 있다. Kotlin Gradle 플러그인이 테스트 컴파일을 메인 컴파일의 "연관 컴파일(associated compilation)"로 설정하기 때문이다. 별도 조치가 필요 없다.
  증거: `data/src/main/kotlin/com/anddd/nevera/data/datasource/HomeRemoteDataSource.kt`가 `internal interface`이고, 같은 모듈의 기존 테스트 `data/src/test/kotlin/com/anddd/nevera/data/datasource/CryptoHelperTest.kt`가 이미 존재한다.

- 관찰: `orbit-test`의 `test()`는 검증용 컨테이너를 **새로 만들어 기존 컨테이너와 바꿔치기한다**. 따라서 그 호출 이전에 발행된 intent는 바꿔치기 전 컨테이너로 가서 테스트가 관찰하는 쪽에서는 재현되지 않는다. 이 프로젝트의 ViewModel은 Kotlin `init {}` 블록에서 초기 로딩을 시작하므로(`AppInfoViewModel`은 `loadAppInfo()`, `HomeViewModel`은 다섯 가지), 그 초기 로딩 과정 자체는 `orbit-test`로 검증할 수 없다. 계획 수립 단계에서 위험으로 지목했던 것이 정확히 사실로 확인되었다.
  증거: `orbit-core-jvm-11.0.0-sources.jar`의 `TestContainerDecorator.test()`가 `RealContainer`를 새로 만들어 `delegate.compareAndSet(expectedValue = actual, newValue = testDelegate)`로 교체한다. 실험으로도 확인했다. `test()` 안에서 첫 항목을 기다리면 로딩된 상태가 아니라 원본 초기 상태가 나온다.

        DIAG vm-test item1 = StateItem(value=AppInfoUiState(isLoading=false,
            appInfo=AppInfoUiModel(termsUrl=, privacyPolicyUrl=, versionName=)))

  반면 같은 ViewModel을 `test()` 없이 관찰하면 `init {}` 로딩이 끝난 상태가 보인다. 즉 프로덕션 코드가 잘못된 것이 아니라 검증 도구가 다른 컨테이너를 보고 있는 것이다.

- 관찰: 위 제약에도 불구하고 `orbit-test`는 이 프로젝트의 `NeveraViewModel`에 그대로 쓸 수 있다. `test()`에 `initialState`를 넘기면 원하는 상태를 주입할 수 있고, `containerHost.handleIntent(...)`로 사용자 액션을 넣어 상태 전이와 SideEffect를 검증할 수 있다. 초기 로딩 로직 자체는 그 아래 UseCase·저장소 테스트가 이미 담당하므로 검증 공백이 크지 않다.
  증거: `feature/mypage/src/test/kotlin/com/anddd/nevera/feature/mypage/appinfo/AppInfoViewModelTest.kt`의 테스트 5개가 이 패턴으로 통과한다.

- 관찰: 원인을 좁힐 때 ViewModel이 아닌 최소 `ContainerHost`로 먼저 실험한 것이 결정적이었다. 그 실험이 통과함으로써 "`orbit-test`가 이 환경에서 아예 동작하지 않는다"는 가설이 배제되었고, 문제가 초기화 시점에 있다는 쪽으로 좁혀졌다.
  증거: 스코프만 받아 `container<Int, String>(0)`을 만드는 임시 호스트에 `test()`를 걸고 intent를 넣자 `DIAG simple host item = StateItem(value=1)`이 정상 출력되었다.

- 관찰: `Dispatchers.setMain(...)` 없이도 ViewModel 테스트는 통과한다. `androidx.lifecycle`의 `viewModelScope`가 Main 디스패처를 찾지 못하면 예외를 던지는 대신 빈 컨텍스트로 물러서기 때문이다. 다만 그 경우 `init {}` 작업이 실제 스레드풀에서 테스트와 동시에 돌아가므로, 대역이 호출 횟수를 세는 경우 그 값이 오염될 수 있다. 그래서 통과 여부와 무관하게 `setMain`을 유지하기로 했다.
  증거: `@BeforeEach`/`@AfterEach`를 제거하고 실행해도 `BUILD SUCCESSFUL`이었다.

- 관찰: 이 저장소의 커스텀 detekt 규칙 `ViewModelAccessOnlyInScreenRule`이 테스트 코드에서 오탐을 낸다. 이 규칙은 `viewModel` 이름의 호출이 `*Screen` 컴포저블 안에 있는지만 보는데, ViewModel 테스트에서 흔히 쓰는 `private fun viewModel() = ...` 팩토리 헬퍼가 이름만으로 걸린다.
  증거: 헬퍼 이름이 `viewModel()`이던 시점의 실행 결과.

        > Task :feature:mypage:detekt FAILED
        AppInfoViewModelTest.kt:62:9: viewModel 호출은 *Screen Composable에서만 허용된다.
            [ViewModelAccessOnlyInScreenRule]
        > Analysis failed with 5 weighted issues.

  헬퍼 이름을 `createViewModel()`로 바꾸자 해소되었다.

- 관찰: 이 저장소에는 이미 따라 쓸 수 있는 "손으로 쓴 가짜 구현체(Fake)" 패턴이 있다. `infra/notification/src/test/kotlin/com/anddd/nevera/infra/notification/testutil/FakeFcmTokenRepository.kt`는 인터페이스를 직접 구현하면서 호출 내역을 리스트에 기록하고(`markedTokens`, `registeredTokens`), 반환값을 생성자 인자(`registerResult`)로 바꿀 수 있게 해 두었다.
  증거: 해당 파일 전체. 같은 디렉터리에 `FakeTokenRepository.kt`, `infra/permission/src/test/.../testutil/FakePermissionChecker.kt`도 같은 형태로 존재한다.


## Decision Log


- 결정: 테스트 도입 순서를 Domain → Data → Presentation으로 한다.
  근거: 세 가지가 겹친다. 첫째, 비용이 이 순서로 커진다. `domain`은 순수 Kotlin 모듈이라 Android 프레임워크가 없고 `testImplementation(libs.coroutines.test)`가 이미 붙어 있어 오늘 당장 테스트를 쓸 수 있다. `data`는 테스트 의존성 한 줄 추가가 필요하고, `feature`는 카탈로그에 없는 라이브러리 등록과 convention plugin 수정이 필요하다. 둘째, Domain은 나머지 두 레이어가 의존하는 계약(`domain/src/main/kotlin/com/anddd/nevera/domain/repository/` 아래 인터페이스 10개)을 정의하므로, 계약을 테스트로 먼저 고정하면 뒤 레이어의 테스트를 그 계약 기준으로 쓸 수 있다. 셋째, 코드만 봐서는 의도를 알 수 없는 로직(성공 시에만 후속 호출, 로그인과 토큰 저장의 원자성)이 Domain의 UseCase에 몰려 있다.
  날짜/작성자: 2026-07-22 / Ju Hyeok

- 결정: CI 수정(마일스톤 0)을 테스트 작성보다 먼저 한다.
  근거: 이 계획의 목적은 "변경 후 사이드이펙트 확인에 드는 노력을 줄이는 것"이다. 테스트가 CI에서 자동으로 돌지 않으면 사람이 기억해서 실행해야 하므로 목적이 달성되지 않는다. 게다가 지금 이미 작성된 도메인 테스트 12개가 검증되지 않는 상태이므로, 이 구멍은 새 테스트를 쓰기 전에 막는 것이 순서상 맞다.
  날짜/작성자: 2026-07-22 / Ju Hyeok

- 결정: CI 명령을 `./gradlew check`나 `./gradlew test`로 넓히는 대신, 실행할 모듈 태스크를 명시적으로 나열한다. 즉 `./gradlew testDebugUnitTest :domain:test :core:common:test :quality:detekt-rules:test`를 쓴다.
  근거: `./gradlew test`를 Android 모듈에 실행하면 `testDebugUnitTest`와 `testReleaseUnitTest`가 모두 돌아 CI 시간이 두 배가 된다. `./gradlew check`는 lint와 detekt까지 끌고 들어와, 현재 세 스텝(`unit-test`, `lint`, `detekt`)을 분리해 각각의 성공/실패를 기록하는 `ci.yml`의 구조를 무너뜨린다. 명시적 나열은 장황하지만 현재 CI 구조를 유지하면서 정확히 필요한 것만 실행한다. 순수 Kotlin 모듈은 셋뿐이고(`domain`, `core:common`, `quality:detekt-rules`) 자주 늘어나지 않으므로 유지 부담도 작다.
  날짜/작성자: 2026-07-22 / Ju Hyeok

- 결정: 협력 객체를 흉내 낼 때 mockk 대신 손으로 쓴 Fake 클래스를 기본으로 한다.
  근거: 이 저장소에는 이미 `infra/notification`과 `infra/permission`에 Fake 패턴이 자리잡혀 있어 일관성이 유지된다. 또한 이 계획에서 검증하려는 것의 상당수가 "무엇을 몇 번 어떤 순서로 호출했는가"인데, Fake가 호출 내역을 리스트에 쌓아 두는 방식이 mockk의 `verify` 블록보다 읽기 쉽고, 프로덕션 인터페이스가 바뀌면 컴파일 에러로 즉시 드러난다는 장점이 있다. mockk는 이미 카탈로그에 있으므로 Fake로 표현하기 번거로운 경우(예: 예외를 던지는 협력자)에는 사용해도 된다.
  날짜/작성자: 2026-07-22 / Ju Hyeok

- 결정: 협력 객체 호출을 그대로 전달하기만 하는 UseCase와 저장소 구현체는 테스트하지 않는다.
  근거: `domain/src/main/kotlin/com/anddd/nevera/domain/usecase/home/GetHomeSummaryUseCase.kt`처럼 본문이 `homeRepository.loadSummary()` 한 줄인 UseCase를 테스트하면, 검증되는 것은 Fake가 설정한 값을 그대로 돌려준다는 사실뿐이라 회귀를 잡아내지 못한다. `data/src/main/kotlin/com/anddd/nevera/data/repository/AuthRepositoryImpl.kt`도 같은 이유로 제외한다. 이 구현체의 일곱 메서드는 모두 `apiCall { ... }.map(성공변환, 실패변환)` 형태이고, 그 안의 변환 함수는 마일스톤 3의 매퍼 테스트가 이미 직접 검증한다. 도메인 UseCase 약 40개 중 실제로 테스트 가치가 있는 것은 10개 안팎이며, 이 계획은 그 10개에 집중한다.
  날짜/작성자: 2026-07-22 / Ju Hyeok

- 결정: Compose UI 테스트와 계측 테스트(`src/androidTest`)는 이 계획의 범위에서 제외한다.
  근거: 계측 테스트는 에뮬레이터가 필요해 실행이 느리고, 현재 CI에는 에뮬레이터 단계가 없어 새로 만들어야 한다. 이는 "확인 노력 최소화"라는 목적과 충돌한다. 또한 이 저장소는 detekt 커스텀 규칙(`quality/detekt-rules/src/main/kotlin/com/anddd/nevera/quality/screencontent/`의 `ScreenDelegatesToContentRule`, `ScreenNoScaffoldRule`, `ViewModelAccessOnlyInScreenRule`, `ToastOutsideScreenRule`와 `mvi/rules/ContentComposableParameterRule`)로 "`*Content` 컴포저블은 `*UiState`와 콜백만 받는 순수 렌더러"임을 이미 구조적으로 강제한다. 즉 화면에 보이는 결과는 `*UiState` 값으로 결정되므로, ViewModel 테스트로 `*UiState`가 올바른지 보장하면 UI 테스트의 추가 가치가 크게 줄어든다.
  날짜/작성자: 2026-07-22 / Ju Hyeok

- 결정: Presentation 레이어는 곧바로 테스트를 작성하지 않고 스파이크 마일스톤(마일스톤 5)으로 시작한다.
  근거: `Surprises & Discoveries`에 기록했듯 `orbit-test`는 모든 상태 방출을 소비하도록 요구하는데, 이 프로젝트의 ViewModel은 `init {}`에서 여러 흐름을 동시에 시작하고 그중 일부는 무한히 이어지는 Flow 구독이다. 이 조합이 실제로 테스트 가능한지는 문서를 읽어서 판단할 수 없고 실행해 봐야 안다. PLANS.md가 권장하는 대로, 위험을 앞당겨 확인하는 작은 프로토타입을 먼저 만들고 그 결과에 따라 마일스톤 6의 형태를 결정한다.
  날짜/작성자: 2026-07-22 / Ju Hyeok

- 결정: 테스트 명명과 프레임워크는 기존 컨벤션을 그대로 계승한다. JUnit 5(`org.junit.jupiter.api.Test`)를 쓰고, 테스트 함수 이름은 백틱으로 감싼 한글 문장으로 쓴다.
  근거: `domain/src/test/kotlin/com/anddd/nevera/domain/usecase/validation/ValidatePasswordUseCaseTest.kt`가 이미 이 방식이고(예: `` `8자 미만이면 TooShort 에러를 반환한다` ``), `quality/detekt-rules`의 테스트 10개도 동일하다. 새 컨벤션을 도입할 이유가 없으며, 한글 문장 이름은 "테스트로 의도를 문서화한다"는 이 계획의 목적에 직접 기여한다.
  날짜/작성자: 2026-07-22 / Ju Hyeok

- 결정: Fake의 미사용 메서드는 계획이 정한 "안전한 기본값" 대신, 자연스러운 기본값이 없는 반환 타입에 한해 어떤 메서드가 준비되지 않았는지 알려주는 `UnsupportedOperationException`을 던진다.
  근거: 계획 수립 시점의 의도는 `TODO()`가 던지는 "An operation is not implemented" 메시지가 실패 원인을 가린다는 것이었는데, 실제로 Fake를 작성해 보니 문제는 예외 자체가 아니라 메시지의 불친절함이었다. `IngredientRepository`의 `editIngredient`는 `NeveraResult<FridgeIngredient, EditIngredientError>`를 돌려주는데, 여기에 기본값을 주려면 필드 여덟 개짜리 `FridgeIngredient` 더미를 지어내야 한다. 그렇게 만든 값이 테스트에 흘러들면 "왜 이 값이 나왔지"를 추적하는 비용이 예외보다 훨씬 크다. 빈 리스트나 `Success(Unit)`처럼 자연스러운 기본값이 있는 메서드에는 계획대로 기본값을 두었고, 그렇지 않은 곳에만 `FakeIngredientRepository.editIngredient 은 이 테스트에서 준비되지 않았다` 형태의 메시지를 가진 예외를 쓴다.
  날짜/작성자: 2026-07-22 / Ju Hyeok

- 결정: ViewModel 테스트는 `init {}` 초기 로딩을 검증 대상에서 제외하고, `test(initialState = ...)`로 상태를 주입한 뒤 Intent → 상태 전이·SideEffect만 검증한다. 마일스톤 6은 폐기하지 않고 이 형태로 진행한다.
  근거: `orbit-test`의 `test()`가 컨테이너를 새로 만들어 바꿔치기하므로 생성자에서 시작된 작업은 구조적으로 관찰할 수 없다(`Surprises & Discoveries` 참조). 이것을 해결하려면 `NeveraViewModel`이 Orbit의 `onCreate`를 쓰도록 바꿔야 하는데, 그러면 feature ViewModel 14개를 전부 손대야 한다. 계획이 정한 폐기 기준은 "가장 단순한 ViewModel에서조차 안정적으로 통과시키지 못할 때"인데, 상태를 주입하면 안정적으로 통과하므로 그 기준에는 해당하지 않는다. 게다가 초기 로딩 로직 자체는 이미 마일스톤 2~4의 UseCase·저장소 테스트가 덮고 있어, ViewModel 층에서 다시 검증하지 못하는 손실이 크지 않다.
  날짜/작성자: 2026-07-22 / Ju Hyeok

- 결정: `NeveraViewModel`을 Orbit의 `onCreate` 방식으로 바꾸는 리팩터링은 이 계획에서 하지 않고 별도 ExecPlan으로 넘긴다.
  근거: feature ViewModel 14개의 초기화 구조를 모두 바꾸는 작업이라 테스트 도입과 성격이 다르다. 두 작업을 한 계획에 섞으면 회귀가 생겼을 때 테스트가 잘못된 것인지 리팩터링이 잘못된 것인지 가릴 수 없다. 그 계획을 진행하면 `runOnCreate()`로 초기 로딩까지 검증할 수 있게 되며, 이 계획이 남긴 ViewModel 테스트가 그 리팩터링의 안전망이 된다. 즉 순서는 지금 이대로가 맞다.
  날짜/작성자: 2026-07-22 / Ju Hyeok

- 결정: 커스텀 detekt 규칙이 테스트 코드에서 오탐을 낼 때, 규칙에 테스트 소스 제외 설정을 추가하는 대신 테스트 쪽 이름을 바꾼다.
  근거: `ViewModelAccessOnlyInScreenRule`이 `viewModel()` 팩토리 헬퍼를 Compose의 `viewModel()` 호출로 오인했는데, 이는 규칙의 결함이라기보다 이름 충돌이다. 팀의 품질 게이트를 약화시키는 대가로 얻는 것이 테스트 헬퍼 이름 하나뿐이라면 교환이 맞지 않는다. `createViewModel()`이 "새 인스턴스를 만든다"는 뜻을 더 정확히 드러내기도 한다. 다만 앞으로 오탐이 반복되면 `config/detekt/detekt.yml`의 해당 규칙에 `excludes: ['**/test/**']`를 넣는 편이 나을 수 있으므로 선택지를 여기 남겨 둔다.
  날짜/작성자: 2026-07-22 / Ju Hyeok

- 결정: ViewModel 테스트 클래스는 `@BeforeEach`에서 `Dispatchers.setMain(StandardTestDispatcher())`을 설정하고 `@AfterEach`에서 되돌린다. 없어도 통과하지만 유지한다.
  근거: 설정하지 않으면 `viewModelScope`가 빈 컨텍스트로 물러서면서 `init {}` 작업이 실제 스레드풀에서 테스트와 동시에 실행된다. 지금 대상인 `AppInfoViewModel`은 무해하지만, 마일스톤 6의 대역들은 호출 횟수를 세므로 초기화가 그 값을 오염시킬 수 있다. 여덟 줄의 정형 코드로 비결정성을 없애는 편이 낫다.
  날짜/작성자: 2026-07-22 / Ju Hyeok

- 결정: 에러 매퍼 테스트는 프로덕션 파일과 1:1로 두지 않고 기능 영역별로 묶는다. `CommonErrorMapperTest`, `AuthErrorMapperTest`, `IngredientErrorMapperTest`, `WishErrorMapperTest`, `UserErrorMapperTest`, `NotificationErrorMapperTest` 여섯 개다.
  근거: 에러 매퍼는 파일 하나가 함수 하나에 열 줄 남짓이라 1:1로 두면 세 줄짜리 테스트 파일이 18개 생긴다. 반면 서버 에러 코드는 기능 단위로 함께 바뀌므로(로그인 관련 코드 체계가 바뀌면 로그인·회원가입·이메일 인증이 같이 움직인다) 기능별로 묶으면 한 파일만 열어도 영향 범위를 볼 수 있다. `NotificationTimeErrorMapper`의 알림 on/off와 알림 시각 변경이 서버 코드 3001을 공유하는 것 같은 관계도 같은 파일 안에서 드러난다. 값 매퍼는 파일마다 내용이 충분히 크므로 계획대로 1:1을 유지한다.
  날짜/작성자: 2026-07-22 / Ju Hyeok

- 결정: (후속) 여러 에러 매퍼 테스트에 복제된 `httpError` 헬퍼를 공용 픽스처 `NetworkErrorFixtures.kt`로 추출한다.
  근거: 매퍼 테스트는 앞으로도 늘어날 영역이라 파일마다 같은 2줄을 다시 붙이게 된다. 추출의 실익은 "생성 방식이 바뀌면 한 곳만 고침"이 아니다 — `NetworkError.HttpError` 생성자가 바뀌면 컴파일러가 모든 호출부를 짚어주므로 조용히 깨질 위험이 없다. 진짜 이득은 새 파일마다 붙던 보일러플레이트 제거와 더미 메시지("서버 메시지") 컨벤션의 단일 관리다. 병합 전 리뷰 지적을 받아들였고, 심각도는 정확성 위험이 없어 Minor로 판단한다.
  날짜/작성자: 2026-07-23 / Ju Hyeok

- 결정: (후속) `testutil`의 테스트 대역과 응답 빌더를 성격(종류) × 도메인 두 축으로 분리한다. 대역은 클래스별 1파일, 응답 빌더는 도메인별 `*ResponseFixtures.kt`.
  근거: 단일 `FakeDataSources.kt`에 성격이 다른 두 종류(행위 대역 + 데이터 픽스처)가 섞여 있어 파일 이름이 둘 중 하나만 광고했고, 팀 작업 시 서로 다른 기능을 건드려도 같은 파일에서 머지 충돌이 났다. 종류로 나누면 각 파일이 한 종류만 담아 스스로 이름을 정하므로 `*Doubles` 같은 뭉뚱그린 집합명사가 필요 없고, 클래스별·도메인별 파일이라 충돌도 격리된다. 응답 빌더의 소속은 "호출하는 대역(소비자)"이 아니라 "만드는 DTO(생산자)"로 정한다 — `fridgeIngredientResponse`는 재료·냉장고 두 대역이 공유해 소비자 기준으로는 소유가 안 정해지지만, `FridgeIngredientResponse`를 생산하므로 `IngredientResponseFixtures.kt`에 명확히 귀속된다. 파일명은 표준 테스트 용어 `*Fixtures`를 쓰고(기존 `NetworkErrorFixtures.kt`와 일관), 종류를 흐리는 `*TestData`/`*Doubles`는 피한다. 기능 응집을 택하고 종류 기반 SRP 순수성을 일부 포기한 것은, 팀·머지 충돌 최소화가 우선이라는 사용자 결정에 따른 의도적 트레이드오프다.
  날짜/작성자: 2026-07-23 / Ju Hyeok

- 결정: 각 모듈에 흩어져 있는 `ExampleUnitTest.kt`와 `ExampleInstrumentedTest.kt`의 처리는 마지막 마일스톤으로 미룬다.
  근거: 이 파일들은 항상 통과하므로 해롭지 않고, 지우는 작업이 다른 마일스톤과 아무 의존관계가 없다. 먼저 지우면 "테스트가 0개인 모듈"이 생겨 Gradle 테스트 리포트에서 모듈이 사라지는 등 부수적인 혼란만 생긴다. 실제 테스트가 들어간 모듈부터 순차적으로 정리하는 편이 안전하다.
  날짜/작성자: 2026-07-22 / Ju Hyeok


## Outcomes & Retrospective


### 마일스톤 0 (2026-07-22)


`.github/workflows/ci.yml`의 테스트 스텝 한 줄을 넓혀, 지금까지 CI에서 한 번도 실행되지 않던 순수 Kotlin 모듈의 테스트가 Pull Request마다 실행되도록 했다. 이로써 이미 저장소에 있던 도메인 테스트 18개(`ValidatePasswordUseCaseTest` 12개, `FcmTokenErrorLogMessageTest` 6개)가 처음으로 자동 검증 범위에 들어왔다.

계획과 다른 점은 없었다. 검증은 두 단계로 했다. 먼저 새 명령이 통과하고 `domain/build/test-results/test/`에 `ValidatePasswordUseCaseTest`가 `tests="12"`로 기록되는지 확인했고, 그다음 단언 하나를 일부러 틀리게 만들어 같은 명령이 실패하는지 확인했다. 두 번째 단계가 없으면 "명령이 통과했다"가 "테스트가 실행되었다"를 뜻하는지 알 수 없다.

배운 점 하나를 기록해 둔다. `ci.yml`의 테스트 결과 업로드 스텝은 경로가 `'**/build/reports/tests'`로 이미 와일드카드라, 순수 Kotlin 모듈이 만드는 `domain/build/reports/tests/test/`도 자동으로 포함된다. 업로드 설정은 손댈 필요가 없었다.


### 마일스톤 1 (2026-07-22)


의존성이 없는 UseCase 두 개에 테스트 17개를 추가했다. 도메인 모듈의 테스트 수는 19개에서 36개가 되었다.

`ResolveDeeplinkUseCaseTest`(7개)는 딥링크 파싱의 정상 경로 두 가지와 거부해야 하는 입력 다섯 가지를 다룬다. 계획에 없었지만 추가한 케이스가 하나 있다. `startsWith`로 접두사를 검사하므로 접두사가 문자열 중간에 있으면 거부되어야 하는데, 이 성질은 코드만 봐서는 의도인지 우연인지 알 수 없어 테스트로 고정했다.

`ValidateEmailUseCaseTest`(10개)는 `Empty`·`InvalidFormat`·`Valid` 세 결과를 모두 덮되, `InvalidFormat`은 서로 다른 이유로 걸리는 입력 여섯 가지로 나눴다. 정규식은 한 덩어리라 어느 부분이 깨졌는지 실패 메시지만으로 알기 어렵기 때문에, 이유별로 테스트를 쪼개 두면 회귀 시 원인이 바로 드러난다.

테스트가 실제로 회귀를 잡는지 확인하기 위해 `ResolveDeeplinkUseCase`의 `if (id.isNotBlank())` 조건을 제거했더니 의도한 두 테스트(`접두사만 있고 식별자가 없으면 null을 반환한다`, `식별자가 공백뿐이면 null을 반환한다`)가 정확히 실패했고, 확인 후 되돌렸다. 이 확인 절차를 이후 마일스톤에서도 계속 적용한다.


### 마일스톤 2 (2026-07-22)


Fake 6종(`FakeAuthRepository`, `FakeTokenRepository`, `FakeHomeRepository`, `FakeIngredientRepository`, `FakeFcmTokenRepository`, `FakeFcmTokenProvider`)과 UseCase 테스트 28개를 추가했다. 도메인 모듈의 테스트 수는 36개에서 64개가 되었다.

가장 큰 수확은 `SyncDeviceTokenUseCase`다. 이 UseCase는 인자로 받은 토큰의 유무, 저장된 토큰과의 일치 여부, 동기화 필요 표시, 로그인 여부라는 네 조건의 조합으로 등록 여부를 결정하는데, 조합을 하나씩 테스트로 옮기고 나니 13개가 나왔다. 코드를 읽는 것만으로 이 13가지 경로를 머릿속에서 추적하는 것은 사실상 불가능하다.

계획과 달라진 점이 하나 있고 `Decision Log`에 별도 항목으로 기록했다. 계획은 Fake의 미사용 메서드에 "안전한 기본값"을 두라고 했지만, `FridgeIngredient`처럼 자연스러운 기본값이 없는 반환 타입에는 임의의 더미 객체를 만드는 대신 어떤 메서드가 준비되지 않았는지 알려주는 예외를 던지도록 했다.

`ProcessIngredientUseCase`의 `if (result is NeveraResult.Success)` 검사를 제거하자 의도한 두 테스트가 정확히 실패했고, 확인 후 되돌렸다.


### 마일스톤 3 (2026-07-22)


매퍼 테스트 120개를 추가해 `data` 모듈의 테스트 수는 9개에서 129개가 되었다.

계획과 달라진 점이 둘 있다. 첫째, 계획은 "분기가 있는 에러 매퍼만 고른다"고 했지만 실제로 18개 파일을 모두 열어 보니 전부 서버 코드 분기를 갖고 있었고, 테스트 한 건이 두 줄이라 전부 덮는 편이 고르는 것보다 쌌다. 둘째, 파일 구성을 프로덕션 파일과 1:1로 두지 않고 기능 영역별 6개(`Common`, `Auth`, `Ingredient`, `Wish`, `User`, `Notification`)로 묶었다. 근거는 `Decision Log`에 남겼다.

가장 값진 발견은 날짜 형식이다. `IngredientMapper.toApiExpirationDate()`가 만드는 문자열이 `2026-07-22T00:00+09:00`일 것이라 예상하고 테스트를 썼는데 실제로는 `2026-07-22T00:00:00+09:00`였다. `DateTimeFormatter.ISO_OFFSET_DATE_TIME`은 `LocalTime.toString()`과 달리 0초를 생략하지 않는다. 서버로 나가는 문자열이라 이런 차이가 문제가 될 수 있는데, 이제 형식이 테스트로 고정되었다.

테스트를 쓰면서 확인한, 고쳐야 할지 판단이 필요한 동작이 두 가지 있다. 둘 다 이 계획의 원칙대로 고치지 않고 현재 동작을 그대로 고정한 뒤 여기 기록만 남긴다. 하나는 `ProcessIngredientMapper`가 알 수 없는 처리 유형 문자열을 폐기가 아닌 구조(`Consumed`)로 떨어뜨리는 것이고, 다른 하나는 `FridgeIngredientMapper`가 파싱할 수 없는 유통기한을 오늘 날짜로 대체해 화면에 잘못된 유통기한이 보이게 되는 것이다.


### 마일스톤 4 (2026-07-22)


저장소 구현체 테스트 18개를 추가해 `data` 모듈의 테스트 수는 129개에서 147개가 되었다. 이로써 이 계획이 목표로 삼았던 "사이드이펙트가 가장 예측하기 어려운 코드"가 자동 검증 범위에 들어왔다.

계획에서 가장 불확실했던 부분이 그대로 확인되었다. `ApiCallExecutor`를 대역으로 바꾸지 않고 `ApiCallExecutor(Gson())`로 실제 구현을 쓰는 방식이 문제없이 동작했다. 덕분에 "서버가 HTTP 200과 함께 본문에 에러 코드를 담아 보내면 실패로 처리된다"는 실제 경로가 저장소 테스트에서 함께 검증된다. 계획이 걱정했던 `Gson` 의존성 문제도 없었다. `retrofit-converter-gson`의 전이 의존성으로 테스트 컴파일 경로에 이미 들어와 있어 빌드 파일을 추가로 손대지 않았다.

`internal` 가시성도 예상대로 문제가 되지 않았다. `IngredientRepositoryImpl`과 DataSource 인터페이스가 모두 `internal`이지만 같은 모듈의 `src/test`에서 그대로 보인다.

한 가지 기법을 기록해 둔다. `observeHomeSummary()`와 `observeRescuedIngredients()`는 `filterNotNull()`이 걸려 있어 값이 들어오기 전에는 아무것도 방출하지 않는다. 그래서 "실패했을 때 방출하지 않는다"를 검증하려면 `first()`를 그냥 부를 수 없고 영원히 멈춘다. `withTimeoutOrNull`로 감싸 방출이 없음을 `null`로 확인하는 방식을 썼는데, `runTest`의 가상 시간 덕분에 실제로 기다리지 않고 즉시 끝난다.


### 마일스톤 5 (2026-07-22)


스파이크의 목적은 테스트를 많이 만드는 것이 아니라 "이 프로젝트의 ViewModel을 `orbit-test`로 검증할 수 있는가"에 실행 결과로 답하는 것이었다. 답은 "조건부로 가능"이다.

계획이 던진 세 질문에 모두 답했다. 첫째, `init {}` 블록은 실제로 문제였고 원인은 예상보다 근본적이었다. 지연이나 타이밍 문제가 아니라 `test()`가 컨테이너를 통째로 바꿔치기하기 때문에 생성자에서 시작된 작업이 구조적으로 관찰되지 않는다. 둘째, 초기 상태 자동 확인(`autoCheckInitialState`)은 끄지 않아도 됐다. 실패의 원인은 그것이 아니라 초기 로딩 상태가 아예 오지 않는 것이었다. 셋째, `orbit-test`가 전이로 끌고 오는 JUnit 4는 아무 문제를 일으키지 않았다.

원인 규명 과정에서 배운 방법을 남겨 둔다. 처음에는 실패 메시지가 `No value produced in 3s`뿐이라 원인을 짐작만 할 수 있었다. 진전을 만든 것은 ViewModel이 아닌 최소 `ContainerHost`를 만들어 같은 검증을 해 본 것이다. 그것이 통과하면서 "라이브러리가 이 환경에서 동작하지 않는다"는 가설이 배제되었고, 다음으로 `test()`를 건 경우와 걸지 않은 경우를 비교하자 원인이 드러났다. 라이브러리 소스를 내려받아 `TestContainerDecorator`를 읽은 것이 마지막 확증이 되었다.

결과적으로 마일스톤 6은 폐기하지 않는다. 다만 검증 범위가 계획보다 좁아진다. 초기 로딩 시나리오는 다루지 못하고, 상태를 주입한 뒤의 Intent → 상태 전이·SideEffect만 다룬다. 이 제약과 그 근거는 `Decision Log`에 두 항목으로 남겼고, 초기 로딩까지 검증하려면 필요한 후속 작업(`NeveraViewModel`의 `onCreate` 전환)도 별도 계획으로 분리해 기록했다.

예상하지 못한 걸림돌이 하나 더 있었다. 이 저장소의 커스텀 detekt 규칙이 테스트 코드의 `viewModel()` 팩토리 헬퍼를 Compose 호출로 오인해 빌드를 실패시켰다. 규칙을 완화하는 대신 헬퍼 이름을 `createViewModel()`로 바꿔 해소했다.


### 마일스톤 6 (2026-07-23)


ViewModel 테스트 59개를 추가했다(`HomeViewModel` 17, `FridgeViewModel` 18, `SignupViewModel` 24).

스파이크에서 확정한 형태가 세 ViewModel 모두에 그대로 통했다. 다만 스파이크가 놓친 사실이 하나 드러났다. `init` 블록의 작업이 전부 유실되는 것은 아니다. 생성 시점에 한 번 발행되고 끝나는 intent는 유실되지만, `SignupViewModel`처럼 흐름을 계속 구독하는 경우 그 구독은 살아 있어서 `test()` 이후의 방출이 검증용 컨테이너로 들어온다. 그래서 예상하지 못한 상태 변경이 하나 더 생겨 테스트가 실패했다. 해결은 초기 상태를 실제와 일관되게 만드는 것이었다. 상태 흐름은 같은 값을 다시 방출하지 않으므로, 주입하는 초기 상태에 파생 필드까지 미리 채워 두면 구독이 만들어 내는 첫 변경이 무시된다.

또 하나 배운 것은 "상태가 바뀌지 않는 것"도 검증 대상이라는 점이다. 회원가입에서 비밀번호 확인란이 비어 있을 때, 검증 실패가 만드는 상태는 이미 화면에 반영된 값과 같아 새 방출이 없다. 처음에는 상태 변경을 기대하는 테스트를 썼다가 실패했고, 관찰 가능한 결과가 "가입 요청이 나가지 않는다"뿐임을 확인해 그렇게 고쳤다.

세 ViewModel에서 가장 값진 검증은 각각 다르다. `HomeViewModel`은 목록 더 불러오기의 세 가지 건너뛰기 조건, `FridgeViewModel`은 화면의 0~1 실수를 서버가 허용하는 네 단계로 접는 경계값, `SignupViewModel`은 이메일 미인증·비밀번호 조건 미달·확인란 불일치라는 세 가지 가입 차단 조건이다. 모두 코드를 읽어야만 알 수 있었고 실수로 깨뜨려도 컴파일이 통과하던 것들이다.


### 마일스톤 7과 계획 전체 회고 (2026-07-23)


계획의 모든 마일스톤을 마쳤다. 저장소 전체 단위 테스트는 시작 시점의 108개에서 347개가 되었고, 이 계획으로 새로 쓴 것은 247개다. 레이어별로는 도메인 45개, 데이터 138개, 프레젠테이션 64개다.

`Purpose / Big Picture`가 약속한 세 가지와 실제 결과를 비교하면 다음과 같다.

첫째, 명령 하나로 전 레이어를 검증하는 것은 달성했다. 저장소 루트에서 `./gradlew testDebugUnitTest :domain:test :core:common:test :quality:detekt-rules:test`를 실행하면 347개가 모두 실행되고 `BUILD SUCCESSFUL`이 출력된다.

둘째, CI 자동화도 달성했다. 이 명령이 `.github/workflows/ci.yml`의 테스트 스텝과 정확히 같으므로 로컬에서 통과하면 CI에서도 통과한다. 시작 시점에 순수 Kotlin 모듈의 테스트가 CI에서 아예 실행되지 않던 문제를 마일스톤 0에서 해소한 것이 이 계획에서 가장 값싸고 효과가 큰 변경이었다.

셋째, 의도의 문서화도 달성했다. 예로 든 `ProcessIngredientUseCase`의 "성공했을 때만 후속 호출"은 이제 `처리에 실패하면 홈 요약을 다시 불러오지 않는다`라는 이름의 테스트가 지킨다.

계획 대비 가장 크게 달라진 것은 프레젠테이션 레이어의 검증 범위다. `orbit-test`의 `test()`가 컨테이너를 바꿔치기하는 구조 때문에 `init` 블록의 초기 로딩은 검증할 수 없었고, 상태를 주입한 뒤의 Intent 처리만 다뤘다. 이 제약을 없애려면 `NeveraViewModel`을 Orbit의 `onCreate` 방식으로 바꿔야 하는데, 별도 계획으로 분리해 두었다. 지금 남긴 ViewModel 테스트 59개가 그 리팩터링의 안전망이 되므로 순서상으로는 이 편이 낫다.

방법론에서 배운 것이 하나 있다. 모든 마일스톤에서 테스트를 쓴 뒤 프로덕션 코드를 일부러 망가뜨려 의도한 테스트가 실패하는지 확인했는데, 이 절차가 없었다면 통과하지만 아무것도 지키지 않는 테스트를 여러 개 남겼을 것이다. 실제로 마일스톤 6에서는 이 확인 과정에서 "상태가 바뀌지 않는 것이 정상"인 경우를 발견해 검증 방식을 고쳤다.

테스트를 쓰다가 발견했지만 이 계획에서 고치지 않고 기록만 남긴 프로덕션 동작이 세 가지 있다. 원칙대로 테스트 추가와 동작 변경을 섞지 않았다. `ProcessIngredientMapper`가 알 수 없는 처리 유형을 폐기가 아닌 구조로 떨어뜨리는 것, `FridgeIngredientMapper`가 파싱 실패한 유통기한을 오늘 날짜로 대체해 화면에 잘못된 값이 보이는 것, 그리고 `HomeViewModel`에서 목록 추가 요청이 실패하면 `isLoadingMore`가 `true`로 남아 다음 요청이 영구히 막히는 것이다. 마지막 것은 사용자가 체감할 수 있는 결함이므로 별도 이슈로 다루는 편이 좋다.

남은 일도 적어 둔다. `feature/ingredient`, `feature/splash`, `feature/notification`, `feature/sample`의 ViewModel에는 아직 테스트가 없다. 이 계획이 확립한 형태를 그대로 따르면 되므로 추가 설계 없이 진행할 수 있다.


### 후속 리팩터링 (2026-07-23)


계획 완료 후 병합 전 코드 리뷰에서 나온 두 지적을 반영했다. 둘 다 동작이나 테스트 개수를 바꾸지 않는 순수 구조 정리이며, 각각 별도 커밋으로 분리했다.

첫째, 에러 매퍼 테스트 6개 파일에 똑같이 복제돼 있던 `httpError` 헬퍼를 `testutil/NetworkErrorFixtures.kt`로 추출했다. 리뷰어는 "생성 방식이 바뀌어도 한 곳만 고치면 된다"를 근거로 들었지만, 실제로 그건 컴파일러가 잡아주는 부분이라 추출의 진짜 값어치는 보일러플레이트 제거와 컨벤션 단일화에 있다고 근거를 바로잡아 받아들였다.

둘째, `FakeDataSources.kt` 한 파일에 섞여 있던 DataSource 대역과 응답 빌더를 성격·도메인별 7개 파일로 나눴다. 이 과정에서 정리한 두 원칙은 앞으로 남은 feature 모듈 테스트를 추가할 때 그대로 적용한다. (1) **빌더는 만드는 DTO에 귀속된다** — 호출하는 대역이 여럿이어도 생산하는 타입이 소속을 정한다. (2) **뭉뚱그린 집합명사 파일명(`*Doubles`, `*TestData`)을 피한다** — 한 파일이 한 종류만 담으면 파일명이 스스로 역할을 말한다.

두 리팩터링 모두 `testutil` 패키지 안에서의 파일 재배치라 테스트 import가 하나도 바뀌지 않았고, `:data:testDebugUnitTest`와 `detekt`가 그대로 통과함을 확인했다.


## Context and Orientation


### 이 저장소의 구조


Nevera Android는 Gradle 멀티 모듈 프로젝트다. `settings.gradle.kts`가 20개 모듈을 포함하며, 이 계획과 관계있는 것은 다음과 같다.

`domain`은 순수 Kotlin(JVM) 모듈이다. Android SDK에 의존하지 않으며 `build-logic/src/main/kotlin/NeveraKotlinJvmPlugin.kt`가 적용된다. 안에는 세 종류가 있다. 데이터를 담는 model 클래스 약 49개(`domain/src/main/kotlin/com/anddd/nevera/domain/model/` 아래), 데이터를 가져오는 방법을 선언만 하는 repository 인터페이스 10개(`.../domain/repository/`), 그리고 하나의 사용자 시나리오를 실행하는 UseCase 약 40개(`.../domain/usecase/`)다. UseCase는 관례적으로 `operator fun invoke(...)`를 하나 갖는다. 즉 `useCase(인자)` 형태로 함수처럼 호출된다.

`data`는 Android 라이브러리 모듈이며 `domain`의 repository 인터페이스를 실제로 구현한다. 안에는 Retrofit API 선언(`data/src/main/kotlin/com/anddd/nevera/data/api/`), 그 API를 감싸는 DataSource(`.../data/datasource/`), 서버 응답 형태의 model(`.../data/model/`), 서버 model을 도메인 model로 바꾸는 mapper(`.../data/mapper/`), 그리고 repository 구현체(`.../data/repository/`)가 있다.

`feature:*`는 화면 단위 Android 라이브러리 모듈이다. 각 화면은 MVI 패턴을 따르며, 자세한 규칙은 저장소 루트 `CLAUDE.md`에 있다.

`core:mvi`는 모든 feature ViewModel의 부모 클래스 `NeveraViewModel`을 제공한다.

`core:common`은 순수 Kotlin 모듈이며 성공/실패를 표현하는 `NeveraResult`와 네트워크 오류를 표현하는 `NetworkError`를 제공한다.

`core:network`는 Retrofit·OkHttp 설정과 `ApiCallExecutor`를 제공한다.

`quality:detekt-rules`는 이 프로젝트 전용 정적 분석 규칙을 담은 순수 Kotlin 모듈이다. 이 계획에서 코드를 수정하지는 않지만, CI 명령에 이 모듈의 `test` 태스크가 이미 명시되어 있다는 점이 마일스톤 0과 관계있다.


### 반드시 알아야 할 타입 세 가지


**`NeveraResult`** — 이 프로젝트에서 "성공했거나 실패했다"를 표현하는 타입이다. `core/common/src/main/kotlin/com/anddd/nevera/core/common/NeveraResult.kt`에 정의되어 있고 전체가 여섯 줄이다.

    sealed interface NeveraResult<out T, out E> {
        data class Success<T>(val data: T) : NeveraResult<T, Nothing>
        data class Failure<E>(val error: E) : NeveraResult<Nothing, E>
    }

같은 파일에 `onSuccess`, `onFailure`, `mapSuccess`, `mapFailure`, `map`, `fold` 확장 함수가 있다. `Success`와 `Failure`가 모두 `data class`이므로 테스트에서 `assertEquals(NeveraResult.Success(Unit), result)`처럼 값 비교가 그대로 동작한다. 이 성질은 테스트를 쓸 때 매우 편하니 기억해 둔다.

**`NetworkError`** — 서버 통신에서 생길 수 있는 실패를 네 가지로 나눈 sealed interface다. `core/common/src/main/kotlin/com/anddd/nevera/core/common/NetworkError.kt`에 있으며 `HttpError(code, message, throwable)`, `NetworkConnectionError`, `TimeoutError`, `UnknownError`로 구성된다. `data` 레이어의 에러 매퍼들은 이 타입을 받아 화면에서 쓸 도메인 에러로 바꾼다.

**`ApiCallExecutor`** — `core/network/src/main/kotlin/com/anddd/nevera/core/network/auth/ApiCallExecutor.kt`에 있는 클래스로, 서버 호출 람다를 받아 `NeveraResult<T, NetworkError>`로 바꾼다. 서버는 HTTP 200을 주면서도 본문 안에 에러 코드를 담아 보낼 수 있는데, 이 클래스가 그 경우를 실패로 처리한다. 인터페이스가 아니라 구체 클래스이고 생성자 인자가 `Gson` 하나뿐이므로, 테스트에서 `ApiCallExecutor(Gson())`을 직접 만들어 쓸 수 있다.


### MVI 관련 용어


이 프로젝트의 화면은 네 종류의 타입으로 상태를 다룬다. 자세한 정의는 저장소 루트 `CLAUDE.md`에 있고, 이 계획을 실행하는 데 필요한 최소한만 옮기면 다음과 같다.

`*Intent`는 사용자가 화면에서 한 행동을 타입으로 표현한 것이다. `*UiState`는 화면이 어떻게 보여야 하는지를 담은 하나의 불변 객체다. `*Mutation`은 상태가 어떻게 바뀌어야 하는지를 의미 단위로 표현한 타입이다. `*SideEffect`는 상태로 보관하지 않고 한 번 소비되면 끝나는 이벤트(화면 이동, 토스트, 바텀시트 열기)다.

`core/mvi/src/main/kotlin/com/anddd/nevera/core/mvi/NeveraViewModel.kt`는 이 네 타입을 제네릭 인자로 받는 추상 클래스이며, 자식 클래스는 두 함수를 구현한다. `handleIntent(intent)`는 사용자 행동을 받는 유일한 입구이고, `applyMutation(mutation)`은 상태를 실제로 바꾸는 유일한 장소다. 내부적으로는 Orbit MVI 라이브러리(`org.orbit-mvi`, 버전 11.0.0)의 컨테이너를 쓴다.

Orbit 용어 중 이 계획에 등장하는 것은 셋이다. **컨테이너(Container)**는 상태 하나와 SideEffect 흐름 하나를 들고 있는 객체다. **`intent { }` 블록**은 컨테이너 안에서 코루틴으로 실행되는 작업 단위다. **`reduce { }` 블록**은 현재 상태를 새 상태로 교체하는 연산이며, 이 프로젝트에서는 `applyMutation` 안에서만 호출하도록 detekt 규칙으로 강제되어 있다.


### 현재 테스트 현황


의미 있는 테스트는 다음 다섯 곳에만 있다.

`domain/src/test/kotlin/com/anddd/nevera/domain/usecase/validation/ValidatePasswordUseCaseTest.kt`는 비밀번호 검증 규칙을 12개 케이스로 검증한다. 이 계획에서 새로 쓸 테스트의 문체 기준점이다.

`domain/src/test/kotlin/com/anddd/nevera/domain/model/notification/FcmTokenErrorLogMessageTest.kt`.

`data/src/test/kotlin/com/anddd/nevera/data/datasource/CryptoHelperTest.kt`.

`infra/notification/src/test/kotlin/com/anddd/nevera/infra/notification/worker/`의 두 파일과, 같은 모듈 `testutil/` 아래 Fake 두 개.

`infra/permission/src/test/kotlin/com/anddd/nevera/infra/permission/DefaultPermissionCheckerTest.kt`와 `testutil/FakePermissionChecker.kt`.

`quality/detekt-rules/src/test/kotlin/`의 9개 파일은 커스텀 정적 분석 규칙을 검증한다.

그 밖의 모듈은 두 부류로 나뉜다. 대부분은 `src/test/kotlin/com/anddd/nevera/ExampleUnitTest.kt` 하나만 갖고 있는데, 내용은 `assertEquals(4, 2 + 2)` 한 줄이라 항상 통과한다. 그리고 `feature/auth`, `feature/notification`, `core/mvi` 세 모듈에는 `src/test` 디렉터리 자체가 없다. 이 세 모듈에 테스트를 추가할 때는 디렉터리부터 만들어야 한다는 뜻이며, 특히 `feature/auth`는 마일스톤 6에서 `SignupViewModel`을 다루므로 해당된다. Gradle 설정은 이미 모든 모듈에 JUnit 5를 붙여 두었으므로 디렉터리를 만드는 것 외에 추가 설정은 필요 없다.


### 현재 테스트 도구 설정


`build-logic/src/main/kotlin/NeveraTestUnitPlugin.kt`가 모든 모듈에 JUnit 5를 붙인다. 이 플러그인은 `useJUnitPlatform()`을 설정하고 `junit-jupiter`, `junit-jupiter-engine`, `junit-platform-launcher`를 추가한다. 순수 Kotlin 모듈은 `NeveraKotlinJvmPlugin`이, Android 모듈은 `NeveraAndroidLibraryPlugin`이 각각 이 플러그인을 적용하므로 모든 모듈에서 JUnit 5를 쓸 수 있다.

`gradle/libs.versions.toml`에 이미 등록되어 바로 쓸 수 있는 테스트 라이브러리는 `libs.coroutines.test`(코루틴 테스트용, 별칭 `coroutines-test`), `libs.mockk`(별칭 `mockk`, 버전 1.13.13), `libs.assertj.core`(별칭 `assertj-core`)다.

모듈별 현재 테스트 의존성은 다음과 같다. `domain`은 `testImplementation(libs.coroutines.test)`가 이미 있다. `data`는 테스트 의존성이 하나도 없다. `feature:*` 모듈들도 없으며, 이들에 공통 의존성을 넣는 자리는 `build-logic/src/main/kotlin/NeveraFeaturePlugin.kt`다.


### 현재 CI 설정


`.github/workflows/ci.yml`은 하나의 job 안에서 세 개의 검사 스텝을 실행한다. 각 스텝은 `continue-on-error: true`로 설정되어 실패해도 즉시 중단되지 않고, 마지막 스텝에서 세 결과를 모아 하나라도 실패면 job을 실패시킨다. 테스트 스텝의 현재 명령은 다음과 같다.

    ./gradlew testDebugUnitTest :quality:detekt-rules:test --no-daemon --info

이 구조 덕분에 테스트가 깨져도 lint와 detekt 결과를 함께 볼 수 있다. 마일스톤 0은 이 구조를 유지한 채 명령 한 줄만 넓힌다.


## Plan of Work


### 마일스톤 0: CI가 순수 Kotlin 모듈의 테스트를 실행하게 만든다


이 마일스톤의 범위는 파일 한 개, 한 줄이다. 하지만 이 계획에서 가장 중요한 단계다. 지금 CI는 `domain`과 `core:common`의 테스트를 실행하지 않으므로, 이후 마일스톤에서 아무리 좋은 테스트를 써도 자동으로 검증되지 않기 때문이다.

`.github/workflows/ci.yml`의 `Run unit tests` 스텝에서 `run:` 값을 다음과 같이 바꾼다.

    ./gradlew testDebugUnitTest :domain:test :core:common:test :quality:detekt-rules:test --no-daemon --info

`:quality:detekt-rules:test`는 이미 있던 항목이므로 그대로 두고, `:domain:test`와 `:core:common:test` 두 개만 추가하는 것이다. 나머지 설정(`id`, `continue-on-error`, `env`)은 건드리지 않는다.

이 마일스톤이 끝나면 저장소에 존재하지만 검증되지 않던 도메인 테스트 12개 이상이 Pull Request마다 실행된다. 이것을 눈으로 확인하는 방법은 `Validation and Acceptance` 절의 마일스톤 0 항목에 있다. 로컬에서 새 명령이 도메인 테스트를 실제로 실행하는지 먼저 확인한 뒤, 일부러 실패하는 테스트를 잠시 넣어 명령이 실패로 끝나는 것까지 확인하고 되돌린다. 이 두 단계를 모두 거쳐야 "명령이 통과했다"가 "테스트가 실행되었다"를 의미한다고 믿을 수 있다.


### 마일스톤 1: 의존성 없는 도메인 UseCase부터 시작한다


이 마일스톤의 목적은 두 가지다. 실제로 회귀를 잡는 테스트를 몇 개 확보하는 것과, 이후 마일스톤에서 반복할 작업의 리듬(파일을 어디에 만들고, 이름을 어떻게 짓고, 어떤 명령으로 확인하는지)을 확립하는 것이다. 그래서 협력 객체가 전혀 없는 UseCase 두 개로 시작한다. 새 의존성도, 새 Fake도 필요 없다.

첫 번째 대상은 `domain/src/main/kotlin/com/anddd/nevera/domain/usecase/deeplink/ResolveDeeplinkUseCase.kt`다. 이 UseCase는 문자열 하나를 받아 `nevera://detail/` 로 시작하면 뒤에 붙은 식별자로 `DeeplinkAction.NavigateToIngredientDetail`을 만들고, 그렇지 않거나 식별자가 비어 있으면 `null`을 돌려준다. 딥링크는 외부 앱이나 푸시 알림에서 들어오는 입력이라 손으로 확인하기 번거로운 대표적인 경로다. 테스트는 `domain/src/test/kotlin/com/anddd/nevera/domain/usecase/deeplink/ResolveDeeplinkUseCaseTest.kt`에 만든다. 최소한 다음 네 가지 상황을 다룬다. 올바른 딥링크에서 식별자가 정확히 뽑히는 경우, 접두사는 맞지만 식별자가 비어 있어 `null`이 되는 경우, 접두사가 다른 문자열이 `null`이 되는 경우, 빈 문자열이 `null`이 되는 경우다.

두 번째 대상은 `domain/src/main/kotlin/com/anddd/nevera/domain/usecase/validation/ValidateEmailUseCase.kt`다. 같은 패키지의 `ValidatePasswordUseCase`는 이미 테스트가 있으므로, 짝을 맞춰 이메일 쪽도 채운다. 테스트 파일은 `domain/src/test/kotlin/com/anddd/nevera/domain/usecase/validation/ValidateEmailUseCaseTest.kt`다.

이 UseCase는 `domain/src/main/kotlin/com/anddd/nevera/domain/model/validation/EmailValidationResult.kt`에 정의된 세 결과 중 하나를 돌려준다. 입력이 공백뿐이면 `Empty`, 정규식에 맞지 않으면 `InvalidFormat`, 통과하면 `Valid`다. 정규식은 골뱅이 앞에 영숫자와 `._%+-`가 하나 이상, 골뱅이 뒤에 영숫자·점·하이픈, 마지막 점 뒤에 알파벳 두 글자 이상을 요구한다. 세 결과 각각에 대응하는 테스트를 쓰되, `InvalidFormat` 쪽은 서로 다른 이유로 걸리는 입력을 여러 개 넣는다. 골뱅이가 없는 문자열, 골뱅이 뒤에 점이 없는 문자열, 최상위 도메인이 한 글자인 문자열, 한글이 섞인 문자열이 좋은 후보다. `Empty` 쪽은 빈 문자열과 공백만 있는 문자열을 모두 다룬다(`isBlank()`가 둘 다 참이므로 같은 결과여야 한다). 코드에 없는 규칙을 상상해서 테스트를 쓰지 않는다.

두 테스트 파일 모두 기존 `ValidatePasswordUseCaseTest.kt`의 형태를 따른다. 즉 `org.junit.jupiter.api.Test` 애너테이션, 백틱으로 감싼 한글 함수 이름, `org.junit.jupiter.api.Assertions`의 단언 함수를 쓴다.

이 마일스톤이 끝나면 `./gradlew :domain:test`가 기존 테스트와 새 테스트를 모두 실행하고 통과한다.


### 마일스톤 2: Fake 저장소를 도입하고 오케스트레이션 UseCase를 검증한다


여기가 Domain 레이어의 본론이다. 대상은 "여러 저장소를 조율하거나, 성공한 뒤에 추가 동작이 따라붙는" UseCase들이다. 이런 UseCase의 계약은 코드를 읽어야만 알 수 있고, 실수로 깨뜨려도 컴파일이 통과하므로 테스트의 가치가 가장 크다.

먼저 Fake를 만든다. `domain/src/test/kotlin/com/anddd/nevera/domain/testutil/` 디렉터리를 새로 만들고, 필요한 저장소 인터페이스의 Fake를 하나씩 추가한다. 작성 방식은 `infra/notification/src/test/kotlin/com/anddd/nevera/infra/notification/testutil/FakeFcmTokenRepository.kt`를 그대로 따른다. 즉 인터페이스를 구현하되, 각 메서드가 돌려줄 값은 `var` 프로퍼티나 생성자 기본값으로 바꿀 수 있게 하고, 호출이 일어났다는 사실은 `mutableListOf`에 기록하거나 카운터로 센다.

이 마일스톤에서 필요한 Fake는 다음과 같다. `domain/src/main/kotlin/com/anddd/nevera/domain/repository/AuthRepository.kt`에 대한 `FakeAuthRepository`, `TokenRepository.kt`에 대한 `FakeTokenRepository`, `IngredientRepository.kt`에 대한 `FakeIngredientRepository`, `HomeRepository.kt`에 대한 `FakeHomeRepository`, `FcmTokenRepository.kt`와 `FcmTokenProvider.kt`에 대한 Fake다. 마지막 두 개는 `infra/notification`의 테스트에 거의 같은 것이 이미 있으므로 그 파일을 열어 보고 필요한 부분만 옮겨 온다. 인터페이스에 선언된 메서드가 많아도 이 마일스톤에서 쓰지 않는 것은 `TODO()`가 아니라 안전한 기본값(빈 리스트, `NeveraResult.Success`, `null`)을 돌려주게 둔다. `TODO()`는 실행되는 순간 예외를 던져 테스트 실패 원인을 헷갈리게 만든다.

Fake가 준비되면 다음 네 UseCase를 검증한다.

`EmailLoginUseCase`(`domain/src/main/kotlin/com/anddd/nevera/domain/usecase/auth/EmailLoginUseCase.kt`)는 로그인에 성공했을 때만 토큰을 저장한다. 이 파일의 주석은 "로그인과 토큰 저장은 원자적으로 처리되어야 하는 하나의 비즈니스 시나리오"라고 적고 있는데, 테스트가 없으면 이 문장은 지켜지지 않아도 아무도 모른다. 검증할 것은 셋이다. 성공하면 `TokenRepository`에 접근 토큰·갱신 토큰·로그인 수단(`LoginProvider.EMAIL`)이 전달된다는 것, 실패하면 토큰 저장이 **한 번도 호출되지 않는다**는 것, 그리고 성공 시 반환값이 `NeveraResult.Success(Unit)`이라는 것이다. 두 번째가 이 테스트의 핵심이다.

`ProcessIngredientUseCase`(`.../usecase/ingredient/ProcessIngredientUseCase.kt`)는 재료 처리에 성공했을 때만 홈 요약(`homeRepository.loadSummary()`)과 처리된 재료 목록(`ingredientRepository.loadProcessedIngredients()`)을 다시 불러온다. 검증할 것은 성공 시 두 후속 호출이 각각 정확히 한 번 일어난다는 것과, 실패 시 두 호출이 모두 일어나지 않고 원래의 실패 결과가 그대로 반환된다는 것이다.

`SyncDeviceTokenUseCase`(`.../usecase/SyncDeviceTokenUseCase.kt`)는 이 저장소에서 가장 분기가 복잡한 UseCase다. 푸시 알림 토큰을 서버와 동기화하는데, 인자로 받은 토큰이 있는지, 저장된 토큰과 같은지, 동기화가 필요하다고 표시되어 있는지, 로그인 상태인지에 따라 등록할지 건너뛸지를 결정한다. 최소한 다음 다섯 상황을 다룬다. 새 토큰이 들어오면 저장하고 등록한다, 저장된 토큰과 같고 동기화 필요 표시가 없으면 아무것도 하지 않는다, 인자가 비어 있으면 저장된 토큰을 쓴다, 로그인 상태가 아니면 등록하지 않고 성공을 반환한다, 등록에 성공하면 동기화 필요 표시가 해제된다.

`CheckAutoLoginUseCase`(`.../usecase/auth/CheckAutoLoginUseCase.kt`)는 짧지만 빈 문자열과 `null`을 모두 "로그인 안 됨"으로 취급하는 규칙이 들어 있다. 저장된 토큰이 `null`일 때, 빈 문자열일 때, 값이 있을 때 세 가지를 검증한다.

이 UseCase들은 `suspend` 함수이므로 테스트 본문을 `kotlinx.coroutines.test.runTest { }`로 감싼다. 이 라이브러리는 `domain/build.gradle.kts`에 이미 `testImplementation(libs.coroutines.test)`로 들어 있어 추가 작업이 없다.

이 마일스톤이 끝나면 도메인 레이어의 "숨은 계약"이 모두 실행 가능한 형태로 문서화된다.


### 마일스톤 3: 데이터 레이어의 순수 변환 함수를 검증한다


`data` 레이어에서 비용 대비 효과가 가장 큰 곳은 매퍼다. 매퍼는 입력을 받아 출력을 돌려주는 순수 함수라 협력 객체가 필요 없고, 동시에 서버 스펙이 코드에 숫자와 문자열로 박혀 있는 지점이라 서버가 바뀌면 조용히 틀린 값을 만들어 낸다.

먼저 `data/build.gradle.kts`의 `dependencies` 블록 끝에 다음 한 줄을 추가한다.

    testImplementation(libs.coroutines.test)

매퍼 테스트 자체에는 코루틴이 필요 없지만, 마일스톤 4에서 필요하므로 이 시점에 함께 넣어 둔다.

첫 번째 대상은 에러 매퍼다. `data/src/main/kotlin/com/anddd/nevera/data/mapper/error/` 아래 18개 파일이 있는데, 모두 테스트할 필요는 없다. 이들 중 상당수는 공통 매퍼에 그대로 위임하는 한 줄짜리다. 실제로 서버 코드에 따라 분기하는 것만 고른다. 골라내는 방법은 각 파일에서 `private object ...ErrorCode` 선언이나 `when (code)` 분기가 있는지 보는 것이다. `LoginErrorMapper.kt`가 대표적인 예로, 서버 코드 `2008`을 `LoginError.InvalidCredentials`로 바꾸고 나머지는 공통 에러로 처리한다.

에러 매퍼 테스트는 각 매퍼마다 최소 세 가지를 다룬다. 명시된 서버 코드가 대응하는 전용 도메인 에러로 바뀌는 경우, 알려지지 않은 서버 코드가 공통 에러로 떨어지는 경우, 그리고 `NetworkError.HttpError`가 아닌 다른 종류(`NetworkConnectionError`, `TimeoutError`, `UnknownError`)가 각각 어떤 공통 에러가 되는지다. 마지막 항목의 기준은 `data/src/main/kotlin/com/anddd/nevera/data/mapper/error/CommonErrorMapper.kt`에 있으며, 이 파일 자체도 테스트 대상이다. 네 가지 `NetworkError`가 각각 `CommonError.ServerError`, `CommonError.NetworkUnavailable`, `CommonError.Timeout`, `CommonError.Unknown`으로 간다는 사실을 고정한다.

두 번째 대상은 값 매퍼다. 우선순위가 가장 높은 것은 `data/src/main/kotlin/com/anddd/nevera/data/mapper/IngredientMapper.kt`다. 이 파일에는 검증할 가치가 높은 세 가지가 있다. 하나는 서버의 카테고리 문자열을 `FoodCategory`로 바꾸는 `toFoodCategory()`인데, 같은 카테고리에 여러 문자열이 매핑되고(`"VEG"`와 `"VEGETABLE"`이 모두 `FoodCategory.Veg`), 알 수 없는 값은 `FoodCategory.Etc`가 되며, 심지어 `"GRAINS"`는 전용 항목이 없어 임시로 `Etc`가 된다는 주석까지 달려 있다. 이 임시 결정이야말로 테스트로 못 박아 두어야 나중에 전용 항목이 생겼을 때 안전하게 바꿀 수 있다. 다른 하나는 보관 위치 문자열을 바꾸는 `toStorageLocation()`으로, 알 수 없는 값이 `StorageLocation.Pantry`가 된다. 마지막은 `toApiExpirationDate()`로, `LocalDate`를 한국 표준시(Asia/Seoul) 자정 기준의 ISO-8601 문자열로 바꾼다. 시간대가 얽힌 변환은 실행 환경의 기본 시간대에 따라 결과가 달라지는 고전적인 함정이므로, 테스트에서 구체적인 날짜를 넣고 기대 문자열을 문자 그대로 비교한다.

`toApiString()` 계열(도메인 enum을 서버 문자열로 되돌리는 함수)도 함께 검증한다. 특히 `toFoodCategory()`와 `toApiString()`을 이어 붙였을 때 원래 값으로 돌아오는지(왕복 검증)를 몇 개 골라 확인하면 매핑표의 비대칭을 잡아낼 수 있다.

세 번째 대상은 나머지 값 매퍼 중 분기가 있는 것들이다. `NotificationMapper.kt`, `HomeMapper.kt`, `OcrProgressMapper.kt`, `ProcessIngredientMapper.kt`를 각각 열어 보고, `when`이나 `?:`(널 대체) 같은 분기가 있으면 그 분기를 덮는 테스트를 쓴다. 필드를 그대로 옮기기만 하는 매퍼는 건너뛴다.

이 매퍼들과 DataSource 인터페이스는 대부분 `internal`로 선언되어 있지만, 같은 모듈의 `src/test`에서는 그대로 보인다. 별도 설정이 필요 없다.

이 마일스톤이 끝나면 `./gradlew :data:testDebugUnitTest`가 새 테스트를 실행하고 통과한다.


### 마일스톤 4: 메모리 캐시를 가진 저장소 구현체를 검증한다


이 저장소에서 사이드이펙트가 가장 예측하기 어려운 코드가 여기 있다. `data/src/main/kotlin/com/anddd/nevera/data/repository/IngredientRepositoryImpl.kt`는 세 개의 `MutableStateFlow`(냉장고 재료 목록, 구조된 재료 목록, 폐기된 재료 목록)를 들고 있고, 이 값들은 `observeFridgeIngredients()` 등을 통해 여러 화면으로 흘러간다. 즉 이 캐시 조작이 틀리면 증상은 조작을 일으킨 화면이 아니라 그 목록을 구독하는 다른 화면에서 나타난다.

테스트를 쓰려면 협력 객체 두 종류가 필요하다. 하나는 DataSource이고, 다른 하나는 `ApiCallExecutor`다. 후자는 인터페이스가 아니지만 `Gson` 하나만 받는 순수한 변환기이므로 테스트에서 `ApiCallExecutor(Gson())`을 직접 만들어 실제 구현을 쓴다. 이렇게 하면 "서버가 HTTP 200과 함께 에러 본문을 보냈을 때 실패로 처리된다"는 실제 동작까지 함께 검증된다. `Gson`은 `data` 모듈이 `nevera.network` 플러그인을 통해 가져오는 `retrofit-converter-gson`의 전이 의존성으로 이미 컴파일 경로에 있다. 만약 테스트 컴파일에서 `com.google.gson.Gson`을 찾지 못하면 `data/build.gradle.kts`에 `testImplementation("com.google.code.gson:gson:2.11.0")`처럼 명시적으로 추가하는 대신, `gradle/libs.versions.toml`에 gson 별칭을 추가하고 그것을 참조한다(이 저장소는 모든 의존성을 카탈로그로 관리한다).

DataSource Fake는 `data/src/test/kotlin/com/anddd/nevera/data/testutil/` 아래에 만든다. 이들 인터페이스는 `ApiResponse<T>`(`core/network/src/main/kotlin/com/anddd/nevera/core/network/model/ApiResponse.kt`의 `data class ApiResponse<T>(val result: T?, val error: ApiError?)`)를 돌려주므로, Fake는 미리 정해 둔 `ApiResponse`를 반환하기만 하면 된다.

검증할 시나리오는 다음과 같다.

`IngredientRepositoryImpl.editIngredient`에는 캐시 갱신 규칙이 둘 있다. 편집 결과의 보관 위치나 카테고리가 기존 값과 달라졌으면 그 항목을 캐시에서 **제거**하고(현재 화면의 필터 조건에 더 이상 맞지 않으므로), 둘 다 그대로면 항목을 새 값으로 **교체**한다. 또한 캐시에 없는 식별자면 캐시를 그대로 둔다. 세 경우를 각각 검증한다. 캐시의 초기 상태는 `getFridgeIngredients(...)`를 먼저 호출해 채운다.

`IngredientRepositoryImpl.processIngredient`에도 규칙이 둘 있다. 처리 결과가 완료(`completed`)면 해당 항목을 캐시에서 제거하고, 부분 처리면 항목의 금액(`cost`)을 잔여 금액으로 갱신한다. 두 경우와, 실패했을 때 캐시가 전혀 바뀌지 않는 경우를 검증한다.

`IngredientRepositoryImpl.loadProcessedIngredients`는 구조된 재료와 폐기된 재료를 동시에 불러와 각 캐시에 넣는다. 두 캐시가 모두 채워지는지, 그리고 한쪽 호출이 실패해도 다른 쪽 캐시는 정상적으로 채워지는지를 검증한다.

`HomeRepositoryImpl.loadSummary`(`data/src/main/kotlin/com/anddd/nevera/data/repository/HomeRepositoryImpl.kt`)는 성공 시 요약을 캐시에 넣고, 그 캐시는 `observeHomeSummary()`로 흘러나간다. 이 흐름은 `filterNotNull()`이 걸려 있어 값이 들어오기 전에는 아무것도 방출하지 않는다. 성공 후 구독하면 값이 나오는지, 실패했을 때는 캐시가 갱신되지 않는지를 검증한다.

`Flow`를 검증할 때는 `kotlinx.coroutines.flow.first()`처럼 값을 한 번만 받아 오는 방식을 우선 고려한다. 위 시나리오는 모두 "동작을 먼저 시킨 뒤 캐시의 현재 값을 확인"하는 형태라 이것으로 충분하며, `MutableStateFlow`는 마지막 값을 항상 들고 있으므로 구독 시점이 늦어도 값을 놓치지 않는다.

이 마일스톤이 끝나면 데이터 레이어에서 가장 위험한 코드가 자동 검증 범위에 들어온다.


### 마일스톤 5 (스파이크): Presentation 레이어의 테스트 가능성을 실제로 확인한다


이 마일스톤은 프로토타입이다. 목적은 테스트를 많이 쓰는 것이 아니라, "이 프로젝트의 ViewModel을 `orbit-test`로 테스트할 수 있는가, 있다면 어떤 형태인가"라는 질문에 실행 결과로 답하는 것이다.

먼저 의존성을 붙인다. `gradle/libs.versions.toml`의 `[libraries]` 절에 다음 항목을 추가한다.

    orbit-test = { group = "org.orbit-mvi", name = "orbit-test", version.ref = "orbitMvi" }

`orbitMvi` 버전 참조는 이미 `[versions]`에 `orbitMvi = "11.0.0"`으로 존재하므로 새로 만들지 않는다. 그다음 `build-logic/src/main/kotlin/NeveraFeaturePlugin.kt`의 `dependencies` 블록에 테스트 의존성 두 줄을 추가한다.

    "testImplementation"(libs.findLibrary("orbit-test").get())
    "testImplementation"(libs.findLibrary("coroutines-test").get())

`orbit-test`는 코루틴 테스트 라이브러리와 Turbine을 전이 의존성으로 함께 가져오지만, 직접 쓰는 API(`runTest`)는 명시적으로 선언해 두는 편이 의존성 추적에 유리하다.

스파이크 대상은 가장 작은 ViewModel이다. `feature/mypage/src/main/kotlin/com/anddd/nevera/feature/mypage/appinfo/AppInfoViewModel.kt`(63행)로 시작한다. 이 파일을 열어 `init {}` 블록이 무엇을 하는지, 어떤 UseCase에 의존하는지 먼저 확인한다. 테스트는 `feature/mypage/src/test/kotlin/com/anddd/nevera/feature/mypage/appinfo/AppInfoViewModelTest.kt`에 만든다.

`orbit-test`의 사용 형태는 다음과 같다. `runTest { }` 안에서 ViewModel 인스턴스를 만들고, 그 인스턴스에 `test(this) { ... }`를 호출한다. `this`는 `runTest`가 제공하는 테스트 스코프다. 블록 안에서는 `awaitState()`로 다음 상태를, `awaitSideEffect()`로 다음 SideEffect를 하나씩 꺼내 확인하거나, `expectState { copy(...) }`로 "직전 상태에서 이렇게 바뀐다"를 선언적으로 쓸 수 있다.

이 스파이크가 답해야 하는 질문은 셋이다.

첫째, `init {}` 블록이 시작한 작업 때문에 테스트가 실패하는가. `orbit-test`는 테스트 종료 시점까지 방출된 모든 상태와 SideEffect가 소비되지 않으면 실패하도록 설계되어 있다. `AppInfoViewModel`이 초기 로딩을 한다면 그 과정에서 나온 상태들을 테스트가 모두 소비해야 한다. `skipItems(n)`으로 넘기거나 `cancelAndIgnoreRemainingItems()`로 정리할 수 있는지 실제로 확인한다.

둘째, 기본으로 켜져 있는 초기 상태 자동 확인(`TestSettings`의 `autoCheckInitialState`, 기본값 `true`)이 방해가 되는가. 필요하면 `test(this, settings = TestSettings(autoCheckInitialState = false))`로 끌 수 있다.

셋째, JUnit 5 환경에서 `orbit-test`가 전이로 끌고 오는 JUnit 4가 문제를 일으키는가. 이론적으로는 JUnit Platform이 Vintage 엔진 없이는 JUnit 4 테스트를 발견하지 않으므로 무해하지만, 실제로 확인한다.

세 질문의 답을 `Surprises & Discoveries`에 증거(테스트 실행 출력)와 함께 기록하고, 그 결과로 정해진 테스트 작성 방식을 `Decision Log`에 남긴다.

폐기 기준을 미리 정해 둔다. `AppInfoViewModel`처럼 가장 단순한 ViewModel에서조차 테스트를 안정적으로 통과시키지 못하고, 그 원인이 `init {}` 기반 초기화 구조에 있다고 판명되면, 이 계획의 Presentation 부분은 여기서 멈춘다. 그 경우 마일스톤 6을 실행하는 대신 별도의 후속 ExecPlan에서 `NeveraViewModel`이 Orbit의 `onCreate` 방식을 쓰도록 바꾸는 리팩터링을 다룬다. 그 리팩터링은 모든 feature ViewModel을 건드리므로 테스트 도입과 같은 계획에 섞을 일이 아니다. 이 결론에 도달하면 그 사실과 근거를 `Outcomes & Retrospective`에 명확히 적고, 마일스톤 5에서 추가한 의존성은 되돌릴지 남길지 판단해 기록한다.


### 마일스톤 6: 확정된 패턴으로 주요 ViewModel을 검증한다


마일스톤 5가 성공했을 때만 진행한다. 스파이크에서 확정한 형태를 그대로 적용해 화면 상태 전이를 검증한다.

작업 순서는 검증 대상의 성격 순이다. 먼저 각 ViewModel의 `applyMutation` 구현, 즉 "Mutation이 들어오면 UiState가 어떻게 바뀌는가"를 다룬다. 이것은 부수 효과가 없는 상태 전이라 가장 안정적이고, MVI 패턴을 쓰는 이유 그 자체를 지킨다. 다음으로 "특정 Intent를 넣으면 의도한 SideEffect가 나가는가"를 다룬다. 화면 이동과 토스트가 잘못된 조건에서 발생하는 버그는 손으로 잡기 어렵다. 마지막으로 초기 로딩처럼 여러 작업이 얽힌 복합 시나리오를 다룬다.

대상 ViewModel은 셋이다.

`feature/main/src/main/kotlin/com/anddd/nevera/feature/main/home/HomeViewModel.kt`(370행)가 가장 크고 위험하다. 특히 검증 가치가 높은 것은 목록 더 불러오기 로직이다. 이 코드는 `Mutex`로 동시 요청을 막고, 이미 불러오는 중이거나 더 불러올 것이 없으면 요청을 건너뛰며, 응답 개수가 페이지 크기와 같은지로 다음 페이지 존재 여부를 판단한다. 세 조건이 각각 정확히 동작하는지 확인한다. 또한 닉네임 갱신에 성공했을 때만 인사 바텀시트 SideEffect가 나가는지, 온보딩이 완료되지 않았을 때만 닉네임 설정 바텀시트 SideEffect가 나가는지를 확인한다.

`feature/auth/src/main/kotlin/com/anddd/nevera/feature/auth/signup/SignupViewModel.kt`(352행)는 입력 검증과 단계 진행이 얽혀 있다. 파일을 읽고 검증 규칙과 단계 전이를 파악한 뒤, 각 규칙에 대응하는 테스트를 쓴다.

`feature/fridge/src/main/kotlin/com/anddd/nevera/feature/fridge/main/FridgeViewModel.kt`(214행)는 필터와 정렬이 있는 목록 화면이다. 필터를 바꿨을 때 상태가 어떻게 바뀌고 어떤 재조회가 일어나는지를 확인한다.

각 ViewModel 테스트는 해당 feature 모듈의 `src/test/kotlin/` 아래, 프로덕션 코드와 같은 패키지에 만든다. ViewModel이 의존하는 UseCase들은 실제 클래스이므로(인터페이스가 아니다) Fake 저장소를 생성자에 넣어 만든 실제 UseCase를 쓰거나, UseCase 자체를 mockk로 대체한다. 어느 쪽을 택할지는 마일스톤 5의 결과를 보고 정하고 `Decision Log`에 남긴다. 전자는 도메인 로직까지 함께 검증되어 더 사실적이지만 준비 코드가 길어지고, 후자는 화면 로직만 격리해 보지만 도메인 계약이 바뀌어도 테스트가 통과할 수 있다.


### 마일스톤 7: 마무리와 정리


모든 테스트가 자리를 잡은 뒤, 실제 테스트가 들어간 모듈에서 `ExampleUnitTest.kt`를 삭제할지 결정한다. 삭제한다면 그 모듈에 다른 테스트가 최소 하나 이상 있는지 먼저 확인한다. 계측 테스트 쪽 `ExampleInstrumentedTest.kt`는 이 계획의 범위가 아니므로 건드리지 않는다.

그다음 `Validation and Acceptance`의 전체 검증 명령을 실행해 통과를 확인하고, `Outcomes & Retrospective`를 작성한다. 회고에는 최소한 다음을 포함한다. 레이어별로 추가된 테스트 수, 계획과 달랐던 부분과 그 이유, 그리고 이 과정에서 발견한 프로덕션 코드의 문제(테스트를 쓰다 보면 거의 항상 나온다)를 어떻게 처리했는지.


## Concrete Steps


모든 명령은 저장소 루트(`/Users/juhyeok/AndroidStudioProjects/Nevera-Android`)에서 실행한다.

작업을 시작하기 전에 현재 상태를 확인한다.

    ./gradlew :domain:test

기대 출력은 마지막 줄의 `BUILD SUCCESSFUL`이다. 이 명령이 이미 통과한다는 사실은 "도메인 테스트가 실행 가능하지만 CI가 실행하지 않을 뿐"임을 확인해 준다.

마일스톤 0에서는 `.github/workflows/ci.yml`을 수정한 뒤 새 명령을 로컬에서 그대로 실행한다.

    ./gradlew testDebugUnitTest :domain:test :core:common:test :quality:detekt-rules:test

이 명령은 처음 실행 시 모든 모듈을 컴파일하므로 수 분이 걸릴 수 있다. 실행이 끝나면 도메인 테스트가 실제로 실행되었는지 리포트로 확인한다.

    open domain/build/reports/tests/test/index.html

브라우저에 열린 리포트에서 `ValidatePasswordUseCaseTest`가 12개 테스트와 함께 보여야 한다. 파일이 존재하는지만 확인하려면 다음을 쓴다.

    ls -la domain/build/reports/tests/test/index.html

마일스톤 1과 2에서는 도메인 모듈만 반복 실행하는 것이 빠르다.

    ./gradlew :domain:test

특정 테스트 클래스만 실행하려면 다음처럼 필터를 준다.

    ./gradlew :domain:test --tests "*ResolveDeeplinkUseCaseTest"

마일스톤 3과 4에서는 데이터 모듈을 실행한다. Android 라이브러리 모듈이므로 태스크 이름이 다르다는 점에 주의한다.

    ./gradlew :data:testDebugUnitTest

마일스톤 5와 6에서는 해당 feature 모듈을 실행한다.

    ./gradlew :feature:mypage:testDebugUnitTest
    ./gradlew :feature:main:testDebugUnitTest

테스트가 실패하면 콘솔 요약만으로는 원인을 알기 어려울 때가 많다. 그때는 HTML 리포트를 연다. 경로 규칙은 순수 Kotlin 모듈이 `<모듈>/build/reports/tests/test/index.html`, Android 모듈이 `<모듈>/build/reports/tests/testDebugUnitTest/index.html`이다.

의존성을 추가하거나 convention plugin을 수정한 뒤 변경이 반영되지 않는 것처럼 보이면, Gradle 데몬을 재시작한 뒤 다시 실행한다. 이 저장소에서는 build-logic의 클래스가 데몬에 캐시되어 이런 일이 실제로 발생한 적이 있다.

    ./gradlew --stop

작업이 끝날 때마다 커밋한다. 마일스톤 하나가 여러 커밋으로 나뉘어도 좋으며, 커밋 메시지는 이 저장소의 Conventional Commits 관례를 따른다(예: `test(domain): EmailLoginUseCase 토큰 저장 계약 검증 추가`).


## Validation and Acceptance


각 마일스톤은 독립적으로 검증 가능해야 한다. 아래 기준은 사람이 눈으로 확인할 수 있는 동작으로 표현되어 있다.

**마일스톤 0의 수용 기준.** `.github/workflows/ci.yml`의 테스트 스텝 명령에 `:domain:test`와 `:core:common:test`가 포함되어 있다. 로컬에서 그 명령을 그대로 실행하면 `BUILD SUCCESSFUL`이 출력되고, `domain/build/reports/tests/test/index.html`이 생성되며 그 안에 `ValidatePasswordUseCaseTest`의 12개 테스트가 보인다.

더 중요한 확인이 하나 더 있다. 명령이 실패를 실제로 감지하는지 봐야 한다. `domain/src/test/kotlin/com/anddd/nevera/domain/usecase/validation/ValidatePasswordUseCaseTest.kt`의 아무 단언 하나를 일부러 틀리게 고친 뒤 명령을 다시 실행하면 다음과 같은 실패가 나와야 한다.

        > Task :domain:test FAILED
        ValidatePasswordUseCaseTest > 빈 문자열은 Empty를 반환한다 FAILED
            org.opentest4j.AssertionFailedError at ValidatePasswordUseCaseTest.kt:...
        FAILURE: Build failed with an exception.

이것을 확인한 뒤 수정을 반드시 되돌린다(`git checkout -- domain/src/test/...`). 이 확인을 건너뛰면 "명령이 통과했다"가 "테스트가 실행되었다"를 뜻하는지 알 수 없다.

**마일스톤 1의 수용 기준.** `./gradlew :domain:test`가 통과하고, `ResolveDeeplinkUseCaseTest`와 `ValidateEmailUseCaseTest`가 리포트에 나타난다. 각 테스트 클래스는 정상 입력과 비정상 입력을 모두 다룬다. 새 테스트가 실제로 의미 있는지 확인하려면 `ResolveDeeplinkUseCase.kt`에서 `if (id.isNotBlank())` 조건을 잠시 지워 본다. 식별자가 비었을 때를 다루는 테스트가 실패해야 하고, 확인 후 되돌린다.

**마일스톤 2의 수용 기준.** `./gradlew :domain:test`가 통과한다. 다음 네 가지가 테스트 이름으로 존재하며 통과한다. 이메일 로그인 실패 시 토큰이 저장되지 않는다, 재료 처리 실패 시 홈 요약을 다시 불러오지 않는다, 저장된 토큰과 같고 동기화가 필요 없으면 서버에 등록하지 않는다, 저장된 접근 토큰이 빈 문자열이면 자동 로그인은 `null`을 반환한다.

각 테스트가 진짜로 계약을 지키는지 확인하려면 프로덕션 코드를 잠시 망가뜨려 본다. 예를 들어 `ProcessIngredientUseCase.kt`의 `if (result is NeveraResult.Success)` 조건을 지우면 "실패 시 후속 호출이 없다"는 테스트가 실패해야 한다. 확인 후 되돌린다.

**마일스톤 3의 수용 기준.** `./gradlew :data:testDebugUnitTest`가 통과한다. `CommonErrorMapper`의 네 가지 `NetworkError` 분기가 모두 테스트로 덮여 있다. `IngredientMapper`의 카테고리 매핑 테스트가 알려진 문자열, 별칭 문자열(`"VEGETABLE"` 같은), 알 수 없는 문자열을 모두 다룬다. 날짜 변환 테스트가 구체적인 날짜에 대해 기대 문자열과 정확히 일치하는지 비교한다.

**마일스톤 4의 수용 기준.** `./gradlew :data:testDebugUnitTest`가 통과한다. 다음이 테스트로 검증된다. 재료 편집 시 보관 위치가 바뀌면 캐시에서 제거되고 그대로면 교체된다, 재료 처리가 완료 상태면 캐시에서 제거되고 부분 처리면 금액이 갱신된다, 재료 처리가 실패하면 캐시가 바뀌지 않는다, 홈 요약 로드에 성공하면 `observeHomeSummary()`가 그 값을 방출하고 실패하면 방출하지 않는다.

**마일스톤 5의 수용 기준.** `./gradlew :feature:mypage:testDebugUnitTest`가 통과하고 `AppInfoViewModelTest`가 최소 하나의 의미 있는 상태 전이를 검증한다. `Surprises & Discoveries`에 세 질문(초기화 블록의 영향, 초기 상태 자동 확인 설정, JUnit 4 전이 의존성)에 대한 답이 실행 출력과 함께 기록되어 있다. `Decision Log`에 이후 ViewModel 테스트를 어떤 형태로 쓸지에 대한 결정이 기록되어 있다. 또는 폐기 기준에 도달했다면 그 판단과 근거가 기록되어 있다.

**마일스톤 6의 수용 기준.** `./gradlew :feature:main:testDebugUnitTest :feature:auth:testDebugUnitTest :feature:fridge:testDebugUnitTest`가 통과한다. `HomeViewModel`의 더 불러오기 로직에 대해 세 가지(이미 불러오는 중이면 건너뛴다, 더 불러올 것이 없으면 건너뛴다, 응답이 페이지 크기보다 적으면 더 불러올 것이 없다고 표시한다)가 각각 테스트로 존재한다.

**계획 전체의 수용 기준.** 저장소 루트에서 다음 명령이 통과한다.

    ./gradlew testDebugUnitTest :domain:test :core:common:test :quality:detekt-rules:test

그리고 이 명령이 `.github/workflows/ci.yml`의 테스트 스텝과 정확히 같아서, 로컬에서 통과하면 CI에서도 통과한다는 것이 보장된다. 마지막으로 `./gradlew detekt`가 여전히 통과해야 한다. 새로 추가한 테스트 코드도 정적 분석 대상이기 때문이다.


## Idempotence and Recovery


이 계획의 거의 모든 단계는 파일 추가이므로 여러 번 반복해도 안전하다. Gradle 테스트 태스크는 상태를 남기지 않으며, 소스가 바뀌지 않으면 `UP-TO-DATE`로 건너뛴다. 캐시 때문에 결과가 이상하면 `./gradlew clean` 없이 `--rerun-tasks`를 먼저 시도한다.

    ./gradlew :domain:test --rerun-tasks

검증 과정에서 프로덕션 코드나 기존 테스트를 일부러 망가뜨리는 단계가 여러 번 등장한다. 그때마다 확인이 끝나면 즉시 되돌린다.

    git checkout -- <수정한 파일 경로>

되돌리기 전에 다른 변경을 커밋하지 않도록 주의한다. 안전한 방법은 일부러 망가뜨리기 직전에 작업 중인 변경을 먼저 커밋해 두는 것이다. 그러면 `git checkout --`가 커밋된 상태로 정확히 되돌린다.

빌드 설정을 건드리는 단계는 셋뿐이다. `.github/workflows/ci.yml` 한 줄(마일스톤 0), `data/build.gradle.kts`에 테스트 의존성 한 줄(마일스톤 3), 그리고 `gradle/libs.versions.toml`과 `build-logic/src/main/kotlin/NeveraFeaturePlugin.kt`(마일스톤 5)다. 각각을 별도 커밋으로 분리하면 문제가 생겼을 때 그 커밋만 되돌릴 수 있다. 특히 마일스톤 5는 폐기 가능성이 명시된 스파이크이므로 반드시 별도 커밋으로 둔다.

`build-logic` 아래 파일을 고친 뒤 변경이 반영되지 않는 것처럼 보이면 Gradle 데몬을 재시작한다. 이 저장소에서 실제로 겪은 함정이다.

    ./gradlew --stop

이 계획은 프로덕션 코드의 동작을 바꾸지 않는 것을 원칙으로 한다. 테스트를 쓰다가 프로덕션 코드의 버그를 발견하면, 그 자리에서 고치지 말고 `Surprises & Discoveries`에 기록한 뒤 별도 커밋(또는 별도 이슈)으로 분리한다. 테스트 추가와 동작 변경이 한 커밋에 섞이면 나중에 어느 쪽이 회귀를 일으켰는지 추적할 수 없다.


## Artifacts and Notes


마일스톤 0의 근거가 된 확인 결과. 순수 Kotlin 모듈에는 Android 테스트 태스크가 없다.

    $ ./gradlew :domain:testDebugUnitTest --dry-run
    FAILURE: Build failed with an exception.
    * What went wrong:
    Cannot locate tasks that match ':domain:testDebugUnitTest' as task
    'testDebugUnitTest' not found in project ':domain'.
    BUILD FAILED in 2s

`orbit-test` 11.0.0의 의존성 목록(`orbit-test-jvm-11.0.0.pom`에서 추출). 마일스톤 5에서 클래스패스에 무엇이 늘어나는지 보여 준다.

    kotlin-test          (compile 및 runtime)
    kotlinx-coroutines-core-jvm   (compile)
    kotlinx-coroutines-test-jvm   (compile)
    orbit-core-jvm       (compile)
    kotlin-stdlib        (compile)
    junit                (runtime)
    turbine-jvm          (runtime)

`orbit-test`가 제공하는 검증 함수 목록(`OrbitTestContext` 클래스의 공개 멤버). 마일스톤 5·6에서 쓸 도구다.

    runOnCreate()                    Orbit onCreate 람다 실행 (이 프로젝트에는 효과 없음)
    expectState(expected)            다음 상태가 주어진 값과 같은지 확인
    expectState { copy(...) }        다음 상태가 직전 상태에서 이렇게 바뀌는지 확인
    expectSideEffect(expected)       다음 SideEffect가 주어진 값과 같은지 확인
    awaitState()                     다음 상태를 꺼내 온다
    awaitSideEffect()                다음 SideEffect를 꺼내 온다
    skipItems(count)                 다음 n개를 확인 없이 소비한다
    expectNoItems()                  소비되지 않은 항목이 없는지 확인
    cancelAndIgnoreRemainingItems()  남은 항목을 버리고 컨테이너를 취소한다

기존 Fake의 형태(마일스톤 2에서 따라 쓸 기준). `infra/notification/src/test/kotlin/com/anddd/nevera/infra/notification/testutil/FakeFcmTokenRepository.kt`에서 발췌했다.

    class FakeFcmTokenRepository(
        var storedToken: String?,
        var syncNeeded: Boolean,
        var registerResult: NeveraResult<Unit, FcmTokenError> = NeveraResult.Success(Unit),
    ) : FcmTokenRepository {

        val markedTokens = mutableListOf<String>()
        val registeredTokens = mutableListOf<String>()

        override suspend fun getFcmToken(): String? = storedToken

        override suspend fun registerFcmToken(token: String): NeveraResult<Unit, FcmTokenError> {
            registeredTokens += token
            return registerResult
        }
    }

핵심은 셋이다. 반환할 값은 생성자 인자로 바꿀 수 있게 두고, 호출 사실은 리스트에 기록하며, 인터페이스를 직접 구현하므로 프로덕션 인터페이스가 바뀌면 컴파일이 깨져 즉시 알 수 있다.

기존 테스트의 문체(마일스톤 1 이후 모든 테스트가 따를 기준). `domain/src/test/kotlin/com/anddd/nevera/domain/usecase/validation/ValidatePasswordUseCaseTest.kt`에서 발췌했다.

    class ValidatePasswordUseCaseTest {

        private val useCase = ValidatePasswordUseCase()

        @Test
        fun `8자 미만이면 TooShort 에러를 반환한다`() {
            val result = useCase("Ab1!")

            assertTrue(result is PasswordValidationResult.Invalid)
            assertTrue((result as PasswordValidationResult.Invalid).errors
                .contains(PasswordValidationError.TooShort(8)))
        }
    }

테스트 이름이 "무엇을 하면 무엇이 된다" 형태의 한글 평서문이라는 점, 준비·실행·검증이 빈 줄로 구분된다는 점을 그대로 따른다.


## Interfaces and Dependencies


### 사용할 라이브러리


이미 카탈로그(`gradle/libs.versions.toml`)에 있어 그대로 참조하면 되는 것.

`libs.coroutines.test` — `org.jetbrains.kotlinx:kotlinx-coroutines-test`. `suspend` 함수를 테스트하는 `runTest { }`를 제공한다. `domain`에는 이미 붙어 있고, `data`에는 마일스톤 3에서 추가한다.

`libs.mockk` — `io.mockk:mockk`, 버전 1.13.13. Fake로 표현하기 번거로운 경우에만 쓴다.

`libs.assertj.core` — `org.assertj:assertj-core`. 현재 어떤 모듈도 쓰고 있지 않다. 기존 테스트가 모두 JUnit 5 기본 단언을 쓰므로 일관성을 위해 이 계획에서도 기본 단언을 쓰고, AssertJ는 도입하지 않는다.

카탈로그에 새로 추가해야 하는 것.

`orbit-test` — `org.orbit-mvi:orbit-test`, 버전 참조는 기존 `orbitMvi`(11.0.0)를 재사용한다. 마일스톤 5에서 추가한다. 이 라이브러리를 넣으면 Turbine(`app.cash.turbine`)과 `kotlinx-coroutines-test`가 전이 의존성으로 함께 들어온다.


### 수정할 빌드 파일


`.github/workflows/ci.yml` — `Run unit tests` 스텝의 `run:` 한 줄(마일스톤 0).

`data/build.gradle.kts` — `dependencies` 블록에 `testImplementation(libs.coroutines.test)` 추가(마일스톤 3).

`gradle/libs.versions.toml` — `[libraries]` 절에 `orbit-test` 항목 추가(마일스톤 5).

`build-logic/src/main/kotlin/NeveraFeaturePlugin.kt` — `dependencies` 블록에 `testImplementation` 두 줄 추가(마일스톤 5). 이 플러그인은 모든 feature 모듈에 적용되므로 여기 한 번만 추가하면 8개 feature 모듈이 모두 테스트 의존성을 갖는다.


### 새로 만들 디렉터리와 그 역할


`domain/src/test/kotlin/com/anddd/nevera/domain/testutil/` — 도메인 저장소 인터페이스의 Fake 구현체(마일스톤 2).

`data/src/test/kotlin/com/anddd/nevera/data/testutil/` — DataSource 대역과 응답·에러 픽스처. 대역은 클래스별 파일(`FakeHomeRemoteDataSource.kt`, `FakeIngredientRemoteDataSource.kt`, `FakeFridgeRemoteDataSource.kt`, `FakeOcrDataSources.kt`), 응답 빌더는 도메인별(`HomeResponseFixtures.kt`, `IngredientResponseFixtures.kt`), 네트워크 에러 빌더는 `NetworkErrorFixtures.kt`. 마일스톤 4에서 단일 `FakeDataSources.kt`로 시작해 이후 리뷰 대응으로 성격·도메인별로 분리했다(위 후속 리팩터링 참조).

테스트 파일 자체는 검증 대상과 같은 패키지에 둔다. 예를 들어 `com.anddd.nevera.domain.usecase.auth.EmailLoginUseCase`의 테스트는 `domain/src/test/kotlin/com/anddd/nevera/domain/usecase/auth/EmailLoginUseCaseTest.kt`에 만든다. 같은 패키지에 두면 `internal` 선언에 접근할 수 있고, 프로덕션 파일과 테스트 파일이 IDE에서 나란히 보인다.


### 이 계획이 의존하는 프로덕션 타입


다음 타입들은 이 계획에서 수정하지 않지만 테스트가 직접 참조한다. 전체 경로를 적어 둔다.

`com.anddd.nevera.core.common.NeveraResult` — 성공/실패 표현. `Success`와 `Failure` 모두 `data class`라 값 비교가 가능하다.

`com.anddd.nevera.core.common.NetworkError` — 네 가지 네트워크 실패 종류.

`com.anddd.nevera.core.network.auth.ApiCallExecutor` — 생성자가 `Gson` 하나를 받는 구체 클래스. 마일스톤 4에서 실제 인스턴스를 만들어 쓴다.

`com.anddd.nevera.core.network.model.ApiResponse` — `result`와 `error` 두 필드를 가진 서버 응답 껍데기. Fake DataSource가 이 타입을 돌려준다.

`com.anddd.nevera.core.mvi.NeveraViewModel` — 모든 feature ViewModel의 부모. Orbit의 `ContainerHost`를 구현하므로 `orbit-test`의 `test(testScope) { }` 확장 함수를 그대로 쓸 수 있다.

`org.orbitmvi.orbit.test.OrbitTestContext` — 마일스톤 5·6에서 쓰는 검증 API. 제공 함수 목록은 `Artifacts and Notes`에 있다.


## 변경 메모


- 2026-07-23: 계획 완료 후 병합 전 리뷰 대응으로 두 건의 구조 리팩터링(공용 `httpError` 픽스처 추출, `FakeDataSources.kt`의 대역/픽스처 분리)을 반영했다. `Progress`에 후속 항목을, `Decision Log`에 두 결정을, `Outcomes & Retrospective`에 "후속 리팩터링" 절을 추가했고, `Interfaces and Dependencies`의 `testutil` 설명을 현재 파일 구조에 맞게 갱신했다. 두 작업 모두 동작과 테스트 개수를 바꾸지 않는다.
