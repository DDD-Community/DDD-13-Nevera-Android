package com.anddd.nevera.feature.fridge.main.model

import com.anddd.nevera.core.mvi.NeveraState

data class FridgeUiState(
    val isLoading: Boolean = false,
    val hasUnreadNotification: Boolean = false,
    val selectedStorageFilter: StorageLocationFilter = StorageLocationFilter.All,
    val categoryFilters: Map<StorageLocationFilter, CategoryFilter> = emptyMap(),
    val totalCount: Int = 0,
    val selectedSortOrder: IngredientSortOrder = IngredientSortOrder.ExpiryDate,
    val ingredients: List<FridgeIngredientUiModel> = emptyList(),
) : NeveraState {
    val selectedCategoryFilter: CategoryFilter
        get() = categoryFilters[selectedStorageFilter] ?: CategoryFilter.All
}
