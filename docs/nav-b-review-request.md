# Navigation 구조 개편 — 중간 리뷰 요청

**브랜치**: `nav/b-api-impl`
**기준**: `feature/navigation` (`45224fe1`)
**작성일**: 2026-08-05
**상태**: Phase 2 완료, Phase 3 착수 전 — **머지 요청이 아니라 방향 검토 요청입니다**

---

## 1. 왜 이 작업을 시작했는가

Navigation 처리에 불편감이 있다는 문제 제기에서 출발했습니다. 코드를 진단해 보니 개별 버그보다 **구조적 원인**이 먼저였습니다.

feature 모듈끼리 가로로 연결될 방법이 없어서, 연결이 필요할 때마다 우회로가 생기고 있었습니다. 네 가지가 실제로 관측됐습니다.

| 우회로 | 증거 |
|---|---|
| 직접 의존 시도 흔적 | `feature/main`이 `:feature:notification`을 의존 선언했으나 소스에서 import 0건 |
| 도메인 계층으로 밀수 | `IngredientFocusEventBus`가 `domain`에 존재. `:app`이 보내고 `feature:fridge`가 받음 |
| 시그니처 관통 | `GoogleAuthClient`가 `:app` 함수 3개를 통과 |
| `core:ui` 적재 | 5개 컴포넌트가 **정확히 2개 feature**만 공유 (범용이 아니라 "올릴 데가 없어서 올린 것") |

**진단**: 모듈이 부족한 게 아니라 **수평 관계를 표현할 어휘가 없다.**

세 가지 개선안을 세우고 각각 별도 워크트리에서 **실제로 구현해 비교**했습니다. 문서만으로 판단하지 않은 이유는, 트레이드오프가 코드를 써보기 전에는 드러나지 않기 때문입니다.

실제로 그 판단이 옳았습니다. 안 C(DI 레지스트리)는 문서 단계에서 유력해 보였으나, 구현 후 **컴파일 타임 검증을 잃으면서도 크로스 feature 이동 문제를 못 푼다**는 것이 드러나 탈락했습니다. 관련 문서는 `docs/navigation-approach-experiment-results.md`에 있습니다.

이 브랜치는 그중 **안 B(feature api/impl 분리)** 를 채택해 전면 적용한 결과입니다.

---

## 2. 무엇을 바꿨는가

커밋 14개, 코드 262파일 (+571 / −240). 문서 제외 수치입니다.

### 2.1 핵심 지표

| 항목 | 기준 | 현재 |
|---|---|---|
| Gradle 모듈 | 20 | 28 |
| `NeveraNavHost.kt` 줄 수 | 115 | **85** |
| `:app`의 인라인 `popUpTo` | 5 | **0** |
| `NavController`를 받는 feature | 3 | **0** |
| feature 간 직접 의존 | 1 | **0** |

### 2.2 구조 변경

**feature 모듈 7개를 `api`/`impl`로 분리했습니다.**

```
feature/notification/
├── api/    ← 목적지 이름(Route)만. 순수 Kotlin 모듈
└── impl/   ← 화면·ViewModel·그래프
```

다른 feature는 `api`만 의존 선언합니다. 홈 모듈이 알림으로 가려면 알림의 **이름**만 알면 되고, **화면 구현 12개 파일은 끌어오지 않습니다.**

```
:feature:main:impl → :feature:notification:api   (impl 아님)
```

**`api`를 순수 Kotlin 모듈로 만들 수 있는지가 최대 미지수였습니다.** Navigation의 타입 안전 라우팅이 비-Android 모듈의 `@Serializable` 클래스를 처리하는지 확인된 바가 없어, 프로토타입 마일스톤으로 분리해 착수 전에 검증했습니다. 결과는 성공이었고, 이게 이 방식의 가장 큰 수확입니다 — **그 모듈에 Compose 코드를 넣는 것이 물리적으로 불가능**해집니다. `core:ui`가 잡동사니가 된 것과 같은 일이 구조적으로 일어날 수 없습니다.

### 2.3 백스택 정책에 이름을 부여

기존에는 화면 전이 정책이 익명 람다 안에 흩어져 있었습니다.

```kotlin
// 이전 — :app의 5곳에 이런 블록이 흩어져 있었다
onNavigateToLogin = {
    navController.navigate(AuthGraphRoute) {
        popUpTo(SplashRoute) { inclusive = true }
    }
},
```

```kotlin
// 현재 — 정책에 이름이 있다
onNavigateToLogin = { navigator.replaceFlow(AuthGraphRoute, clearUpTo = SplashRoute) },
```

이게 단순한 미관 문제가 아닌 이유가 있습니다. 진단 과정에서 찾은 결함 중 **셋이 "정책에 이름이 없어서 생긴 누락"** 이었습니다 — `launchSingleTop` 빠뜨림, `clearBackStack` 빠뜨림, 일관성 없는 옵션 조합. 정책이 한 곳에 이름을 갖고 있으면 그런 누락이 한 번만 발생하고 한 번만 고쳐집니다.

### 2.4 feature의 권한 축소

feature가 `NavController`를 통째로 받으면 전역 백스택을 무엇이든 조작할 수 있습니다. 좁은 `Navigator`로 교체했습니다.

```kotlin
class Navigator(navController: NavController) {
    fun navigate(destination: Any)                          // 이동
    fun goBack()                                            // 뒤로
    fun replaceFlow(destination: Any, clearUpTo: Any)       // 흐름 교체
}
inline fun <reified T : Any> Navigator.replaceStep(destination: Any)  // 단계 교체
```

`ingredient`의 `popUpTo` 3곳이 전부 "흐름의 한 단계를 교체"라는 같은 의미여서 `replaceStep`으로 통일했습니다.

```kotlin
navigator.replaceStep<OcrCaptureRoute>(IngredientRoute(uri.toString()))
```

> 타입 파라미터를 쓴 이유가 있습니다. `popUpTo(인스턴스)`로 하면 `OcrCaptureRoute(openGallery = true)`로 진입했을 때 인자가 달라 매칭되지 않고 스택에 남습니다.

### 2.5 규칙을 빌드가 강제

`impl`이 다른 feature의 `impl`에 의존하면 빌드가 멈춥니다.

```
> feature impl 모듈은 다른 feature의 impl에 의존할 수 없습니다.
    위반: :feature:main:impl → :feature:notification:impl
    대안: :feature:notification:api 를 사용하세요.
```

**에러 메시지에 대안을 함께 담은 것이 의도입니다.** 막기만 하면 다음 사람은 또 다른 우회로를 찾습니다. §1의 우회로 4건이 전부 그렇게 생겼습니다.

일부러 위반시켜 동작을 확인했습니다.

### 2.6 의존성 상향 (Nav3 준비)

Compose BOM `2024.09.00` → `2026.03.00` (Compose 1.7.2 → 1.10.5), lifecycle → 2.10.0.

**아키텍처 변경과 섞지 않고 단독 커밋으로 분리한 것이 핵심 판단이었습니다.** 상향하자마자 디자인시스템 전체가 깨졌는데, Nav3와 무관한 원인이었습니다 — `material3` 1.4부터 `material-icons-core`를 전이 의존으로 가져오지 않습니다. 명시 선언 한 줄로 해결됐지만, Nav3 작업과 한 커밋에 있었다면 원인을 가리기 어려웠을 것입니다.

---

## 2.7 `Navigator`는 NIA를 그대로 가져온 것인가 — 비교

**아이디어는 채택했고, 구현은 다릅니다.** 라이브러리 세대가 다르고 앱의 화면 구조도 달라서 그대로 옮길 수 없었습니다. 어디까지 같고 어디부터 다른지 정리합니다.

### 2.7.1 Now in Android의 구조

NIA는 Navigation 3을 씁니다. 백스택이 `NavController` 안에 감춰져 있지 않고, **관찰 가능한 리스트**입니다. 그래서 `Navigator`가 하는 일이 "리스트 조작"입니다.

```kotlin
// core/navigation/Navigator.kt — 전문 (주석 제외)
class Navigator(val state: NavigationState) {

    fun navigate(key: NavKey) {
        when (key) {
            state.currentTopLevelKey -> clearSubStack()   // 현재 탭을 다시 누름 → 탭 루트로
            in state.topLevelKeys    -> goToTopLevel(key) // 다른 탭으로 전환
            else                     -> goToKey(key)      // 현재 탭 안에서 이동
        }
    }

    fun goBack() {
        when (state.currentKey) {
            state.startKey        -> error("You cannot go back from the start route")
            state.currentTopLevelKey -> state.topLevelStack.removeLastOrNull()
            else                  -> state.currentSubStack.removeLastOrNull()
        }
    }

    private fun goToKey(key: NavKey) {
        state.currentSubStack.apply { remove(key); add(key) }   // 중복 제거 후 맨 뒤로
    }

    private fun goToTopLevel(key: NavKey) {
        state.topLevelStack.apply {
            if (key == state.startKey) clear() else remove(key)
            add(key)
        }
    }

    private fun clearSubStack() {
        state.currentSubStack.run { if (size > 1) subList(1, size).clear() }
    }
}
```

상태는 별도 클래스가 들고 있습니다. 탭마다 독립된 서브스택이 있고, 탑레벨 스택이 어느 탭을 보여줄지 고릅니다.

```kotlin
class NavigationState(
    val startKey: NavKey,
    val topLevelStack: NavBackStack<NavKey>,          // 탭 방문 순서
    val subStacks: Map<NavKey, NavBackStack<NavKey>>, // 탭별 화면 스택
)
```

### 2.7.2 우리 프로젝트의 구조

우리는 Navigation Compose(2.9.0)를 씁니다. 백스택이 `NavController` 내부에 감춰져 있고 `NavOptions`로 간접 조작합니다. 그래서 `Navigator`가 하는 일이 **"옵션 조합에 이름 붙이기"** 입니다.

```kotlin
class Navigator(@PublishedApi internal val navController: NavController) {

    fun navigate(destination: Any) {
        navController.navigate(destination) { launchSingleTop = true }
    }

    fun goBack() {
        navController.popBackStack()
    }

    fun replaceFlow(destination: Any, clearUpTo: Any) {
        navController.navigate(destination) {
            popUpTo(clearUpTo) { inclusive = true }
            launchSingleTop = true
        }
    }
}

inline fun <reified T : Any> Navigator.replaceStep(destination: Any) {
    navController.navigate(destination) {
        popUpTo<T> { inclusive = true }
        launchSingleTop = true
    }
}
```

### 2.7.3 같은 점

**세 가지가 같습니다.**

첫째, **feature가 `NavController`가 아니라 좁은 객체를 받습니다.** 이게 이 패턴의 핵심이고, 그대로 채택했습니다. feature는 전역 백스택을 임의로 조작할 수 없습니다.

둘째, **백스택 정책이 한 클래스에 모입니다.** 호출부는 "무엇을 할지"만 말하고 "어떻게 스택이 바뀌는지"는 `Navigator`가 정합니다.

셋째, **`:app`이 `remember`로 한 번만 만들어 내려보냅니다.** DI 컨테이너에 넣지 않습니다. 두 프로젝트 모두 `NavController`/`NavigationState`가 Compose 생명주기에 묶인 객체라 싱글턴이 참조하면 안 되기 때문입니다.

### 2.7.4 다른 점

| 항목 | Now in Android | 우리 |
|---|---|---|
| 라이브러리 | Navigation 3 (`NavKey`, 리스트 백스택) | Navigation Compose 2.9.0 (`NavController`) |
| 감싸는 대상 | `NavigationState` (명시적 스택 2종) | `NavController` (감춰진 스택) |
| 공개 메서드 | **2개** (`navigate`, `goBack`) | **4개** (`navigate`, `goBack`, `replaceFlow`, `replaceStep`) |
| 탭 전환 정책 | **`Navigator` 안에** | **`:app`에 남아 있음** |
| 다단계 흐름 지원 | 없음 (필요 없는 구조) | `replaceStep<T>` |
| 중복 방지 | `remove(key); add(key)` | `launchSingleTop = true` |

**차이가 생긴 이유를 두 가지로 설명할 수 있습니다.**

**(1) 라이브러리 세대.** Nav3는 백스택이 리스트라 `remove(key); add(key)` 한 줄로 "이미 있으면 맨 뒤로 옮기기"가 됩니다. Nav2에는 그 연산이 없어서 `launchSingleTop`, `popUpTo`, `saveState` 같은 옵션 조합으로 표현해야 하고, 조합마다 이름이 필요해집니다. **우리 `Navigator`의 메서드가 2개가 아니라 4개인 건 설계 실패가 아니라 Nav2의 표현력 때문입니다.**

**(2) 앱의 화면 구조.** NIA는 탭 4개 + 상세 화면이라 깊이가 얕습니다. 우리는 OCR 등록처럼 **촬영 → 인식결과 → 완료**로 이어지는 다단계 흐름이 있고, 각 단계는 되돌아갈 수 없어야 합니다. NIA에는 이 사례가 없어서 대응하는 메서드도 없습니다. `replaceStep<T>`는 우리 도메인 때문에 추가한 것입니다.

### 2.7.5 아직 못 따라간 것 — 탭 전환 정책

**이게 지금 구조의 가장 뚜렷한 미완성 지점입니다.**

NIA의 `navigate()`는 목적지가 탭인지 아닌지를 스스로 판단합니다. 그래서 어떤 feature가 `navigator.navigate(ForYouNavKey)`를 호출하면 알아서 탭 전환으로 처리됩니다.

우리 `navigate()`는 그런 분기가 없습니다. 무조건 스택에 쌓습니다. 탭 전환은 여전히 `:app`에 남아 있습니다.

```kotlin
// NeveraApp.kt:76 — Navigator를 거치지 않는다
onItemClick = { destination ->
    navController.navigate(destination.route) {
        popUpTo<HomeRoute> { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
},
```

**당장의 문제는 없습니다.** 현재 feature가 `navigate()`에 넘기는 목적지는 알림·설정·상세뿐이고 탭은 없습니다. 그러나 **잠재적 함정입니다** — 누군가 `navigator.navigate(FridgeRoute)`를 호출하면 탭 전환이 아니라 냉장고 화면이 스택 위에 쌓입니다. 바텀바 선택 상태와 실제 화면이 어긋납니다.

두 가지 대응이 가능합니다.

- **지금**: `Navigator`에 탑레벨 목적지 집합을 알려주고 `navigate()`가 NIA처럼 분기하게 한다. Nav2에서도 가능합니다.
- **Nav3 이후**: `NavigationState`를 도입하면 자연히 해결됩니다.

리뷰에서 이 부분의 우선순위를 판단해 주셨으면 합니다. §5에 항목으로 넣었습니다.

---

## 3. 판단이 바뀐 지점 (되돌린 결정)

작업 도중 두 번 방향을 수정했습니다. 근거와 함께 기록합니다.

### 3.1 "안 A가 낫다"는 권고를 뒤집었습니다

문서 분석 단계에서는 규모(모듈 20개, 리뷰어 3인) 대비 비용을 이유로 **안 A(`:core:navigation` 단일 모듈)를 권고**했습니다.

뒤집은 근거는 Now in Android의 현행 코드입니다. NIA가 이미 **api/impl 분리를 채택**했고, 목적지 이름은 `api`에, 이동은 `core:navigation`의 `Navigator`에 두는 구조로 이동해 있었습니다. 저희가 도달한 구조와 같습니다.

### 3.2 raw `NavController`를 넘기던 것을 `Navigator`로 교체했습니다

Phase 1에서 feature에 `NavController`를 통째로 넘겼는데, 이는 **공식 가이드와 다른 형태**였습니다. [Encapsulate your navigation code](https://developer.android.com/guide/navigation/design/encapsulate)는 콜백을 넘기라고 합니다.

다만 콜백만 쓰면 `:app`이 모든 연결을 중재해야 해서 이번 작업의 목적과 충돌합니다. NIA가 택한 절충이 `Navigator` — 권한을 좁히되 feature가 스스로 이동하게 하는 것 — 이고, 그쪽으로 맞췄습니다.

---

## 4. 검증 결과 — 무엇을 확인했고 무엇을 못 했는가

### 확인한 것

Pixel 9 / API 36 에뮬레이터에서 실제 설치·실행했습니다.

| 항목 | 결과 |
|---|---|
| 빌드 / 단위 테스트 / detekt | 통과 (테스트 **423개** 실행, 파일 46개 그대로) |
| 앱 실행 · 스플래시 → 로그인 | 정상 |
| 홈 → 알림 이동 | 정상 |
| 마이페이지 → 알림 이동 | 정상 |
| Compose 1.10.5 시각적 회귀 | 없음 (기준 브랜치와 렌더링 동일) |
| FATAL EXCEPTION | 0건 |
| 의존 격리 (`impl` 유출) | 없음 |
| 빌드 검사 실동작 | 확인 (일부러 위반) |

### 확인하지 못한 것

로그인 화면에서 막혀 `startDestination`을 임시로 `Home`으로 바꿔 우회했습니다(검증 후 원복). 따라서 다음은 **미검증**입니다.

- 실제 로그인 흐름 (스플래시 → 자동 로그인 → 홈)
- 인증 상태에서의 알림 목록 표시
- 딥링크 경로
- 바텀 탭 상태 보존(`saveState`/`restoreState`)
- **OCR 등록 흐름 전체** — `replaceStep`으로 바꾼 부분이라 우선 검증 대상입니다

계정으로 로그인한 뒤 이어서 검증이 필요합니다.

### 검증 중 발견한 결함 (이 브랜치와 무관)

**냉장고 화면의 알림 버튼이 반응하지 않습니다.** 처음엔 제 회귀를 의심했으나, 기준 브랜치를 그대로 설치해 같은 조작을 했더니 동일하게 재현됐습니다.

```kotlin
// FridgeViewModel.kt:138 — 서버 호출이 먼저다
private fun navigateToNotification() = intent {
    markAllNotificationsAsRead()          // 실패하면 여기서 중단
    postSideEffect(FridgeSideEffect.NavigateToNotification)
}

// HomeViewModel.kt:120 — 바로 발화한다
private fun onNotificationIconClick() = intent {
    postSideEffect(HomeSideEffect.NavigateToNotification)
}
```

읽음 처리 API가 실패하면 `postSideEffect`에 도달하지 못해 화면 이동이 막힙니다. 네트워크가 불안정하거나 세션이 만료된 사용자에게 "알림 버튼이 가끔 안 눌려요"로 나타날 결함입니다.

**이 브랜치의 Navigation 리팩터링과 별개의 MVI 문제라 고치지 않았습니다.** 읽음 처리를 `postSideEffect` 뒤로 옮길지, 실패를 무시할지는 제품 판단이 필요합니다(읽음 처리에 실패해도 화면은 열려야 하는가).

---

## 5. 리뷰에서 봐주셨으면 하는 것

우선순위 순입니다.

**첫째, 모듈 20 → 28개가 감당 가능한 비용인가.** 이게 이 방식의 가장 실질적인 대가입니다. 새 feature를 만들 때마다 빌드 파일을 2개 씁니다. 다만 완화 조건이 있습니다 — 다른 feature가 목적지로 삼기 전까지는 `api`를 만들지 않아도 됩니다.

**둘째, `api`에 무엇을 올릴지의 기준.** "다른 모듈이 그 이름을 쓰는가" 하나로 정했습니다. `AppInfoRoute`·`OcrErrorRoute`처럼 그래프 안에서만 쓰이는 것은 `impl`에 남겼습니다. 이 기준이 실무에서 유지될 만한지 봐주십시오.

**셋째, `Navigator`의 API 표면과 탭 전환 정책의 소속.** 지금 4개(`navigate`/`goBack`/`replaceFlow`/`replaceStep`)입니다. NIA는 2개인데, 차이의 이유는 §2.7.4에 정리했습니다(Nav2의 표현력 + 우리 앱의 다단계 흐름).

더 중요한 건 **탭 전환이 아직 `Navigator` 밖에 있다**는 점입니다(§2.7.5). 지금은 문제가 없지만, 누군가 `navigator.navigate(FridgeRoute)`를 호출하면 탭 전환이 아니라 스택 push가 되어 바텀바 선택 상태와 화면이 어긋납니다. 이걸 지금 막을지, Nav3 마이그레이션까지 미룰지 판단이 필요합니다.

**넷째, Compose 1.10.5 상향의 영향 범위.** Nav3 요건 때문에 필요했지만 마이너 3단계 점프입니다. 디자인시스템 담당자의 확인이 필요합니다.

---

## 6. 남은 작업과 방향

### 6.1 즉시 필요 (이 브랜치 안)

**인증 상태에서의 실행 검증.** 특히 OCR 등록 흐름은 `replaceStep`으로 백스택 정책을 바꾼 부분이라 반드시 확인해야 합니다.

**냉장고 알림 버튼 결함 처리 방향 결정.** 이 브랜치에서 고칠지, 별도 이슈로 뺄지 정해야 합니다. 기준 브랜치에도 있는 문제이므로 후자가 깔끔해 보입니다.

### 6.2 Phase 3 — Navigation 3 마이그레이션

준비는 끝났습니다(의존성 상향 완료). 남은 것은 네 단계입니다.

| 단계 | 내용 | 난이도 |
|---|---|---|
| M10 | Route를 `NavKey`로, `Navigator`를 Nav3 API로 | 보통 |
| M11 | `NavGraphBuilder` 확장 → `EntryProviderScope` 확장 | 보통 |
| M12 | `NavHost` → `NavDisplay`, 바텀 탭 상태 보존 재설계 | **높음** |
| M13 | 딥링크를 Nav3 백스택 합성으로 | 낮음 |

**M12가 최대 난관입니다.** Nav3에는 `saveState`/`restoreState`가 없습니다. 백스택이 관찰 가능한 리스트라, 지금 `popUpTo(HomeRoute) { saveState = true }`로 처리하는 탭 상태 보존을 **탭마다 서브스택을 두고 갈아 끼우는 구조**로 다시 짜야 합니다. NIA의 `NavigationState(topLevelStack, subStacks)`가 그 답입니다.

반대로 **M13은 Nav3가 확실히 단순해집니다.** 현재 딥링크는 `while (popBackStack())` 루프로 백스택을 손질하는데, 리스트를 원하는 모양으로 직접 조립하는 것으로 바뀝니다.

**다만 Nav3 착수 여부는 별도 판단이 필요하다고 봅니다.** Phase 2만으로도 §1에서 진단한 문제는 해결됐습니다. Nav3는 그 위에 얹는 별개의 투자이고, 라이브러리가 정식 버전(1.0.0)이긴 하나 생태계가 아직 얇습니다. Phase 2를 먼저 안정화한 뒤 결정하는 편이 안전합니다.

### 6.3 이 브랜치 밖 — 별도 이슈 권장

진단 과정에서 찾은 결함 중 **이번 리팩터링으로 해결되지 않은 것들**입니다. 상세는 `docs/navigation-architecture-review.md`에 있습니다.

| 등급 | 내용 |
|---|---|
| P0 | 로그아웃 상태에서 딥링크로 인증 벽을 넘을 수 있음 |
| P0 | 화면 회전만으로 딥링크가 재실행되고 진행 중이던 화면이 파괴됨 |
| P0 | OCR 등록 완료 시 `HomeRoute`가 백스택에 중복 push (냉장고 탭 진입 시) |
| P0 | 로그아웃 후에도 이전 계정의 탭 상태가 복원됨 |

**세 번째 항목은 이 브랜치에서 `NOTE` 주석으로 표시만 남겼습니다.** `launchSingleTop`은 대상이 백스택 최상단일 때만 동작하므로 한 줄로는 해결되지 않고, "홈으로 간다"인지 "왔던 곳으로 돌아간다"인지 **제품 결정**이 선행돼야 합니다.

이 넷은 모두 백스택을 단언하는 테스트 한 개면 잡혔을 종류입니다. Navigation 테스트가 현재 사실상 0건이라, **Phase 2 다음 투자로는 Nav3보다 테스트 베이스라인이 우선**이라고 봅니다.

---

## 7. 참고 문서

| 문서 | 내용 |
|---|---|
| `docs/navigation-architecture-review.md` | 현재 Navigation 진단 (P0 4건 포함) |
| `docs/navigation-module-architecture.md` | 모듈 경계 분석, 3개 안 제시 |
| `docs/navigation-approach-comparison.md` | 3개 안 상세 비교 + 예제 코드 |
| `docs/navigation-approach-experiment-results.md` | 3개 안 실제 구현 결과 |
| `docs/execplan-navigation-modularization.md` | 이 브랜치의 실행 계획 (결정 로그·발견 사항 포함) |

비교용 브랜치 `nav/a-core-navigation`, `nav/c-di-registry`도 남아 있습니다.
