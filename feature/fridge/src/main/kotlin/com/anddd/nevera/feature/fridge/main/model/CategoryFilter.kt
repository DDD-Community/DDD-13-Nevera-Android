package com.anddd.nevera.feature.fridge.main.model

import com.anddd.nevera.domain.model.ingredient.FoodCategory

sealed interface CategoryFilter {

    data object All : CategoryFilter

    data class Specific(val category: FoodCategory) : CategoryFilter
}
