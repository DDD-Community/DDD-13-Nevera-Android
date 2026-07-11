# Presentation Layer 구조와 관리 규칙

이 문서는 feature 모듈의 Presentation Layer에서 `*Screen` / `*Content` / Component / `*ViewModel` / `*UiState` / `*SideEffect` / `*Intent` / `*Mutation`이 실제로 어떤 파일·디렉토리로 나뉘고, 각 계층이 어떤 기준으로 역할을 분담하는지를 정리합니다.

각 타입의 의미와 상태 변경 흐름(Intent → Mutation → applyMutation → UiState) 자체는 [`mvi-core-architecture.md`](./mvi-core-architecture.md)에서 다룹니다. 이 문서는 그 타입들이 **실제 파일 구조로 어떻게 배치되고, Screen · Content · Component가 Compose 관점에서 왜 그렇게 나뉘어야 하는지**에 집중합니다.

## 1. 디렉토리 및 파일 구조

feature 모듈은 화면 단위로 `main/`(또는 화면명) 패키지 아래에 다음 구조를 가집니다. `feature/main/home`을 예로 듭니다.

```text
feature/main/src/main/kotlin/.../home/
├── HomeScreen.kt              # 오케스트레이터: ViewModel 구독, SideEffect 처리
├── HomeViewModel.kt           # NeveraViewModel 구현체
├── component/
│   ├── HomeContent.kt         # Content: uiState 전체를 받는 화면 최상위 렌더러
│   ├── WishBanner.kt          # Component: uiState의 일부 슬라이스만 받는 하위 렌더러
│   ├── RecentIngredientSection.kt  # Component: LazyColumn 섹션 단위
│   ├── IngredientItem.kt      # Component: 리스트 아이템 단위
│   ├── CreateWishBottomSheet.kt   # SideEffect로 트리거되는 바텀시트 (Screen이 호출)
│   ├── GreetingBottomSheet.kt
│   └── ...
├── model/
│   ├── HomeIntent.kt          # sealed interface : NeveraIntent
│   ├── HomeUiState.kt         # data class : NeveraState
│   ├── HomeMutation.kt        # sealed interface : NeveraMutation
│   ├── HomeSideEffect.kt      # sealed interface : NeveraSideEffect
│   └── HomeProfileUiModel.kt  # 도메인 모델 → UI 표시용 모델 (필요한 화면만)
└── navigation/
    └── HomeNavigation.kt      # Route 정의 + NavGraphBuilder 확장 함수
```

| 위치 | 담는 것 |
|---|---|
| `model/` | `Intent`, `UiState`, `Mutation`, `SideEffect`, UiModel(도메인 → 표시용 변환 모델) |
| 화면 루트 (`main/` 등) | `*ViewModel.kt`, `*Screen.kt` |
| `component/` | `*Content.kt`(화면 최상위 렌더러) + Component(하위 조립 단위, SideEffect 트리거 바텀시트 포함) |
| `navigation/` | `*Route`, `NavGraphBuilder.xxxScreen()` 확장 함수 |

`component/` 디렉토리 안에서 파일 하나가 `*Content`인지 Component인지는 접두사가 아니라 역할로 구분합니다. 화면 전체를 조립하는 파일만 `{Name}Content`라는 이름을 쓰고, 그 하위에서 조립되는 파일들(`WishBanner`, `IngredientItem` 등)은 역할을 드러내는 이름을 그대로 씁니다.

이 구조는 `.claude/skills/create-feature-module`이 새 feature 모듈을 생성할 때 그대로 스캐폴딩합니다(`/create-feature-module <모듈명>`). 즉 새 화면을 만들 때마다 수작업으로 구조를 결정하지 않고, 스캐폴드가 이 구조를 강제합니다.

## 2. 네이밍 규칙

모듈명(예: `my-page`, `myPage`, `MyPage`)은 단어 목록으로 정규화한 뒤 용도에 따라 다르게 표기합니다.

| 표기 | 규칙 | 예시 | 사용처 |
|---|---|---|---|
| `{name}` | 전체 소문자 붙임 | `mypage` | 모듈 디렉토리명, 패키지명 |
| `{Name}` | PascalCase | `MyPage` | 클래스명 접두사 — `MyPageScreen`, `MyPageViewModel`, `MyPageContent`, `MyPageUiState` 등 |
| `{camelName}` | camelCase | `myPage` | Navigation 확장 함수명 — `NavGraphBuilder.myPageScreen()` |

## 3. 타입별 책임 요약

| 타입 | 정의 | 선언 형태 |
|---|---|---|
| `*Intent` | 사용자가 화면에서 수행한 액션 | `sealed interface : NeveraIntent` |
| `*UiState` | 화면 렌더링에 쓰는 단일 상태 | `data class : NeveraState` |
| `*Mutation` | 상태 변경의 의미 단위 | `sealed interface : NeveraMutation` |
| `*SideEffect` | 상태로 보관하지 않는 일회성 동작 | `sealed interface : NeveraSideEffect` |

상세 설계 원칙과 예시는 [`mvi-core-architecture.md`](./mvi-core-architecture.md) 참고.

## 4. Screen / Content / Component 3계층 구조

| 계층 | 역할 | 파라미터 특징 |
|---|---|---|
| `*Screen` | 상태 구독, SideEffect 처리, 오케스트레이터 | `viewModel`, navigation 콜백 등 값이 아닌 것에 의존 |
| `*Content` | 화면 최상위 렌더러. `Scaffold` 등 레이아웃 뼈대를 잡고 Component를 배치 | `uiState: *UiState`와 Intent 람다만 |
| Component | Content 내부에서 조립되는 하위 렌더러 (`WishBanner`, `IngredientItem` 등) | `uiState`의 일부 슬라이스(원시 타입, UiModel)와 콜백만 |

### 판정 질문

> **"이 컴포넌트를 Preview에서 필요한 값만 넘겨 그대로 띄울 수 있는가?"**
> - Yes → `*Content` 또는 Component (내부에 스크롤·애니메이션 같은 local UI state가 있어도 무방)
> - No (ViewModel, navigation 콜백 중 하나라도 필요) → `*Screen`

Screen/Content 분리의 목적은 "검증하기 어려운 코드(ViewModel, navigation)를 얇은 Screen 한 곳에 몰아넣고, 나머지 렌더링 코드는 `(값, 콜백) → UI`라는 결정적 함수로 유지해 Preview·리뷰만으로 검증 가능하게 만드는 것"입니다. Content를 다시 Component로 쪼개는 목적은 여기에 더해 **재구성(recomposition) 범위를 좁히는 것**입니다 — 자세한 내용은 [5장](#5-compose-관점에서-본-계층-분리의-장점)에서 다룹니다.

> **자동 검증**: 이 계층 규칙은 detekt 커스텀 룰셋 `NeveraScreenContentRules`로 강제됩니다 — `ScreenDelegatesToContentRule`(Screen의 Content 위임), `ViewModelAccessOnlyInScreenRule`(ViewModel 구독을 Screen으로 제한), `ToastOutsideScreenRule`(Toast를 Screen으로 제한). `./gradlew detekt`로 검사되며 CI와 Claude Code Stop 훅에 편입되어 있습니다.

### 경계 사례 처리

- **Content/Component 내부 local UI state**: "리셋되어도 비즈니스적으로 아무 일도 일어나지 않는가?"가 Yes면 `remember`로 내부에 둡니다. 스크롤 위치(`HomeContent`의 `rememberLazyListState`), 인라인 편집 토글(`IngredientContent`의 `editState`) 등이 해당합니다. No(리셋 시 비즈니스 의미 손실)면 UiState로 승격합니다.
- **SideEffect가 닿을 수 없는 Content 내부 이벤트**(예: 특정 아이템으로 스크롤 이동): SideEffect 대신 nullable UiState 필드 + ack용 Intent 쌍으로 표현합니다. `IngredientContent`가 `uiState.scrollTargetIndex`를 `LaunchedEffect`로 감지해 스크롤한 뒤 `IngredientIntent.ScrollHandled`를 보내 필드를 비우는 패턴입니다.
- **플랫폼 리소스 바인딩이 필요한 화면**(카메라 프리뷰 바인딩 등): `uiState + Intent 람다만` 규칙의 명시적 예외로, `onBindXxx` 같은 콜백을 Content에 추가로 넘길 수 있습니다. `OcrCaptureScreen`이 `OcrCaptureContent(uiState = ..., onIntent = ..., onBindCamera = viewModel::bindCamera)`로 호출하는 것이 그 예시입니다. 이 예외는 Screen에서만 주입하고, 화면 자체가 플랫폼 리소스에 강하게 결합돼 있을 때로 한정합니다.
- **Content/Component에서 시스템 환경(Context, 권한 상태 등)을 조회하는 것**: 조회 결과에 따라 *렌더링 내용 자체가 달라지는 것*은 금지합니다(Preview·리뷰만으로 화면을 예측할 수 없게 되기 때문). 반면 **사용자 이벤트가 발생한 시점에 값을 읽어 그대로 Intent에 실어 ViewModel로 넘기는 것**은 허용합니다 — 무엇을 할지 판단하는 주체가 여전히 ViewModel이기 때문입니다. `SettingNotificationContent`의 `ExpiryAlarmRow`가 스위치 클릭 시점에 `DefaultPermissionChecker.isGranted()`로 알림 권한을 확인해 `ExpiryAlarmToggled(enabled, isPermissionGranted)` Intent에 실어 보내는 것이 이 경우입니다.

### SideEffect로 트리거된 바텀시트의 dismiss

Screen의 local state(`rememberSaveable { mutableStateOf(false) }`)를 직접 `false`로 바꾸는 것으로 처리합니다. dismiss 전용 Intent를 만들어 ViewModel까지 올리지 않습니다. `HomeScreen`의 `showXxxBottomSheet` 플래그들이 그 예시입니다.

### UiState vs SideEffect 선택 기준

| 판단 기준 | UiState | SideEffect |
|---|---|---|
| "ViewModel 생존 범위 내에서 구독·반응이 지속되는가?" | Yes | No |
| 예시 | 알림 뱃지(`hasUnreadNotification`, 세션 내내 실시간 반응) | 바텀시트 표시, Toast, Navigation (한 번 소비되면 끝) |

바텀시트 visibility는 `rememberSaveable`이 Configuration Change를 커버하므로 UiState로 승격할 필요가 없습니다.

## 5. Compose 관점에서 본 계층 분리의 장점

Compose는 composable 함수의 입력값이 이전 호출과 동일하면 그 함수의 재구성(recomposition)을 건너뜁니다(smart recomposition / skip). Screen·Content·Component를 나누는 것은 파일 정리 이상의 의미를 가집니다 — 이 skip 메커니즘이 실제로 작동하도록 **재구성 범위와 파라미터를 의도적으로 좁히는 작업**입니다.

### 5.1 재구성 범위 제어

Content는 화면 전체 `UiState`를 받으므로 상태가 하나라도 바뀌면 항상 다시 실행됩니다. 하지만 Content 내부의 각 Component는 `UiState`의 특정 슬라이스만 파라미터로 받기 때문에, 자신과 무관한 필드가 바뀌었을 때는 재구성을 건너뜁니다.

```kotlin
// HomeContent.kt — Component는 uiState 전체가 아니라 필요한 값만 받는다
WishBanner(
    nickname = uiState.profile.nickname,
    wish = uiState.wish,
    onCreateWish = { onIntent(HomeIntent.CreateWishClick) },
    onEditWish = { onIntent(HomeIntent.WishEditClick) },
)
```

`rescuedIngredients` 페이지네이션으로 `uiState`가 바뀌어도 `nickname`과 `wish`가 그대로라면 `WishBanner`는 재구성되지 않습니다. 이 로직이 `HomeContent` 하나에 전부 풀어져 있었다면 이런 부분 skip은 일어나지 않습니다.

`LazyColumn` 리스트에서도 같은 원리가 `key` 단위로 적용됩니다.

```kotlin
// RecentIngredientSection.kt
items(items = visibleIngredients, key = { it.id }) { ingredient -> IngredientItem(...) }
```

리스트 아이템을 `IngredientItem`이라는 별도 Component로 분리하고 `key`를 지정했기 때문에, 아이템 하나가 추가/변경돼도 나머지 아이템들의 `IngredientItem` 호출은 재구성을 건너뜁니다.

### 5.2 Screen의 로컬 상태가 Content를 건드리지 않음

`HomeScreen`은 바텀시트 표시 여부를 로컬 state(`rememberSaveable`)로 갖습니다. 이 값이 바뀌면 `HomeScreen`은 재구성되지만, `HomeContent(uiState = state, onIntent = ...)` 호출에 전달되는 인자 자체는 그대로이므로 `HomeContent`는 재구성을 건너뜁니다. "바텀시트를 연다"는 이벤트가 화면의 나머지 UI(Content)를 다시 그리게 만들지 않는다는 뜻입니다. Screen과 Content가 분리돼 있지 않고 로컬 state와 UiState 렌더링이 한 함수 안에 섞여 있었다면 이 경계는 사라집니다.

### 5.3 계층별 독립 Preview

Content와 Component가 `UiState`/원시 값만 파라미터로 받기 때문에, ViewModel·Hilt·NavController 없이 각 계층을 독립적으로 미리보기 할 수 있습니다. `home` feature는 실제로 계층마다 다른 밀도로 Preview를 두고 있습니다.

| 계층 | 예시 | Preview 개수 |
|---|---|---|
| Content | `HomeContent` | 1개 |
| Component | `WishBanner` | 3개 (위시 없음 / 진행중 / 달성) |
| Component | `RecentIngredientSection` | 2개 |
| Component | `IngredientItem` | 11개 (카테고리별 + 긴 이름 말줄임) |

`IngredientItem`처럼 상태 조합이 많은 leaf 컴포넌트는 Content 레벨에서 전부 조합해 Preview로 만들기 어렵습니다. Component로 쪼개져 있기 때문에 각 변형을 독립된 `@Preview`로 바로 확인할 수 있고, 디자인 리뷰도 앱을 빌드·실행하지 않고 Preview만으로 진행할 수 있습니다.

### 5.4 좁은 파라미터가 주는 안정성(stability) 추론 용이성

Compose 컴파일러는 함수 파라미터를 모두 안정적(stable)이라고 추론할 수 있을 때만 해당 함수를 skippable로 표시합니다. `UiState` 전체를 하위 컴포넌트까지 그대로 전달하면, 그 컴포넌트가 실제로 쓰지 않는 필드까지 안정성 추론 대상에 끼어듭니다. Component가 `String`, `Boolean`, UiModel처럼 좁고 단순한 타입만 파라미터로 받으면, "이 함수가 언제 다시 그려지는가"를 훨씬 좁은 범위에서 판단할 수 있습니다.

## 6. ViewModel 규칙

- 모든 feature ViewModel은 `NeveraViewModel<UiState, SideEffect, Intent, Mutation>`을 상속합니다. `ViewModel()`을 직접 상속하지 않습니다.
- `handleIntent()`가 사용자 액션의 유일한 진입점입니다. `handleIntent()` 밖에 사용자 액션을 처리하는 `public` 함수를 두지 않습니다.
- `reduce`는 `applyMutation()` 내부에서만 호출합니다. business logic 함수(`onXxxClick()` 등) 안에서 직접 호출하지 않습니다.
- 실시간으로 반응해야 하는 도메인 상태(알림 뱃지, 재고 목록 등)는 `init {}`에서 UseCase의 `Flow`를 `intent { collect { ... } }`로 구독해 `Mutation`을 흘려보냅니다 (`HomeViewModel.observeBadge()`, `subscribeRescuedIngredients()` 참고).
- 동시 요청으로 인한 상태 충돌이 우려되는 로직(페이지네이션 등)은 `Mutex`로 가드합니다 (`HomeViewModel.loadMoreIngredients()` 참고).

## 7. Screen 비대화 방지

SideEffect로 트리거되는 바텀시트가 늘어날수록 Screen에 `showXxx` 플래그 선언 + `collectSideEffect`의 `when` 분기 + `if` 블록이 3곳씩 늘어납니다. 대응 원칙:

- Screen에는 레이아웃/`Modifier` 코드를 두지 않습니다. Screen은 구독·분기·위임만 하고, 실제 레이아웃은 전부 Content 또는 SideEffect 트리거 컴포넌트(바텀시트 등) 내부에 있어야 합니다. Scaffold 사용은 `ScreenNoScaffoldRule`이 파일 단위(`*Screen.kt`)로 자동 검출합니다.
- SideEffect 트리거 바텀시트가 3개를 넘으면 플래그들을 `rememberSaveable`로 묶은 별도 state holder + 전용 `*BottomSheets` 컴포저블로 추출하는 것을 고려합니다. 화면이 단순할 때(바텀시트 1~2개)는 추출을 강제하지 않습니다 — 간접 참조 비용이 이득을 넘어서기 때문입니다.

## 8. AppBar 규칙

`*Content`의 `Scaffold(topBar = ...)`에는 반드시 디자인 시스템 AppBar(`NeveraAppBar` / `NeveraDisplayAppBar` / `NeveraLogoAppBar` / `NeveraSearchAppBar`)를 사용합니다. Material3 기본 AppBar를 직접 사용하지 않습니다. 상세 사용 기준은 프로젝트 루트 `CLAUDE.md` 참고.

## 9. 자동 생성 도구

`/create-feature-module <모듈명>`(`.claude/skills/create-feature-module`)이 위 구조를 그대로 스캐폴딩합니다: `build.gradle.kts`, `AndroidManifest.xml`, `model/{Name}Intent.kt` · `{Name}UiState.kt` · `{Name}SideEffect.kt` · `{Name}Mutation.kt`, `{Name}ViewModel.kt`, `component/{Name}Content.kt`, `{Name}Screen.kt`, `navigation/{Name}Navigation.kt`, 테스트 파일까지 한 번에 생성하고 `settings.gradle.kts` / `app/build.gradle.kts`에 모듈을 등록합니다.

## 참고 파일

- `feature/main/src/main/kotlin/com/anddd/nevera/feature/main/home/HomeScreen.kt` — Screen 오케스트레이터 예시
- `feature/main/src/main/kotlin/com/anddd/nevera/feature/main/home/component/HomeContent.kt` — Content 예시, Component에 슬라이스만 전달하는 호출부
- `feature/main/src/main/kotlin/com/anddd/nevera/feature/main/home/component/WishBanner.kt`, `IngredientItem.kt`, `RecentIngredientSection.kt` — Component 예시 및 계층별 독립 Preview 예시
- `feature/main/src/main/kotlin/com/anddd/nevera/feature/main/home/HomeViewModel.kt` — Flow 구독, Mutex 가드, applyMutation 예시
- `feature/ingredient/.../main/component/IngredientContent.kt` — local UI state, ack-Intent 스크롤 패턴 예시
- `feature/ingredient/.../ocrcapture/OcrCaptureScreen.kt` — 플랫폼 리소스 바인딩 콜백 예외 예시
- `feature/mypage/.../settingnotification/component/SettingNotificationContent.kt` — 이벤트 핸들러 내 시스템 값 조회 예시
- `.claude/skills/create-feature-module/SKILL.md` — 스캐폴드 템플릿 원본
- `CLAUDE.md` — Presentation Layer 규칙 원본 (AppBar, Screen/Content 배치 기준)
- `docs/mvi-core-architecture.md` — Intent/Mutation/UiState/SideEffect/ViewModel 설계 원칙 상세
