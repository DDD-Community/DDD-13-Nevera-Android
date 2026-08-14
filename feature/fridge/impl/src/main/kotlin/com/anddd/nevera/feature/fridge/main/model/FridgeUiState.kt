package com.anddd.nevera.feature.fridge.main.model

import androidx.compose.runtime.Immutable
import com.anddd.nevera.core.mvi.NeveraState
import com.anddd.nevera.domain.model.ingredient.IngredientSortOrder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class FridgeUiState(
    val isLoading: Boolean = false,
    val hasUnreadNotification: Boolean = false,
    val selectedStorageFilter: StorageLocationFilter = StorageLocationFilter.All,
    val categoryFilters: ImmutableMap<StorageLocationFilter, CategoryFilter> = persistentMapOf(),
    val selectedSortOrder: IngredientSortOrder = IngredientSortOrder.ExpiryDate,
    val ingredients: ImmutableList<FridgeIngredientUiModel> = persistentListOf(),
) : NeveraState {
    val selectedCategoryFilter: CategoryFilter
        get() = categoryFilters[selectedStorageFilter] ?: CategoryFilter.All

    val totalCount: Int
        get() = ingredients.size
}
