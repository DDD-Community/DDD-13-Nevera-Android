package com.anddd.nevera.feature.fridge.main

import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.usecase.notification.MarkAllNotificationsAsReadUseCase
import com.anddd.nevera.domain.usecase.notification.ObserveUnreadNotificationUseCase
import com.anddd.nevera.feature.fridge.main.model.FridgeIngredientUiModel
import com.anddd.nevera.feature.fridge.main.model.FridgeIntent
import com.anddd.nevera.feature.fridge.main.model.FridgeMutation
import com.anddd.nevera.feature.fridge.main.model.FridgeSideEffect
import com.anddd.nevera.feature.fridge.main.model.FridgeUiState
import com.anddd.nevera.feature.fridge.main.model.CategoryFilter
import com.anddd.nevera.feature.fridge.main.model.IngredientSortOrder
import com.anddd.nevera.feature.fridge.main.model.StorageLocationFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class FridgeViewModel @Inject constructor(
    private val observeUnreadNotification: ObserveUnreadNotificationUseCase,
    private val markAllNotificationsAsRead: MarkAllNotificationsAsReadUseCase,
) :
    NeveraViewModel<FridgeUiState, FridgeSideEffect, FridgeIntent, FridgeMutation>(FridgeUiState()) {

    init {
        observeBadge()
        handleIntent(FridgeIntent.Load)
    }

    override fun handleIntent(intent: FridgeIntent) {
        when (intent) {
            FridgeIntent.Load -> load()

            is FridgeIntent.SelectStorageFilter -> selectStorageFilter(intent.filter)

            is FridgeIntent.SelectCategoryFilter -> selectCategoryFilter(intent.filter)

            FridgeIntent.AddIngredientClick -> addIngredient()

            is FridgeIntent.SelectSortOrder -> selectSortOrder(intent.order)

            FridgeIntent.NotificationIconClicked -> navigateToNotification()
        }
    }

    private fun load() = intent {
        applyMutation(FridgeMutation.Loading)
        // TODO: UseCase로 초기 데이터 로드 후 실데이터로 교체
        applyMutation(FridgeMutation.ShowIngredients(mockIngredients))
        applyMutation(FridgeMutation.LoadComplete)
    }

    private fun selectStorageFilter(filter: StorageLocationFilter) = intent {
        applyMutation(FridgeMutation.SelectStorageFilter(filter))
    }

    private fun addIngredient() = intent {
        postSideEffect(FridgeSideEffect.ShowCaptureModeBottomSheet)
    }

    private fun selectSortOrder(order: IngredientSortOrder) = intent {
        applyMutation(FridgeMutation.SelectSortOrder(order))
    }

    private fun observeBadge() = intent {
        observeUnreadNotification().collect { hasUnread ->
            applyMutation(FridgeMutation.BadgeUpdated(hasUnread))
        }
    }

    private fun navigateToNotification() = intent {
        markAllNotificationsAsRead()
        postSideEffect(FridgeSideEffect.NavigateToNotification)
    }

    private fun selectCategoryFilter(filter: CategoryFilter) = intent {
        applyMutation(
            FridgeMutation.SelectCategoryFilter(
                storageFilter = state.selectedStorageFilter,
                categoryFilter = filter,
            )
        )
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
                    categoryFilters = state.categoryFilters + (mutation.storageFilter to mutation.categoryFilter),
                )
            }

            is FridgeMutation.SelectSortOrder -> reduce { state.copy(selectedSortOrder = mutation.order) }

            is FridgeMutation.BadgeUpdated -> reduce { state.copy(hasUnreadNotification = mutation.hasUnread) }
        }
    }
}

// TODO: UseCase 연결 후 제거
private val mockIngredients = listOf(
    FridgeIngredientUiModel(id = 1L, name = "제주 햇당근", category = FoodCategory.Veg, quantity = 1, cost = 6500, expiryDate = LocalDate.now().plusDays(28)),
    FridgeIngredientUiModel(id = 2L, name = "삼겹살", category = FoodCategory.MeatEggs, quantity = 1, cost = 0, expiryDate = LocalDate.now().minusDays(3)),
    FridgeIngredientUiModel(id = 3L, name = "서울우유 1L", category = FoodCategory.Dairy, quantity = 2, cost = 3200, expiryDate = LocalDate.now().plusDays(5)),
    FridgeIngredientUiModel(id = 4L, name = "새우", category = FoodCategory.Sea, quantity = 3, cost = 12000, expiryDate = LocalDate.now().plusDays(1)),
    FridgeIngredientUiModel(id = 5L, name = "청포도", category = FoodCategory.Fruit, quantity = 1, cost = 4500, expiryDate = LocalDate.now().plusDays(14)),
    FridgeIngredientUiModel(id = 6L, name = "간장", category = FoodCategory.Sauce, quantity = 1, cost = 2800, expiryDate = LocalDate.now().plusDays(180)),
    FridgeIngredientUiModel(id = 7L, name = "콜라 1.5L", category = FoodCategory.Drink, quantity = 2, cost = 1800, expiryDate = LocalDate.now().plusDays(90)),
    FridgeIngredientUiModel(id = 8L, name = "두부", category = FoodCategory.Processed, quantity = 1, cost = 1500, expiryDate = LocalDate.now()),
    FridgeIngredientUiModel(id = 9L, name = "계란 10구", category = FoodCategory.MeatEggs, quantity = 10, cost = 3000, expiryDate = LocalDate.now().minusDays(1)),
    FridgeIngredientUiModel(id = 10L, name = "오징어", category = FoodCategory.Sea, quantity = 2, cost = 8000, expiryDate = LocalDate.now().plusDays(3)),
)
