# 안 B — feature 모듈을 api/impl로 분리해 화면 이동을 연결한다

이 ExecPlan은 살아있는 문서다. `Progress`, `Surprises & Discoveries`, `Decision Log`, `Outcomes & Retrospective` 섹션은 작업이 진행되는 동안 반드시 최신 상태로 유지해야 한다.

이 문서는 저장소 루트의 `PLANS.md`가 정의한 ExecPlan 요건에 따라 작성되고 유지된다.

브랜치: `nav/b-api-impl`

## Purpose / Big Picture

이 앱에서 홈 화면 우상단의 알림 아이콘을 누르면 알림 목록 화면으로 간다. 그런데 홈 화면을 담고 있는 모듈은 알림 화면이 존재한다는 사실조차 모른다. 대신 최상위 모듈인 `:app`이 "알림으로 가라"는 함수를 만들어 홈에게 넘겨준다. 알림으로 가는 길이 필요한 화면이 홈·냉장고·마이페이지 세 곳이라, `:app`은 같은 함수를 세 번 만들어 세 곳에 넘긴다.

이 작업이 끝나면 세 화면이 각각 알림 목적지의 이름을 직접 알고 스스로 이동한다. `:app`은 그 세 함수를 더 이상 만들지 않는다.

그게 가능하려면 "알림 화면의 목적지 이름"이 알림 화면 구현과 분리되어야 한다. 이 계획은 알림 모듈 자체를 두 개로 쪼갠다. 이름만 담은 아주 작은 모듈(`:feature:notification:api`)과 화면 구현을 담은 모듈(`:feature:notification:impl`)이다. 다른 화면은 `api`만 의존 선언한다. 알림 화면 UI가 바뀌어도 남의 모듈에는 영향이 없고, **의존을 선언하지 않은 모듈은 알림 목적지의 이름조차 볼 수 없다.**

사용자 눈에 보이는 동작은 변하지 않는다. 이건 내부 구조 변경이다. 그래서 "동작이 그대로인 것"과 "구조가 실제로 바뀐 것"을 둘 다 증명한다. 전자는 앱을 실행해 세 화면에서 알림 아이콘을 눌러 확인하고, 후자는 `./gradlew :feature:main:dependencies`에 알림 화면 구현 모듈이 나타나지 않는 것으로 확인한다.

이 계획은 같은 목표를 다른 방식으로 구현한 두 계획(`nav/a-core-navigation`, `nav/c-di-registry` 브랜치)과 비교하기 위한 것이다. 세 브랜치 모두 **똑같은 범위**를 구현해 차이를 직접 비교할 수 있게 한다.

## Progress

- [x] (2026-08-04) M1 `nevera.feature.api` convention plugin 작성 및 등록
- [x] (2026-08-04) M2 `:feature:notification`을 `api`/`impl`로 분리 (콜백 방식은 유지)
- [x] (2026-08-04) M3 홈·냉장고·마이페이지를 직접 이동으로 전환, `:app` 콜백 3개 제거
- [x] (2026-08-04) M4 impl 간 의존을 빌드가 거부하도록 검사 추가
- [ ] M5 (범위 밖) `auth`, `main`, `fridge`, `ingredient` 분리

## Surprises & Discoveries

- 관찰: `api` 모듈을 **순수 Kotlin(JVM) 모듈로 만들 수 있다.** Navigation의 타입 안전 라우팅이 비-Android 모듈의 `@Serializable` 클래스를 문제없이 처리한다.
  증거: `feature/notification/api/build.gradle.kts`가 `nevera.kotlin.jvm`만 적용하고 Android 플러그인을 쓰지 않는데 `./gradlew :app:assembleDebug`가 통과했다. 이 모듈의 의존성은 `kotlinx-serialization-json` 하나뿐이다. 이건 안 A와의 중요한 차이다 — 안 A의 `:core:navigation`은 `NavController`를 참조해야 해서 Android 라이브러리일 수밖에 없다.

- 관찰: `api` 모듈이 순수 Kotlin이므로 **Compose 코드를 넣는 것이 물리적으로 불가능하다.** 이 모듈이 잡동사니 저장소가 되는 것을 규칙이 아니라 컴파일러가 막는다.
  증거: `api` 모듈에는 `androidx.compose.*`가 클래스패스에 없으므로 `@Composable`을 쓰면 컴파일되지 않는다.

- 관찰: 디렉터리 이동은 `git mv`를 써야 이력이 보존된다.
  증거: `git mv feature/notification/src feature/notification/impl/src`로 옮긴 뒤 `git log --follow`가 이전 이력을 따라간다. 파일 탐색기로 옮기면 삭제+추가로 기록되어 이력이 끊긴다.

- 관찰: `:app`도 `api`와 `impl` 양쪽에 의존해야 한다. `impl`만으로는 부족하다.
  증거: `:app`의 `NeveraNavHost.kt`는 그래프 등록 함수(`notificationScreen()`, impl에 있음)와 목적지 이름(`NotificationRoute`, api에 있음)을 둘 다 쓴다. `implementation`은 전이되지 않으므로 `impl`을 통해 `api`가 딸려오지 않는다. 두 줄 다 선언해야 한다.

## Decision Log

- 결정: `api` 모듈을 순수 Kotlin(JVM) 모듈로 만든다.
  근거: `api`에는 `@Serializable` 데이터 클래스만 들어간다. Android 의존성이 없으면 빌드가 빠르고, UI 코드를 실수로 넣는 것이 컴파일 단계에서 막힌다. 이 저장소에는 이미 `:domain`과 `:core:common`이 `nevera.kotlin.jvm`으로 운영되는 선례가 있다. 실현 가능성은 M2에서 확인했다.
  날짜/작성자: 2026-08-04 / 이 계획 작성자

- 결정: `api` 모듈에 `navigateToXxx` 같은 `NavController` 확장 함수를 두지 않는다. Route 클래스만 둔다.
  근거: `NavController`와 `NavOptionsBuilder`는 `androidx.navigation`(AAR)에 있어 순수 Kotlin 모듈에서 참조할 수 없다. 확장 함수를 두려면 `api`를 Android 라이브러리로 만들어야 하는데, 그러면 순수 Kotlin의 이점(빠른 빌드, UI 차단)을 전부 잃는다. 호출부가 `navController.navigate(NotificationRoute)`를 직접 쓰는 것으로 충분하다.
  날짜/작성자: 2026-08-04 / 이 계획 작성자

- 결정: `api`의 패키지를 `…feature.notification.api`로 하고, `impl`의 그래프 코드는 `…feature.notification.navigation`에 남긴다.
  근거: 두 모듈이 같은 패키지 이름을 나눠 갖는 상황(split package)을 피한다. 자바와 안드로이드에서 금지는 아니지만 IDE의 자동 완성과 코드 탐색이 혼란스러워진다.
  날짜/작성자: 2026-08-04 / 이 계획 작성자

- 결정: 비교 대상 세 브랜치의 구현 범위를 "알림 화면으로 가는 경로"로 한정한다.
  근거: 여덟 개 feature를 전부 마이그레이션하면 브랜치마다 수백 개 파일이 바뀌어 비교가 불가능해진다. 소비자가 세 곳으로 가장 많은 알림 경로 하나만 세 방식으로 구현하면, 같은 요구에 대한 세 가지 답을 나란히 놓고 볼 수 있다.
  날짜/작성자: 2026-08-04 / 이 계획 작성자

## Outcomes & Retrospective

M4까지 완료. 목표였던 "`:app`이 알림 콜백을 만들지 않는다"와 "의존을 선언한 모듈만 목적지 이름을 본다"를 모두 달성했다.

`api` 모듈은 파일 두 개(`build.gradle.kts`와 Route 하나)로 끝났다. 예상보다 훨씬 작다. 순수 Kotlin으로 만들 수 있다는 것이 이 방식의 가장 큰 수확이다 — Route를 담는 그릇이 Android를 전혀 모르는 상태로 유지되므로, 시간이 지나도 이 모듈이 커질 방법이 없다.

빌드 검사(M4)는 실제로 동작한다. 일부러 `impl` 의존을 넣으면 대안을 알려주는 메시지와 함께 빌드가 멈춘다.

아쉬운 점은 셋이다. 첫째, 디렉터리 이동이 들어가서 diff가 크고 되돌리기가 번거롭다. 둘째, `:app`이 `api`와 `impl` 양쪽을 선언해야 해서 빌드 파일이 한 줄 더 길어진다. 셋째, 이 작업은 알림 하나만 했는데도 마이그레이션 절차가 꽤 길었다. 나머지 네 모듈에 같은 절차를 반복해야 한다.

배운 점: 이 방식의 실제 비용은 모듈 개수가 아니라 **첫 마이그레이션의 절차**다. 두 번째부터는 같은 절차의 반복이라 빨라질 것으로 보인다.

## Context and Orientation

### 이 저장소

Nevera는 Android 앱이다. 냉장고 속 식재료를 영수증 사진으로 등록하고 관리한다. Gradle 멀티모듈 구조이고 모듈 목록은 저장소 루트의 `settings.gradle.kts`에 있다. 작업 시작 시점에 20개다.

화면을 담는 모듈은 `feature/` 아래에 여덟 개 있다. `splash`, `auth`, `main`, `mypage`, `notification`, `sample`, `ingredient`, `fridge`다. `main`이라는 모듈이 담고 있는 화면 이름은 "홈"이다. 모듈 이름과 화면 이름이 다르다.

최상위 모듈은 `app`이다. 화면들을 하나의 흐름으로 연결하는 코드가 여기 있다.

### 화면 이동이 동작하는 방식

이 앱은 Jetpack Compose와 Navigation Compose를 쓴다. 알아야 할 개념은 셋이다.

**Route(목적지 이름)** 는 `@Serializable`이 붙은 코틀린 클래스다. 문자열 경로 대신 클래스를 쓰므로 오타가 컴파일 에러로 잡힌다.

**NavGraphBuilder 확장 함수** 는 "이 Route에 도착하면 이 화면을 그려라"를 등록한다.

**NavController** 는 실제 이동을 실행하는 객체다.

이 셋을 `:app`의 `app/src/main/kotlin/com/anddd/nevera/navigation/NeveraNavHost.kt`가 조립한다.

### 작업 전 상태

`NeveraNavHost.kt`에 다음 코드가 홈·냉장고·마이페이지 각각에 대해 세 번 반복되어 있었다.

    onNavigateToNotification = {
        navController.navigate(NotificationRoute) { launchSingleTop = true }
    },

`feature:main`(홈)이 `NotificationRoute`를 직접 쓰지 못한 이유는 `:feature:notification`에 의존하지 않기 때문이다. 의존하면 알림 화면 UI 구현 전체(12개 파일)를 끌어오게 되므로 하지 않는 것이다.

`feature/main/build.gradle.kts`에는 `implementation(project(":feature:notification"))`이라는 선언이 있었으나 소스 어디에서도 그 패키지를 import 하지 않았다. 누군가 직접 의존을 시도했다가 방식을 바꾸고 선언만 남긴 것으로 보인다. **규칙이 없으면 다음 사람은 이 선언을 보고 해도 되는 것으로 읽는다.** 이 작업의 M4가 그런 선언을 빌드 단계에서 막는다.

### 모듈이 만들어지는 방식

각 feature 모듈의 `build.gradle.kts`는 짧다. `nevera.feature`라는 이 저장소가 직접 만든 Gradle 플러그인이 공통 설정을 넣어 주기 때문이다. 플러그인 소스는 `build-logic/src/main/kotlin/NeveraFeaturePlugin.kt`에 있고, 등록은 `build-logic/build.gradle.kts`의 `gradlePlugin` 블록에서 한다.

순수 Kotlin 모듈용 플러그인도 이미 있다. `nevera.kotlin.jvm`이고 소스는 `build-logic/src/main/kotlin/NeveraKotlinJvmPlugin.kt`다. `:domain`과 `:core:common`이 이걸 쓴다.

## Plan of Work

네 마일스톤으로 나눈다. 각 마일스톤 끝에서 `./gradlew :app:assembleDebug`가 통과해야 한다.

**M1**은 `api` 모듈들이 공통으로 쓸 Gradle 플러그인을 만든다. 하는 일은 코틀린 JVM 플러그인과 직렬화 플러그인을 켜는 것뿐이다. Compose도 Hilt도 넣지 않는다 — **넣지 않는 것이 이 모듈의 존재 이유다.**

**M2**는 알림 모듈을 `api`와 `impl`로 쪼갠다. 이 시점에는 `:app`과 다른 feature의 코드를 바꾸지 않는다. 콜백 방식이 그대로 유지되므로 구조만 바뀌고 동작은 완전히 같다. 문제가 생기면 원인이 "모듈 분리" 하나로 좁혀진다.

**M3**에서 실제 이득이 나온다. 세 소비자 모듈이 `api`를 의존 선언하고 직접 이동하게 한 뒤, `:app`에서 콜백 세 개를 지운다.

**M4**는 규칙을 빌드가 강제하게 한다. `impl`이 다른 feature의 `impl`에 의존하면 빌드를 실패시킨다.

## Concrete Steps

모든 명령은 워크트리 루트 `/Users/juhyeok/AndroidStudioProjects/Nevera-nav-b-api-impl`에서 실행한다.

### M1 — convention plugin

`build-logic/src/main/kotlin/NeveraFeatureApiPlugin.kt`를 만들고, `build-logic/build.gradle.kts`의 `gradlePlugin` 블록에 `nevera.feature.api`로 등록한다.

    ./gradlew :build-logic:build

### M2 — 알림 모듈 분리

    mkdir -p feature/notification/impl
    git mv feature/notification/src feature/notification/impl/src
    git mv feature/notification/build.gradle.kts feature/notification/impl/build.gradle.kts
    mkdir -p feature/notification/api/src/main/kotlin/com/anddd/nevera/feature/notification/api

`settings.gradle.kts`에서 `include(":feature:notification")`을 두 줄로 바꾸고, `app/build.gradle.kts`의 의존도 두 줄로 바꾼다.

    ./gradlew :app:assembleDebug

### M3 — 직접 이동 전환

세 소비자의 `build.gradle.kts`에 `implementation(project(":feature:notification:api"))`를 넣고, 각 navigation 파일을 고친 뒤 `:app`에서 콜백을 지운다.

    ./gradlew :app:assembleDebug

### M4 — 빌드 검사

`NeveraFeaturePlugin.kt`에 구성 시점 검사를 넣는다. 일부러 위반해서 동작을 확인한다.

## Validation and Acceptance

**빌드와 테스트가 통과한다.**

    ./gradlew :app:assembleDebug
    ./gradlew testDebugUnitTest :domain:test :core:common:test :quality:detekt-rules:test
    ./gradlew detekt

이 작업은 테스트 코드를 건드리지 않으므로 통과 테스트 개수가 변경 전과 같아야 한다.

**구조가 바뀌었다.** 이게 핵심 수용 기준이다.

    ./gradlew :feature:main:dependencies --configuration debugCompileClasspath | grep notification

기대 출력은 api 모듈만 나타나는 것이다.

    +--- project :feature:notification:api

`:feature:notification:impl`이 이 목록에 나타나면 실패다. 홈 모듈이 여전히 알림 화면 구현을 끌어오고 있다는 뜻이다.

    grep -c "onNavigateToNotification" app/src/main/kotlin/com/anddd/nevera/navigation/NeveraNavHost.kt

변경 전에는 3, 변경 후에는 0이 나와야 한다.

**규칙이 강제된다.** `feature/main/build.gradle.kts`에 일부러 `implementation(project(":feature:notification:impl"))`을 넣고 빌드하면, 대안을 알려주는 메시지와 함께 실패해야 한다. 확인 후 그 줄을 지운다.

**동작이 그대로다.** 앱을 설치해 홈·냉장고·마이페이지 세 곳의 알림 아이콘을 각각 눌러 알림 목록이 뜨고 뒤로가기로 돌아오는지 확인한다.

## Idempotence and Recovery

각 마일스톤은 커밋으로 구분되어 있으므로 `git revert`로 하나씩 되돌릴 수 있다. 특히 M2의 디렉터리 이동은 `git mv`로 수행했을 때만 깨끗하게 되돌아가므로, 파일 탐색기나 IDE의 드래그로 옮기지 않는다.

빌드가 이상하게 실패하면 모듈 구조 변경 후 흔히 생기는 Gradle 캐시 문제일 수 있다.

    ./gradlew --stop
    rm -rf .gradle build
    ./gradlew :app:assembleDebug

이 워크트리 전체를 버리려면 저장소 루트에서 다음을 실행한다.

    git worktree remove ../Nevera-nav-b-api-impl

## Artifacts and Notes

M4의 빌드 검사가 실제로 동작하는 것을 확인한 출력이다.

    * What went wrong:
    A problem occurred configuring project ':feature:main'.
    > feature impl 모듈은 다른 feature의 impl에 의존할 수 없습니다.
        위반: :feature:main → :feature:notification:impl
        대안: :feature:notification:api 를 사용하세요.
        근거: docs/execplan-nav-b-api-impl.md

새로 만든 `api` 모듈은 파일 두 개다.

    feature/notification/api/build.gradle.kts
    feature/notification/api/src/main/kotlin/com/anddd/nevera/feature/notification/api/NotificationRoute.kt

## Interfaces and Dependencies

새 Gradle 플러그인은 하나다. id는 `nevera.feature.api`, 구현 클래스는 `com.anddd.nevera.buildlogic.NeveraFeatureApiPlugin`이다. 적용하는 것은 `nevera.kotlin.jvm`과 `org.jetbrains.kotlin.plugin.serialization` 둘뿐이고, 의존성은 `kotlinx-serialization-json` 하나다.

새 Gradle 모듈은 `:feature:notification:api`다. 기존 `:feature:notification`은 `:feature:notification:impl`로 경로가 바뀐다. 최종 모듈 수는 20개에서 21개가 된다. (범위 밖인 나머지 네 모듈까지 분리하면 25개가 된다.)

`api` 모듈이 노출하는 것은 하나다. `com.anddd.nevera.feature.notification.api.NotificationRoute` — `@Serializable data object`다.

바뀌는 공개 함수 시그니처는 셋이다.

    fun NavGraphBuilder.homeScreen(
        navController: NavController,
        onNavigateToCamera: () -> Unit,
        onNavigateToGallery: () -> Unit,
    )

    fun NavGraphBuilder.fridgeScreen(
        navController: NavController,
        onNavigateToCamera: () -> Unit,
        onNavigateToGallery: () -> Unit,
        onNavigateToEditIngredient: (Long) -> Unit,
    )

    fun NavGraphBuilder.myPageNavGraph(
        navController: NavController,
        onNavigateToLogin: () -> Unit,
    )

세 함수 모두 `onNavigateToNotification` 파라미터가 사라졌다.

모든 `*Screen` 컴포저블의 시그니처는 바뀌지 않는다. ViewModel, UiState, Intent, SideEffect 등 화면 내부 구조도 건드리지 않는다.
