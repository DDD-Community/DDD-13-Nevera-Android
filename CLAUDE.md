# Nevera Android 개발 가이드

## AppBar 규칙

Scaffold의 `topBar` 파라미터에는 **반드시** 디자인 시스템에서 정의된 AppBar를 사용해야 합니다.
Material3 기본 AppBar(`TopAppBar`, `CenterAlignedTopAppBar` 등)를 직접 사용하지 않습니다.

### 사용 가능한 AppBar

| 컴포넌트 | 용도 |
|---|---|
| `NeveraAppBar` | 일반 상세·설정 화면. 중앙 제목 + 좌측 navigation + 우측 action |
| `NeveraDisplayAppBar` | 섹션 진입·타이틀 강조 화면. 좌측 큰 제목 + 우측 action |
| `NeveraLogoAppBar` | 메인·브랜드 화면. 좌측 로고 + 우측 action |
| `NeveraSearchAppBar` | 검색 화면. 검색 UI를 AppBar 내에 직접 배치 |

### 올바른 사용 예시

```kotlin
Scaffold(
    topBar = {
        NeveraAppBar(
            title = "화면 제목",
            navigation = NeveraAppBarNavigation.Back(onClick = onBackClick),
        )
    }
) { ... }
```

### 잘못된 사용 예시 (금지)

```kotlin
// Material3 기본 AppBar 직접 사용 금지
Scaffold(
    topBar = {
        TopAppBar(title = { Text("제목") })       // ❌
        CenterAlignedTopAppBar(title = { ... })   // ❌
    }
)
```

---

## Screen vs Content 컴포넌트 배치 기준

`*Screen`과 `*Content`는 다음 기준으로 역할을 분리한다.

| 위치 | 역할 | 포함하는 것 |
|---|---|---|
| `*Screen` | 상태 구독, SideEffect 처리, 오케스트레이터 | `collectAsState`, `collectSideEffect`, SideEffect로 트리거되는 컴포넌트, navigation 콜백이 필요한 컴포넌트 |
| `*Content` | UiState의 순수 렌더러 | UiState에서 파생된 모든 UI (Loading, 메인 콘텐츠, UiState-driven 바텀시트) |

### 배치 판단 기준

> **"이 컴포넌트의 표시 여부가 UiState만으로 결정되는가?"**
> - Yes → `*Content`
> - No (SideEffect + local state, 또는 navigation 콜백 필요) → `*Screen`

### 올바른 배치 예시

```kotlin
// HomeScreen — SideEffect 트리거, navigation 콜백 필요한 것
var showGreetingBottomSheet by remember { mutableStateOf(false) }
viewModel.collectSideEffect { effect ->
    when (effect) {
        HomeSideEffect.ShowGreetingBottomSheet -> showGreetingBottomSheet = true
    }
}
if (showGreetingBottomSheet) { GreetingBottomSheet(...) }              // ✅ Screen에 위치

// HomeContent — UiState만으로 결정되는 것
if (uiState.isShowSetNicknameBottomSheet) { SetNicknameBottomSheet(...) } // ✅ Content에 위치
if (uiState.isLoading) { LoadingIndicator() }                         // ✅ Content에 위치
```

### `*Content` 시그니처 원칙

`*Content`의 파라미터는 `uiState: *UiState`와 Intent 람다만 허용한다. local state나 SideEffect에서 파생된 값을 파라미터로 받지 않는다.

```kotlin
// ✅ 올바른 Content 시그니처
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
)

// ❌ 잘못된 Content 시그니처 — local state 혼입 금지
@Composable
fun HomeContent(
    uiState: HomeUiState,
    showGreetingBottomSheet: Boolean,   // ❌
    onGreetingDismiss: () -> Unit,      // ❌
)
```

### SideEffect-triggered 바텀시트의 dismiss 처리

SideEffect로 트리거된 바텀시트의 dismiss는 `*Screen`의 local state를 직접 `false`로 바꾸는 것으로 처리한다. dismiss를 Intent → ViewModel로 올리지 않는다.

```kotlin
// ✅ dismiss — local state 직접 처리
if (showGreetingBottomSheet) {
    GreetingBottomSheet(
        onSkipClick = { showGreetingBottomSheet = false },
        onDismissRequest = { showGreetingBottomSheet = false },
    )
}

// ❌ dismiss 전용 Intent 금지
onSkipClick = { onIntent(HomeIntent.GreetingSkipClick) }  // ❌
```

---

## UiState vs SideEffect 선택 기준

| 항목 | UiState | SideEffect |
|---|---|---|
| 성격 | 현재 화면이 어떻게 보여야 하는가 | 한 번 소비되면 끝인 이벤트 |
| 예시 | 서버/도메인에서 파생된 상태, 화면 회전 후 복원 필요한 값 | 유저의 일회성 액션으로 트리거되는 바텀시트, 토스트, 네비게이션 |
| 판단 기준 | "화면 회전 후에도 복원되어야 하는가?" | "한 번 소비되면 끝인가?" |

### 바텀시트 트리거 예시

```kotlin
// ✅ UiState — 온보딩 완료 여부(서버 상태)에서 파생, 화면 회전 후 복원 필요
val isShowSetNicknameBottomSheet: Boolean = false

// ✅ SideEffect — 유저 버튼 탭에 반응하는 일회성 이벤트
data object ShowGreetingBottomSheet : HomeSideEffect
data object ShowCreateWishBottomSheet : HomeSideEffect
```
