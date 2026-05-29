package com.anddd.nevera.feature.fridge.main.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface FridgeIntent : NeveraIntent {
    data object Load : FridgeIntent

    data class SelectStorageFilter(val filter: StorageLocationFilter) : FridgeIntent

    data class SelectCategoryFilter(val filter: CategoryFilter) : FridgeIntent

    data object AddIngredientClick : FridgeIntent

    data class SelectSortOrder(val order: IngredientSortOrder) : FridgeIntent

    data object NotificationIconClicked : FridgeIntent
}
