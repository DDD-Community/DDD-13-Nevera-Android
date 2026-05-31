# MVI 도입 및 core:mvi 설계 원칙

이 문서는 Nevera 프로젝트의 Presentation Layer에서 사용하는 MVI 구조와 `core:mvi` 모듈의 설계 원칙을 설명합니다.

목적은 프로젝트를 처음 보는 사람이 화면 상태 관리 방식, 주요 타입의 역할, 상태 변경 흐름을 빠르게 이해할 수 있도록 돕는 것입니다.

## 배경

Compose 화면은 상태를 기반으로 렌더링됩니다. 따라서 ViewModel이 화면 상태를 노출하고, Composable이 그 상태를 구독하는 구조만으로도 단방향 데이터 흐름을 구성할 수 있습니다.

일반적인 `MVVM + UiState/SideEffect` 구조도 충분히 실용적입니다.

```kotlin
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
)

sealed interface LoginSideEffect {
    data object MoveToHomeScreen : LoginSideEffect
    data class ShowToast(val message: String) : LoginSideEffect
}
```

이 구조는 다음 흐름을 만들 수 있습니다.

1. View는 사용자 이벤트를 ViewModel로 전달합니다.
2. ViewModel은 이벤트를 처리하고 `UiState`를 변경합니다.
3. View는 `UiState`를 기반으로 다시 렌더링합니다.
4. Navigation, Toast 같은 일회성 동작은 `SideEffect`로 처리합니다.

이 방식만으로도 UDF(Unidirectional Data Flow)와 SSOT(Single Source of Truth)를 지킬 수 있습니다.

다만 화면이 복잡해지고 사용자 액션이 많아지면 ViewModel의 public 함수와 상태 변경 지점이 빠르게 늘어날 수 있습니다.

```kotlin
fun onRefreshClicked()
fun onItemClicked(id: Long)
fun onSearchKeywordChanged(keyword: String)
fun onCategorySelected(category: Category)
fun onSortChanged(sort: Sort)
fun onDeleteClicked(id: Long)
fun onDeleteConfirmed()
fun onRetryClicked()
```

이 경우 다음 정보를 코드만 보고 파악하기 어려워질 수 있습니다.

- 이 화면에서 발생 가능한 사용자 액션은 무엇인가?
- 특정 상태는 어떤 액션에 의해 변경되는가?
- 로딩 상태는 어디서 시작되고 어디서 종료되는가?
- Navigation은 어떤 액션의 결과로 발생하는가?
- 상태 변경은 ViewModel 내부 몇 군데에서 일어나는가?

Nevera에서는 이 추적 비용을 줄이기 위해 MVI 구조를 도입했습니다.

MVI 구현체로는 [Orbit MVI](https://github.com/orbit-mvi/orbit-mvi)를 사용합니다. Orbit은 `reduce` · `intent` · `postSideEffect`가 각각의 코루틴 스코프 안에서만 호출될 수 있다는 컴파일 타임 보장을 제공합니다. 덕분에 상태 변경과 부수 효과가 의도된 경로 밖에서 발생하는 것을 런타임이 아닌 컴파일 단계에서 차단할 수 있습니다.

## 핵심 설계 방향

Nevera의 MVI 구조는 다음 목표를 가집니다.

- 화면에서 발생 가능한 사용자 액션을 명시적으로 드러냅니다.
- 상태 변경의 의미를 코드에 남깁니다.
- 실제 상태 변경 지점을 한 곳에 모읍니다.
- 코드 리뷰 시 화면 흐름을 빠르게 파악할 수 있게 합니다.
- feature 모듈마다 같은 Presentation Layer 규칙을 사용하도록 합니다.

## 주요 타입

`core:mvi`는 feature 모듈에서 공통으로 사용할 기본 타입을 제공합니다.

```kotlin
interface NeveraState

interface NeveraSideEffect

interface NeveraIntent

interface NeveraMutation
```

각 타입의 역할은 다음과 같습니다.

| 타입 | 역할 |
| --- | --- |
| `UiState` | 화면이 렌더링에 사용하는 단일 상태입니다. |
| `Intent` | 사용자가 화면에서 수행한 액션을 표현합니다. |
| `Mutation` | 액션 또는 비즈니스 로직의 결과로 발생한 상태 변경의 의미를 표현합니다. |
| `SideEffect` | Navigation, Toast, BottomSheet 등 한 번만 처리할 동작을 표현합니다. |

## Intent

`Intent`는 사용자가 화면에서 수행할 수 있는 액션을 타입으로 명시합니다.

```kotlin
sealed interface HomeIntent : NeveraIntent {
    data class RecentIngredientTabClick(val tab: IngredientFilterTab) : HomeIntent
    data object AddIngredientClick : HomeIntent
    data class LoadMoreIngredients(val tab: IngredientFilterTab) : HomeIntent
    data class UpdateNicknameClick(val nickname: String) : HomeIntent
    data object CreateWishClick : HomeIntent
    data class CreateWishConfirmed(val name: String, val goalAmount: Long) : HomeIntent
    data object WishEditClick : HomeIntent
    data class UpdateWishConfirmed(
        val id: Long,
        val name: String,
        val goalAmount: Long,
    ) : HomeIntent
    data object NotificationIconClicked : HomeIntent
}
```

이 구조를 사용하면 ViewModel의 여러 함수를 직접 살펴보기 전에, 해당 화면에서 발생 가능한 액션 목록을 먼저 확인할 수 있습니다.

## Mutation

`Mutation`은 상태를 어떻게 바꿀 것인지에 대한 의미 단위입니다.

```kotlin
sealed interface HomeMutation : NeveraMutation {
    data object Loading : HomeMutation
    data object LoadComplete : HomeMutation
    data class SetRecentIngredientFilterTab(val tab: IngredientFilterTab) : HomeMutation
    data class ShowProfile(val profile: HomeProfileUiModel) : HomeMutation
    data class ShowWish(val wish: HomeWishUiModel) : HomeMutation
    data object ShowEmptyWish : HomeMutation
    data class ShowSavings(val savings: HomeSavingsUiModel) : HomeMutation
    data class ShowRescuedIngredients(
        val ingredients: List<IngredientUiModel>,
        val hasMore: Boolean,
    ) : HomeMutation
    data object LoadingMoreRescuedIngredients : HomeMutation
    data class AppendRescuedIngredients(
        val ingredients: List<IngredientUiModel>,
        val hasMore: Boolean,
    ) : HomeMutation
    data class BadgeUpdated(val hasUnread: Boolean) : HomeMutation
}
```

`Mutation`은 단순히 `state.copy(...)`를 감싸기 위한 타입이 아닙니다.

`state.copy(isLoading = true)`는 값이 바뀐다는 사실만 보여줍니다. 반면 `HomeMutation.Loading`은 화면이 로딩 상태로 진입한다는 의미를 드러냅니다.

`state.copy(wish = null)`은 `null`의 의미를 바로 설명하지 않습니다. 반면 `HomeMutation.ShowEmptyWish`는 사용자의 위시가 없는 상태를 표현하기 위한 변경이라는 의도를 드러냅니다.

## SideEffect

`SideEffect`는 상태로 보관하지 않는 일회성 동작을 표현합니다. Navigation, Toast, BottomSheet 표시처럼 한 번 처리하면 끝나는 동작에 사용합니다.

```kotlin
sealed interface HomeSideEffect : NeveraSideEffect {
    data class ShowError(val message: String) : HomeSideEffect
    data object ShowCaptureModeBottomSheet : HomeSideEffect
    data object ShowSetNicknameBottomSheet : HomeSideEffect
    data object ShowGreetingBottomSheet : HomeSideEffect
    data object ShowCreateWishBottomSheet : HomeSideEffect
    data object ShowUpdateWishBottomSheet : HomeSideEffect
    data object ShowWishCreatedToast : HomeSideEffect
    data object ShowWishUpdatedToast : HomeSideEffect
    data object NavigateToNotification : HomeSideEffect
}
```

`Intent`와 마찬가지로 sealed interface로 선언하기 때문에, 이 화면에서 발생 가능한 일회성 동작을 타입만으로 파악할 수 있습니다.

## NeveraViewModel

모든 feature ViewModel은 같은 흐름을 따르도록 `NeveraViewModel`을 상속합니다.

```kotlin
abstract class NeveraViewModel<
    STATE : NeveraState,
    SIDE_EFFECT : NeveraSideEffect,
    INTENT : NeveraIntent,
    MUTATION : NeveraMutation,
>(initialState: STATE) : ViewModel(), ContainerHost<STATE, SIDE_EFFECT> {

    override val container = container<STATE, SIDE_EFFECT>(
        initialState = initialState,
        buildSettings = {
            exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                Timber.e(throwable)
            }
        },
    )

    abstract fun handleIntent(action: INTENT)

    protected abstract suspend fun Syntax<STATE, SIDE_EFFECT>.applyMutation(
        mutation: MUTATION,
    )
}
```

이 추상 클래스는 다음 규칙을 강제합니다.

1. View는 사용자 액션을 `handleIntent(intent)`로 전달합니다.
2. ViewModel은 `Intent`를 해석하고 필요한 비즈니스 로직을 수행합니다.
3. 상태 변경이 필요하면 `Mutation`을 생성합니다.
4. `applyMutation()`에서 `Mutation`을 실제 `UiState` 변경으로 변환합니다.
5. 일회성 동작은 `SideEffect`로 방출합니다.

## 상태 변경 흐름

Nevera의 Presentation Layer에서 상태 변경은 다음 흐름을 따릅니다.

```text
User Action
    -> Intent
    -> ViewModel.handleIntent()
    -> business logic
    -> Mutation
    -> applyMutation()
    -> reduce { state.copy(...) }
    -> UiState
    -> Compose UI
```

`SideEffect`는 상태로 보관할 필요가 없는 일회성 동작을 처리할 때 사용합니다.

```text
User Action
    -> Intent
    -> ViewModel.handleIntent()
    -> postSideEffect(...)
    -> Navigation / Toast / BottomSheet
```

## 구현 예시

`handleIntent()`는 화면에서 들어오는 사용자 액션의 단일 진입점입니다.

```kotlin
override fun handleIntent(intent: HomeIntent) {
    when (intent) {
        is HomeIntent.RecentIngredientTabClick -> onRecentIngredientTabClick(intent.tab)
        HomeIntent.AddIngredientClick -> onAddIngredientClick()
        is HomeIntent.LoadMoreIngredients -> loadMoreIngredients(intent.tab)
        is HomeIntent.UpdateNicknameClick -> onConfirmNickname(intent.nickname)
        HomeIntent.CreateWishClick -> onGreetingCreateWishClick()
        is HomeIntent.CreateWishConfirmed -> {
            onCreateWishConfirmed(intent.name, intent.goalAmount)
        }
        HomeIntent.WishEditClick -> onWishEditClick()
        is HomeIntent.UpdateWishConfirmed -> {
            onUpdateWishConfirmed(intent.id, intent.name, intent.goalAmount)
        }
        HomeIntent.NotificationIconClicked -> onNotificationIconClick()
    }
}
```

Intent 처리 함수는 비즈니스 로직과 흐름 제어에 집중합니다.

```kotlin
private fun onRecentIngredientTabClick(tab: IngredientFilterTab) = intent {
    applyMutation(HomeMutation.SetRecentIngredientFilterTab(tab))
}

private fun onAddIngredientClick() = intent {
    postSideEffect(HomeSideEffect.ShowCaptureModeBottomSheet)
}

private fun onNotificationIconClick() = intent {
    markAllNotificationsAsRead()
    postSideEffect(HomeSideEffect.NavigateToNotification)
}
```

실제 상태 변경은 `applyMutation()`에 모읍니다.

```kotlin
override suspend fun Syntax<HomeUiState, HomeSideEffect>.applyMutation(
    mutation: HomeMutation,
) {
    when (mutation) {
        HomeMutation.Loading -> reduce { state.copy(isLoading = true) }

        HomeMutation.LoadComplete -> reduce { state.copy(isLoading = false) }

        is HomeMutation.SetRecentIngredientFilterTab -> reduce {
            state.copy(ingredientFilterTab = mutation.tab)
        }

        is HomeMutation.ShowProfile -> reduce {
            state.copy(profile = mutation.profile)
        }

        is HomeMutation.ShowWish -> reduce {
            state.copy(wish = mutation.wish)
        }

        HomeMutation.ShowEmptyWish -> reduce {
            state.copy(wish = null)
        }

        is HomeMutation.BadgeUpdated -> reduce {
            state.copy(hasUnreadNotification = mutation.hasUnread)
        }

        // ... (일부 생략 — 전체 구현은 HomeViewModel.kt 참고)
    }
}
```

## 리뷰 관점에서의 장점

이 구조를 따르면 코드 리뷰 시 다음 순서로 화면 흐름을 확인할 수 있습니다.

1. `UiState`를 보고 화면이 어떤 상태를 가지는지 확인합니다.
2. `Intent`를 보고 사용자가 어떤 액션을 수행할 수 있는지 확인합니다.
3. `Mutation`을 보고 상태가 어떤 의미 단위로 변경되는지 확인합니다.
4. `applyMutation()`을 보고 각 `Mutation`이 실제 `UiState`를 어떻게 변경하는지 확인합니다.
5. `SideEffect`를 보고 Navigation, Toast, BottomSheet 같은 일회성 동작을 확인합니다.

이 방식은 상태 변경 지점이 분산되는 문제를 줄이고, 화면의 입력과 상태 전이 흐름을 타입 단위로 추적할 수 있게 합니다.

## 트레이드오프

이 구조는 파일과 타입 수가 늘어납니다.

간단한 화면에서도 다음과 같은 파일이 생길 수 있습니다.

```text
SampleIntent.kt
SampleUiState.kt
SampleSideEffect.kt
SampleMutation.kt
SampleViewModel.kt
SampleScreen.kt
SampleContent.kt
SampleNavigation.kt
```

따라서 아주 단순한 화면에서는 구조가 다소 무겁게 느껴질 수 있습니다.

Nevera에서는 feature 모듈의 구조를 자동으로 생성하는 도구를 사용해 반복 작성 비용을 줄이고, Presentation Layer의 일관성을 유지하는 방향을 선택했습니다.

## 설계 원칙 요약

- `Intent`는 사용자가 무엇을 했는지를 표현합니다.
- `Mutation`은 그 결과 화면 상태가 어떻게 바뀌어야 하는지를 표현합니다.
- `UiState`는 화면이 렌더링에 사용하는 단일 상태입니다.
- `SideEffect`는 상태로 보관하지 않는 일회성 동작입니다.
- `reduce`는 원칙적으로 `applyMutation()` 내부에서만 호출합니다.
- feature ViewModel은 `handleIntent()`를 사용자 액션의 단일 진입점으로 사용합니다.
- 상태 변경 흐름은 타입과 함수 이름만으로 추적 가능해야 합니다.

