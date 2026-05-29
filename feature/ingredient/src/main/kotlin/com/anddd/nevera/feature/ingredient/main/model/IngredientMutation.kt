package com.anddd.nevera.feature.ingredient.main.model

import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface IngredientMutation : NeveraMutation {
    data class ImageUriLoaded(val imageUri: String) : IngredientMutation
    data class ProgressUpdated(val progress: Float) : IngredientMutation
    data class ScanCompleted(val items: List<IngredientUiModel>) : IngredientMutation
    data object ScanFailed : IngredientMutation
    data class ItemUpdated(val item: IngredientUiModel) : IngredientMutation
    data class AllSelectionToggled(val selectAll: Boolean) : IngredientMutation
    data object EmptyItemAdded : IngredientMutation
    data object RegisterStarted : IngredientMutation
    data object RegisterFailed : IngredientMutation
}
