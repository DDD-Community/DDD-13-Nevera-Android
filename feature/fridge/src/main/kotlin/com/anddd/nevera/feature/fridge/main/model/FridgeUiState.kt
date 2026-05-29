package com.anddd.nevera.feature.fridge.main.model

import com.anddd.nevera.core.mvi.NeveraState

data class FridgeUiState(
    val isLoading: Boolean = false,
    val selectedStorageFilter: StorageLocationFilter = StorageLocationFilter.All,
    val selectedCategoryFilter: CategoryFilter = CategoryFilter.All,
) : NeveraState
