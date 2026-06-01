package com.anddd.nevera.feature.fridge.main.model

import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface FridgeMutation : NeveraMutation {

    data object Loading : FridgeMutation

    data object LoadComplete : FridgeMutation

    data class ShowIngredients(val ingredients: List<FridgeIngredientUiModel>) : FridgeMutation

    data class SelectStorageFilter(val filter: StorageLocationFilter) : FridgeMutation

    data class SelectCategoryFilter(
        val storageFilter: StorageLocationFilter,
        val categoryFilter: CategoryFilter,
    ) : FridgeMutation

    data class SelectSortOrder(val order: IngredientSortOrder) : FridgeMutation

    data class BadgeUpdated(val hasUnread: Boolean) : FridgeMutation
}
