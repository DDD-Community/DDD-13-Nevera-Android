package com.anddd.nevera.feature.ingredient.main.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface IngredientIntent : NeveraIntent {
    data object StartScan  : IngredientIntent
    data object CancelScan : IngredientIntent
    data class  UpdateItem(val item: IngredientUiModel) : IngredientIntent
    data class  ToggleAllSelection(val selectAll: Boolean) : IngredientIntent
    data object AddEmptyItem : IngredientIntent
    data object Register     : IngredientIntent
}
