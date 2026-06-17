package com.anddd.nevera.feature.ingredient.main.model

import androidx.compose.runtime.Immutable
import com.anddd.nevera.core.mvi.NeveraState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

sealed interface IngredientPhase {
    data object Scanning : IngredientPhase
    data object ScanSuccess : IngredientPhase
    data object Registering : IngredientPhase
}

@Immutable
data class IngredientUiState(
    val phase: IngredientPhase = IngredientPhase.Scanning,
    val imageUri: String = "",
    val scanProgress: Float = 0f,
    val items: ImmutableList<IngredientUiModel> = persistentListOf(),
) : NeveraState {

    val isAllSelected: Boolean
        get() = items.isNotEmpty() && items.all { it.isSelected }

    val selectedItems: ImmutableList<IngredientUiModel>
        get() = items.filter { it.isSelected }.toImmutableList()

    val isRegisterEnabled: Boolean
        get() = selectedItems.isNotEmpty()

    val totalCost: Int
        get() = selectedItems.sumOf { it.cost }
}
