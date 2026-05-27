package com.anddd.nevera.feature.ingredient.main.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface IngredientSideEffect : NeveraSideEffect {
    data class ShowToast(val message: String) : IngredientSideEffect
    data object NavigateBack : IngredientSideEffect
}
