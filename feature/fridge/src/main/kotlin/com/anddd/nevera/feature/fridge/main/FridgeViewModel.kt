package com.anddd.nevera.feature.fridge.main

import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.model.ingredient.IngredientSortOrder
import com.anddd.nevera.domain.usecase.ingredient.GetFridgeIngredientsUseCase
import com.anddd.nevera.domain.usecase.ingredient.ObserveFridgeIngredientsUseCase
import com.anddd.nevera.domain.usecase.ingredient.ObserveIngredientFocusRequestUseCase
import com.anddd.nevera.domain.usecase.notification.MarkAllNotificationsAsReadUseCase
import com.anddd.nevera.domain.usecase.notification.ObserveUnreadNotificationUseCase
import com.anddd.nevera.feature.fridge.main.model.CategoryFilter
import com.anddd.nevera.feature.fridge.main.model.FridgeIngredientUiModel
import com.anddd.nevera.feature.fridge.main.model.FridgeIntent
import com.anddd.nevera.feature.fridge.main.model.FridgeMutation
import com.anddd.nevera.feature.fridge.main.model.FridgeSideEffect
import com.anddd.nevera.feature.fridge.main.model.FridgeUiState
import com.anddd.nevera.feature.fridge.main.model.StorageLocationFilter
import com.anddd.nevera.feature.fridge.main.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import org.orbitmvi.orbit.syntax.Syntax
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FridgeViewModel @Inject constructor(
    private val getFridgeIngredients: GetFridgeIngredientsUseCase,
    private val observeUnreadNotification: ObserveUnreadNotificationUseCase,
    private val markAllNotificationsAsRead: MarkAllNotificationsAsReadUseCase,
    private val observeIngredientFocusRequest: ObserveIngredientFocusRequestUseCase,
    private val observeFridgeIngredients: ObserveFridgeIngredientsUseCase,
) : NeveraViewModel<FridgeUiState, FridgeSideEffect, FridgeIntent, FridgeMutation>(FridgeUiState()) {

    init {
        observeBadge()
        observeFocusRequests()
        observeIngredients()
        intent { loadIngredients() }
    }

    override fun handleIntent(intent: FridgeIntent) {
        when (intent) {
            is FridgeIntent.SelectStorageFilter -> selectStorageFilter(intent.filter)
            is FridgeIntent.SelectCategoryFilter -> selectCategoryFilter(intent.filter)
            FridgeIntent.AddIngredientClick -> addIngredient()
            is FridgeIntent.SelectSortOrder -> selectSortOrder(intent.order)
            FridgeIntent.NotificationIconClicked -> navigateToNotification()
            is FridgeIntent.RescueClick -> showRescueBottomSheet(intent.item)
            is FridgeIntent.RescueConfirm -> rescueIngredient(intent.item, intent.ratio)
            is FridgeIntent.DisposeClick -> showDisposeBottomSheet(intent.item)
            is FridgeIntent.DisposeConfirm -> disposeIngredient(intent.item, intent.ratio)
            is FridgeIntent.IngredientMoreClick -> navigateToEditIngredient(intent.item)
        }
    }

    private fun selectStorageFilter(filter: StorageLocationFilter) = intent {
        applyMutation(FridgeMutation.SelectStorageFilter(filter))
        loadIngredients()
    }

    private fun selectCategoryFilter(filter: CategoryFilter) = intent {
        applyMutation(FridgeMutation.SelectCategoryFilter(storageFilter = state.selectedStorageFilter, categoryFilter = filter))
        loadIngredients()
    }

    private fun selectSortOrder(order: IngredientSortOrder) = intent {
        applyMutation(FridgeMutation.SelectSortOrder(order))
        loadIngredients()
    }

    private fun addIngredient() = intent {
        postSideEffect(FridgeSideEffect.ShowCaptureModeBottomSheet)
    }

    private fun showRescueBottomSheet(item: FridgeIngredientUiModel) = intent {
        postSideEffect(FridgeSideEffect.ShowRescueBottomSheet(item))
    }

    private fun rescueIngredient(item: FridgeIngredientUiModel, ratio: Float) = intent {
        // TODO: 구조 API 연동
    }

    private fun showDisposeBottomSheet(item: FridgeIngredientUiModel) = intent {
        postSideEffect(FridgeSideEffect.ShowDisposeBottomSheet(item))
    }

    private fun disposeIngredient(item: FridgeIngredientUiModel, ratio: Float) = intent {
        // TODO: 폐기 API 연동
    }

    private fun navigateToEditIngredient(item: FridgeIngredientUiModel) = intent {
        postSideEffect(FridgeSideEffect.NavigateToEditIngredient(item.id))
    }

    private fun observeBadge() = intent {
        observeUnreadNotification().collect { hasUnread ->
            applyMutation(FridgeMutation.BadgeUpdated(hasUnread))
        }
    }

    private fun observeIngredients() = intent {
        observeFridgeIngredients().collect { items ->
            applyMutation(FridgeMutation.ShowIngredients(items.map { it.toUiModel() }.toImmutableList()))
        }
    }

    private fun observeFocusRequests() = intent {
        observeIngredientFocusRequest().collect { ingredientId ->
            focusIngredient(ingredientId)
        }
    }

    private fun navigateToNotification() = intent {
        markAllNotificationsAsRead()
        postSideEffect(FridgeSideEffect.NavigateToNotification)
    }

    private fun focusIngredient(ingredientId: Long) = intent {
        // 포커스 요청 도착 시점에 식재료 목록이 아직 로드되지 않았을 수 있으므로,
        // 목록이 채워질 때까지 대기한 뒤 인덱스를 조회한다.
        // 냉장고가 실제로 비어있는 경우 무한 대기로 코루틴이 누수되지 않도록 타임아웃을 둔다.
        val ingredients = withTimeoutOrNull(FOCUS_WAIT_TIMEOUT_MS) {
            container.stateFlow.first { it.ingredients.isNotEmpty() }
        }?.ingredients.orEmpty()

        val index = ingredients.indexOfFirst { it.id == ingredientId }
        if (index >= 0) {
            postSideEffect(FridgeSideEffect.ScrollToIngredient(index))
        } else {
            // TODO: 실 API 연동 시 추가 페이지 요청 후 재시도
            Timber.w("포커스할 식재료를 찾을 수 없음: id=$ingredientId")
        }
    }

    private suspend fun Syntax<FridgeUiState, FridgeSideEffect>.loadIngredients() {
        applyMutation(FridgeMutation.Loading)
        try {
            getFridgeIngredients(
                storageLocation = (state.selectedStorageFilter as? StorageLocationFilter.Specific)?.location,
                category = (state.selectedCategoryFilter as? CategoryFilter.Specific)?.category,
                sortOrder = state.selectedSortOrder,
            ).onFailure {
                postSideEffect(FridgeSideEffect.ShowToast("데이터를 불러오지 못했습니다."))
            }
        } finally {
            applyMutation(FridgeMutation.LoadComplete)
        }
    }

    override suspend fun Syntax<FridgeUiState, FridgeSideEffect>.applyMutation(
        mutation: FridgeMutation,
    ) {
        when (mutation) {
            FridgeMutation.Loading -> reduce { state.copy(isLoading = true) }
            FridgeMutation.LoadComplete -> reduce { state.copy(isLoading = false) }
            is FridgeMutation.ShowIngredients -> reduce { state.copy(ingredients = mutation.ingredients) }
            is FridgeMutation.SelectStorageFilter -> reduce { state.copy(selectedStorageFilter = mutation.filter) }
            is FridgeMutation.SelectCategoryFilter -> reduce {
                state.copy(
                    categoryFilters = (state.categoryFilters + (mutation.storageFilter to mutation.categoryFilter)).toImmutableMap(),
                )
            }

            is FridgeMutation.SelectSortOrder -> reduce { state.copy(selectedSortOrder = mutation.order) }
            is FridgeMutation.BadgeUpdated -> reduce { state.copy(hasUnreadNotification = mutation.hasUnread) }
        }
    }

    companion object {
        private const val FOCUS_WAIT_TIMEOUT_MS = 5_000L
    }
}
