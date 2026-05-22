package com.anddd.nevera.feature.ingredient.main.model

import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface IngredientMutation : NeveraMutation {
    data object LoadSuccess : IngredientMutation
}
