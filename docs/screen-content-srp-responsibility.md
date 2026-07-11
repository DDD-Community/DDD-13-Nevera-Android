# Screen과 Content의 책임 정의 — SRP(단일 책임 원칙) 관점

이 문서는 `*Screen`과 `*Content`가 각각 어떤 책임을 가져야 하는지를 SOLID의 SRP(Single Responsibility Principle)로 정의합니다. 이후 코드를 구현하거나 화면을 설계할 때 "이 코드는 어디에 있어야 하는가"를 판단하는 기준으로 사용합니다.

## 0. 요약

- **Content**: 값(`UiState`)을 받아서 "어떻게 그릴지"만 결정하는 함수. ViewModel도, Context도, Navigation도 모릅니다.
- **Screen**: Content를 ViewModel·Navigation·시스템 API에 "연결"만 하는 함수. 레이아웃 코드를 직접 갖지 않습니다.
- 이 둘을 나누지 않고 Screen이 렌더링까지 다 하면 → Preview가 안 되고, 테스트가 무거워지고, 코드 리뷰 인지 부하가 커집니다. (5장)
- 반대로 Content 안에서 Toast/Dialog/SnackBar를 직접 띄우면 → 같은 메시지가 여러 번 뜨는 실제 버그가 생기고, Preview가 깨집니다. (6장)

## 1. SRP를 어떻게 적용할 것인가

SRP는 흔히 "하나의 클래스는 한 가지 일만 해야 한다"로 오해되지만, Robert C. Martin이 실제로 정의한 형태는 다릅니다.

> **모듈은 하나의, 오직 하나의 액터(actor)에 대해서만 책임져야 한다.**

여기서 액터는 "그 모듈의 변경을 요구할 수 있는 한 사람 또는 한 그룹"을 뜻합니다. 쉽게 말하면 **"이 파일을 고쳐 달라고 요청할 수 있는 사람이 몇 종류인가"**가 기준입니다. 한 파일을 고쳐야 하는 이유가 서로 무관한 두 가지 이상이라면, 그 파일은 이미 두 가지 책임을 지고 있는 것입니다.

널리 알려진 예시로, `Employee` 클래스에 `calculatePay()`(재무팀이 요구), `reportHours()`(인사팀이 요구), `save()`(DBA/인프라팀이 요구)가 한 클래스에 뒤섞여 있으면, 세 그룹 중 누구의 요청으로 코드를 고치든 나머지 두 그룹과 무관한 코드까지 재검토 대상이 됩니다. 이것이 SRP 위반입니다.

이 기준을 Screen/Content에 적용하려면 다음 질문에 답해야 합니다.

> **이 파일을 변경하라고 요청할 수 있는 사람(그룹)은 누구인가? 그 요청은 몇 종류인가?**

## 2. Content의 책임과 액터

**책임**: 주어진 `UiState`(값)를 입력으로 받아, 화면에 무엇을 어떻게 시각적으로 배치·표현할 것인지를 결정한다.

**액터**: 화면의 시각적 표현을 요구하는 쪽 — 디자인/제품(UX). "이 배너를 상단으로 옮겨줘", "이 상태일 때는 스켈레톤을 보여줘", "이 텍스트 스타일을 바꿔줘" 같은 요청을 하는 사람들입니다.

쉽게 말하면, Content는 **"재료(UiState)를 받아서 요리(UI)를 만드는 요리사"**입니다. 어떤 재료가 왜 들어왔는지(왜 로딩 중인지, 왜 에러가 났는지)는 몰라도 되고, 알 필요도 없습니다. 재료가 주어지면 그것을 어떻게 담아낼지만 결정합니다.

| 이 변경은 Content를 바꾸는가? | 예 | 아니오 |
|---|---|---|
| 레이아웃/여백/컬러/타이포그래피 변경 | ✅ | |
| 새로운 `UiState` 필드를 화면에 반영하는 렌더링 추가 | ✅ | |
| 로딩/에러/빈 상태의 시각적 처리 변경 | ✅ | |
| Navigation 대상 화면이 바뀜 | | ✅ |
| ViewModel 주입 방식(Hilt → Koin 등)이 바뀜 | | ✅ |
| 시스템 권한 요청 흐름이 바뀜 | | ✅ |

Content는 "무엇을 왜 보여주는가"라는 비즈니스 판단은 하지 않습니다. 그 판단은 이미 `UiState`라는 값으로 확정되어 들어옵니다. Content가 하는 일은 오직 "그 값을 어떻게 그릴 것인가"입니다.

## 3. Screen의 책임과 액터

**책임**: Content(와 그 하위 컴포넌트)를 앱의 실행 환경 — ViewModel 생명주기, Navigation 그래프, 플랫폼 시스템 API(권한, Toast, ActivityResult) — 에 결합한다.

**액터**: 화면이 앱/플랫폼과 어떻게 연결되는지를 요구하는 쪽 — Navigation·아키텍처 구조를 결정하는 엔지니어링, 그리고 Android OS 자체가 강제하는 제약(권한 모델, 생명주기 API 변경 등).

쉽게 말하면, Screen은 **"주방(Content)에 재료를 배달하고, 완성된 요리를 손님(화면)에게 내가는 웨이터"**입니다. 재료를 어떻게 요리할지는 관여하지 않고, 대신 "재료가 어디서 오는지"(ViewModel), "다음에 어느 테이블로 가야 하는지"(Navigation), "주방에 불이 나면 어떻게 대응할지"(권한 요청, 시스템 다이얼로그) 같은, 요리 자체와 무관한 일들을 책임집니다.

| 이 변경은 Screen을 바꾸는가? | 예 | 아니오 |
|---|---|---|
| Navigation 콜백 시그니처가 바뀜 | ✅ | |
| ViewModel 구독/주입 방식이 바뀜 | ✅ | |
| 권한 요청, `ActivityResultLauncher`, 시스템 Toast 처리 방식이 바뀜 | ✅ | |
| 레이아웃/컬러/타이포그래피 변경 | | ✅ |
| 새로운 `UiState` 필드를 화면에 반영하는 렌더링 추가 | | ✅ |

Screen은 "무엇을 보여줄지" 자체를 판단하지 않습니다. 그 판단은 ViewModel(비즈니스 로직)이 이미 내려서 `UiState`/`SideEffect`로 넘겨줍니다. Screen이 하는 일은 오직 "그 결과를 실행 환경에 연결하는 것"입니다.

## 4. 검증: 두 책임이 실제로 독립적으로 변경되는가

SRP를 제대로 적용했다면, 한쪽 액터의 요구로 인한 변경이 다른 쪽 파일에 영향을 주지 않아야 합니다.

| 변경 요청 | 요청 주체 | 영향받는 파일 |
|---|---|---|
| "위시 배너 디자인을 카드형으로 바꿔줘" | 디자이너 | Content만 |
| "카메라 권한을 앱 시작 시 미리 요청하도록 바꿔줘" | 플랫폼/엔지니어링 | Screen만 |
| "홈 화면 진입 시 특정 재료 ID로 스크롤되게 해줘" | 제품(딥링크 요구사항) | Navigation 시그니처(Screen) + 해당 값을 반영하는 Content 둘 다 걸릴 수 있음 — 7장 참고 |
| "ViewModel 주입을 Hilt에서 다른 프레임워크로 바꿔줘" | 아키텍처 | Screen만 |
| "로딩 중에는 스켈레톤을 보여줘" | 디자이너 | Content만 |

대부분의 시나리오에서 한쪽만 바뀌는 것이 확인됩니다.

## 5. Screen에 Content가 없으면 생기는 문제

"Content가 없다"는 것은 파일을 아예 안 만든다는 뜻이 아니라, **Screen 함수 하나가 ViewModel 구독부터 실제 레이아웃(`Scaffold`, `LazyColumn`, 각종 `Text`/`Button` 배치)까지 전부 담당한다**는 뜻입니다.

### 5.1 나쁜 예

```kotlin
@Composable
fun HomeScreen(
    onNavigateToCamera: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value
    var showBottomSheet by remember { mutableStateOf(false) }

    // 렌더링 로직이 전부 Screen 안에 있다
    Scaffold(
        topBar = { NeveraLogoAppBar(...) },
        floatingActionButton = { NeveraAddIngredientFab(onClick = { showBottomSheet = true }) },
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item { WishBanner(nickname = uiState.profile.nickname, wish = uiState.wish, ...) }
            item { RescueDisposalCostCard(...) }
            // ... 화면의 모든 UI가 여기 이어진다
        }
    }
}
```

### 5.2 구체적으로 어떤 문제가 생기는가

**Preview가 사실상 불가능해집니다.** `HomeScreen`의 `viewModel` 파라미터 기본값이 `hiltViewModel()`이기 때문에, `@Preview`를 붙여 실행하면 실제 Hilt DI 그래프가 필요합니다. 안드로이드 스튜디오의 Preview는 앱을 실제로 실행하지 않으므로 이 함수는 정상적으로 렌더링되지 않거나 크래시가 납니다. 디자이너가 "로딩 상태일 때 어떻게 보이는지" 확인하고 싶어도, 이 구조에서는 실제로 로딩 상태를 만들어내야만(네트워크를 느리게 하거나 디버그 코드를 심거나) 눈으로 볼 수 있습니다. `HomeContent(uiState = HomeUiState(isLoading = true), onIntent = {})`처럼 값만 바꿔서 즉시 확인하는 것이 불가능합니다.

**Compose UI 테스트가 무겁고 깨지기 쉬워집니다.** "에러 메시지가 화면에 뜨는지"를 테스트하고 싶다면, Content가 있을 때는 `composeTestRule.setContent { HomeContent(uiState = HomeUiState(errorMessage = "..."), onIntent = {}) }`로 그 상태를 바로 만들어 검증하면 됩니다. Content가 없으면 실제 ViewModel의 UseCase 호출까지 모킹해서 해당 에러 상태에 "도달"시켜야 하고, 테스트 하나가 ViewModel 내부 로직 변경에도 함께 깨지는 취약한 테스트가 됩니다.

**재사용이 불가능해집니다.** 만약 나중에 같은 리스트 UI를 다른 화면(예: 위젯, 다른 탭)에서도 보여줘야 한다면, 렌더링 로직만 떼어내 재사용하고 싶어도 `viewModel`과 `Context` 의존성이 같은 함수 안에 엉켜 있어 분리 자체가 새로운 리팩터링 작업이 됩니다.

**코드 리뷰의 인지 부하가 커집니다.** 리뷰어가 "화면이 어떻게 그려지는지"만 확인하고 싶어도, 같은 함수 안에 있는 DI 설정, 권한 체크, `LocalContext` 사용 코드까지 함께 읽어야 실제 레이아웃 로직을 파악할 수 있습니다.

**재구성(recomposition) 범위가 넓어집니다.** 바텀시트 visibility 같은 로컬 state가 바뀌면 Screen 전체가 재구성 후보가 되고, 그 안에 있는 레이아웃 코드 전체가 다시 실행될 가능성이 커집니다. Content로 분리했다면 이런 로컬 state 변경이 레이아웃 코드에 영향을 주지 않습니다.

> **자동 검증**: 이 위반은 detekt가 잡아냅니다 — Screen이 Content를 호출하지 않으면 `ScreenDelegatesToContentRule`, Screen 파일에 Scaffold가 있으면 `ScreenNoScaffoldRule`이 빌드를 실패시킵니다.

## 6. Content 내부에서 Dialog/BottomSheet/Toast/SnackBar를 처리하면 생기는 문제

이 문제는 5장보다 더 직접적으로 **실제 런타임 버그**를 만들어냅니다. 이유를 이해하려면 Compose의 기본 규칙 하나를 먼저 알아야 합니다.

> **Composable 함수는 한 번만 실행된다는 보장이 없습니다.** 화면과 무관한 다른 상태가 바뀌어도, Compose는 필요에 따라 같은 Composable 함수를 몇 번이든 다시 실행(recompose)할 수 있습니다.

이 규칙 때문에, Composable 함수 본문 안에서 `Toast.makeText(...).show()` 같은 **명령형(imperative) 코드를 직접 실행**하면, 그 코드는 "한 번"이 아니라 "이 함수가 재실행되는 횟수만큼" 실행됩니다.

### 6.1 나쁜 예 — Toast가 여러 번 뜨는 버그

```kotlin
@Composable
internal fun HomeContent(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
) {
    val context = LocalContext.current

    if (uiState.errorMessage != null) {
        Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_SHORT).show()  // ❌
    }

    LazyColumn { /* ... */ }
}
```

`uiState.errorMessage`가 한 번 세팅된 뒤, 사용자가 스크롤을 하거나 다른 필드(`hasUnreadNotification` 등)가 바뀌어 `HomeContent`가 재구성될 때마다 이 `if` 블록이 다시 평가되고, **같은 에러 메시지의 Toast가 계속 다시 뜹니다.** 값을 다시 `null`로 되돌리는 코드도 없기 때문에, 한 번 에러가 나면 화면이 재구성될 때마다 Toast가 반복해서 나타나는 눈에 보이는 버그가 됩니다. BottomSheet나 SnackBar를 같은 방식으로 직접 열어도 동일한 문제가 생깁니다.

### 6.2 좋은 예 — Screen에서 SideEffect로 1회성 처리

```kotlin
// Screen
viewModel.collectSideEffect { effect ->
    when (effect) {
        is HomeSideEffect.ShowError ->
            Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()  // ✅
        // ...
    }
}
```

`collectSideEffect`가 구독하는 것은 Compose의 recomposition과 무관하게 동작하는 **이벤트 스트림(Flow)** 입니다. ViewModel이 `postSideEffect(...)`를 정확히 한 번 호출하면, 이 코드도 정확히 한 번만 실행됩니다. "화면이 몇 번 다시 그려지는가"와 "이 이벤트가 몇 번 처리되는가"가 완전히 분리되는 것이 핵심입니다.

### 6.3 Preview가 실제로 깨집니다

6.1의 나쁜 예처럼 `Toast.makeText(...).show()`가 조건문 안에서 바로 실행되면, `@Preview`로 `HomeContent(uiState = HomeUiState(errorMessage = "네트워크 오류"))`를 렌더링하는 순간 Preview 환경에서도 이 코드가 그대로 실행을 시도합니다. Preview는 실제 Android 런타임이 아니므로 이 호출은 무의미하거나 Preview 렌더링 자체를 실패시킬 수 있습니다. 즉 "에러 메시지가 있을 때의 화면"을 Preview로 확인하고 싶어서 만든 상태값이, 오히려 그 상태를 Preview로 볼 수 없게 만드는 원인이 됩니다.

### 6.4 Content의 파라미터 계약이 깨집니다

Dialog/BottomSheet를 Content 안에서 열려면 Content가 다음 중 하나를 해야 합니다.

- `viewModel`이나 SideEffect 스트림을 직접 구독한다 → Content가 "`uiState`와 Intent 람다만 받는 함수"라는 계약을 깨고 ViewModel에 의존하게 됩니다. 2장에서 정의한 Content의 책임(오직 디자인 액터에만 응답)이 무너지고, ViewModel 구현이 바뀌어도 Content가 함께 바뀌어야 하는 상황이 생깁니다.
- Screen이 만든 로컬 state를 파라미터로 받는다 → Content의 파라미터 목록에 "SideEffect에서 파생된 값"이 섞이며, 그 값이 왜 `true`가 됐는지 이해하려면 결국 Screen을 열어봐야 합니다. 파일만 두 개로 늘고 실질적으로 얻는 것이 없습니다.

> **자동 검증**: 이 위반은 detekt가 잡아냅니다 — Content/Component에서 `collectSideEffect`·`collectAsState`·`hiltViewModel`을 호출하면 `ViewModelAccessOnlyInScreenRule`, Screen 밖에서 `Toast.makeText`를 호출하면 `ToastOutsideScreenRule`, Content 파라미터에 허용 외 타입이 섞이면 기존 `ContentComposableParameterRule`이 빌드를 실패시킵니다.

## 7. 경계에서 발생하는 긴장 — 정직하게 다루기

SideEffect로 트리거되는 Dialog/BottomSheet는 겉보기에 Screen과 Content 양쪽 액터가 동시에 얽힌 것처럼 보입니다. "어떤 바텀시트를 보여줄지, 그 안에 어떤 문구가 들어갈지"는 디자인/제품이 결정하고, "그것을 화면에 실제로 띄우는 매커니즘(Toast는 `Context` 필요, 바텀시트 visibility는 flag 필요)"은 플랫폼 구조에 속하기 때문입니다.

이걸 SRP 위반으로 볼 수도 있지만, 실제로는 **"무엇을(what)"과 "언제/어떻게 조립할 것인가(when/mechanism)"가 서로 다른 파일에 이미 분리**되어 있다면 위반이 아닙니다.

- 바텀시트의 실제 디자인(내부 레이아웃, 문구, 인터랙션)은 Content와 같은 액터(디자인)에 속하는 **별도 컴포넌트 파일**에 있어야 합니다. 이 파일이 바뀌는 이유는 여전히 디자이너의 요청 하나뿐입니다.
- Screen은 그 컴포넌트 파일을 "지금 조립대에 올릴지 말지"만 결정합니다(6.2의 `collectSideEffect` 예시처럼). Screen이 바뀌는 이유는 "언제 트리거되는가"라는 플랫폼/흐름 제어 관심사 하나뿐이고, 컴포넌트 내부 디자인이 바뀌어도 Screen은 단 한 줄도 바뀔 필요가 없습니다.

즉 "바텀시트를 보여줄지 말지 결정하는 코드"(Screen)와 "바텀시트가 어떻게 생겼는지 정의하는 코드"(별도 컴포넌트)를 물리적으로 분리해 두면, 겉보기에 하나의 기능(온보딩 바텀시트)이 두 파일에 걸쳐 있어도 각 파일은 여전히 하나의 액터에만 책임집니다. 반대로 바텀시트의 레이아웃 코드를 Screen 함수 안에 직접 작성해 버리면, 그 순간 Screen은 "언제 보여줄지"(플랫폼 액터)와 "어떻게 생겼는지"(디자인 액터) 두 가지 이유로 바뀌는 파일이 되어 SRP를 위반합니다.

**참고**: 6장(딥링크로 특정 재료로 스크롤)도 같은 논리로 풀립니다 — "어떤 파라미터를 라우트가 받을지"는 Screen(Navigation 시그니처), "그 값을 받아서 실제로 어떻게 스크롤하고 강조 표시할지"는 Content(렌더링 규칙)이며, 두 파일이 같은 기능을 위해 함께 바뀌더라도 각자는 여전히 하나의 이유로만 바뀝니다. 이는 SRP 위반이 아니라, 하나의 요구사항이 자연스럽게 두 액터의 책임 영역에 걸쳐 있는 정상적인 경우입니다.

## 8. 책임 정의 요약

| 계층 | 한 문장 정의 | 액터 | 비유 |
|---|---|---|---|
| `*Content` | 주어진 값을 **무엇을, 어떻게 그릴 것인가**를 결정한다 | 디자인/제품(UX) | 재료를 받아 요리하는 요리사 |
| `*Screen` | 그 화면을 **언제, 어떤 조건에서 실행 환경에 연결할 것인가**를 결정한다 | 아키텍처/플랫폼 통합 | 재료를 배달하고 요리를 내가는 웨이터 |

## 9. 코드 설계에 적용하는 방법

새 코드를 작성하기 전에, 또는 기존 코드에 파라미터를 추가하기 전에 다음을 스스로 묻습니다.

1. **"이 변경을 요청할 사람은 디자이너/PM인가, 아니면 네비게이션·플랫폼·DI를 다루는 엔지니어인가?"** — 전자면 Content(또는 그 하위 컴포넌트), 후자면 Screen입니다.
2. **"이 코드가 한 번만 실행되어야 하는가, 아니면 화면이 다시 그려질 때마다 실행돼도 괜찮은가?"** — 한 번만 실행돼야 한다면(Toast, Navigation, 바텀시트 오픈 등) Composable 함수 본문에 직접 쓰지 말고, `SideEffect`/`collectSideEffect`처럼 recomposition과 무관하게 정확히 한 번 실행을 보장하는 경로를 사용해야 합니다.
3. **"이 값이 바뀌는 이유가 하나인가?"** — 한 파일이 두 가지 서로 다른 이유로 바뀌기 시작했다면(예: Screen에 레이아웃 코드가 섞이기 시작했다면), 그 시점이 분리해야 할 신호입니다.
4. **"이 기능이 여러 파일에 걸쳐 있다면, 각 파일은 여전히 하나의 이유로만 바뀌는가?"** — 걸쳐 있는 것 자체는 문제가 아닙니다. 각 파일이 여전히 단일 액터에만 응답하는지가 기준입니다.

이 네 가지 질문이 이후 화면을 설계하거나 코드 리뷰를 할 때, "이 코드가 Screen에 있어야 하는가 Content에 있어야 하는가"를 매번 직관이 아니라 근거를 갖고 판단하는 기준이 됩니다.
