package com.anddd.nevera.feature.ingredient.main.model

import com.anddd.nevera.core.mvi.NeveraState

data class IngredientUiState(
    val isLoading: Boolean = false,
) : NeveraState
