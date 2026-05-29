package com.anddd.nevera.feature.ingredient.main.model

import com.anddd.nevera.core.mvi.NeveraState

sealed interface IngredientPhase {
    data object Scanning    : IngredientPhase
    data object ScanSuccess : IngredientPhase
    data object Registering : IngredientPhase
}

data class IngredientUiState(
    val phase: IngredientPhase = IngredientPhase.Scanning,
    val imageUri: String = "",
    val scanProgress: Float = 0f,
    val items: List<IngredientUiModel> = emptyList(),
) : NeveraState {

    val isAllSelected: Boolean
        get() = items.isNotEmpty() && items.all { it.isSelected }

    val selectedItems: List<IngredientUiModel>
        get() = items.filter { it.isSelected }

    val isRegisterEnabled: Boolean
        get() = selectedItems.isNotEmpty()

    val totalCost: Int
        get() = selectedItems.sumOf { it.cost }
}
