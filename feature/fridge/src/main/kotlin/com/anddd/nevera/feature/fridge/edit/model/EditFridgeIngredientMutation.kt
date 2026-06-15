package com.anddd.nevera.feature.fridge.edit.model

import com.anddd.nevera.core.mvi.NeveraMutation
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import java.time.LocalDate

sealed interface EditFridgeIngredientMutation : NeveraMutation {
    data class Loaded(
        val name: String,
        val quantity: Int,
        val cost: Int,
        val category: FoodCategory,
        val storageLocation: StorageLocation,
        val expiryDate: LocalDate,
    ) : EditFridgeIngredientMutation
    data object Loading : EditFridgeIngredientMutation
    data object UpdateComplete : EditFridgeIngredientMutation
    data class QuantityUpdated(val quantity: Int) : EditFridgeIngredientMutation
    data class CostUpdated(val cost: Int) : EditFridgeIngredientMutation
    data class CategoryUpdated(val category: FoodCategory) : EditFridgeIngredientMutation
    data class StorageLocationUpdated(val location: StorageLocation) : EditFridgeIngredientMutation
    data class ExpiryDateUpdated(val date: LocalDate) : EditFridgeIngredientMutation
}
