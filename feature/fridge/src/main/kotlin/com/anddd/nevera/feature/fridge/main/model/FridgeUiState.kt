package com.anddd.nevera.feature.fridge.main.model

import com.anddd.nevera.core.mvi.NeveraState

data class FridgeUiState(
    val isLoading: Boolean = false,
    val selectedStorageFilter: StorageLocationFilter = StorageLocationFilter.All,
    val categoryFilters: Map<StorageLocationFilter, CategoryFilter> = emptyMap(),
) : NeveraState {
    val selectedCategoryFilter: CategoryFilter
        get() = categoryFilters[selectedStorageFilter] ?: CategoryFilter.All
}
