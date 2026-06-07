package com.anddd.nevera.feature.fridge.main.model

import androidx.compose.runtime.Immutable
import com.anddd.nevera.core.mvi.NeveraState
import com.anddd.nevera.domain.model.ingredient.IngredientSortOrder

@Immutable
data class FridgeUiState(
    val isLoading: Boolean = false,
    val hasUnreadNotification: Boolean = false,
    val selectedStorageFilter: StorageLocationFilter = StorageLocationFilter.All,
    val categoryFilters: Map<StorageLocationFilter, CategoryFilter> = emptyMap(),
    val selectedSortOrder: IngredientSortOrder = IngredientSortOrder.ExpiryDate,
    val ingredients: List<FridgeIngredientUiModel> = emptyList(),
) : NeveraState {
    val selectedCategoryFilter: CategoryFilter
        get() = categoryFilters[selectedStorageFilter] ?: CategoryFilter.All

    val totalCount: Int
        get() = ingredients.size
}
