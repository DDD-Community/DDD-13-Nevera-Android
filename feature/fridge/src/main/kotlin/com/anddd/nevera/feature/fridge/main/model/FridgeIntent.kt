package com.anddd.nevera.feature.fridge.main.model

import com.anddd.nevera.core.mvi.NeveraIntent
import com.anddd.nevera.domain.model.ingredient.IngredientSortOrder

sealed interface FridgeIntent : NeveraIntent {

    data class SelectStorageFilter(val filter: StorageLocationFilter) : FridgeIntent

    data class SelectCategoryFilter(val filter: CategoryFilter) : FridgeIntent

    data object AddIngredientClick : FridgeIntent

    data class SelectSortOrder(val order: IngredientSortOrder) : FridgeIntent

    data object NotificationIconClicked : FridgeIntent

    data class RescueClick(val item: FridgeIngredientUiModel) : FridgeIntent

    data class RescueConfirm(val item: FridgeIngredientUiModel, val ratio: Float) : FridgeIntent

    data class DisposeClick(val item: FridgeIngredientUiModel) : FridgeIntent

    data class DisposeConfirm(val item: FridgeIngredientUiModel, val ratio: Float) : FridgeIntent

    data class IngredientMoreClick(val item: FridgeIngredientUiModel) : FridgeIntent
}
