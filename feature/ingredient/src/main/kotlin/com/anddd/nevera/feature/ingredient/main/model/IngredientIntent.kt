package com.anddd.nevera.feature.ingredient.main.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface IngredientIntent : NeveraIntent {
    data object Load : IngredientIntent
    data object Submit : IngredientIntent
}
