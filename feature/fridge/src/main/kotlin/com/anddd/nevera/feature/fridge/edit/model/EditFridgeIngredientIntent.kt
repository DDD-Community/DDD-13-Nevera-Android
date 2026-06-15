package com.anddd.nevera.feature.fridge.edit.model

import com.anddd.nevera.core.mvi.NeveraIntent
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import java.time.LocalDate

sealed interface EditFridgeIngredientIntent : NeveraIntent {
    data class UpdateQuantity(val quantity: Int) : EditFridgeIngredientIntent
    data class UpdateCost(val cost: Int) : EditFridgeIngredientIntent
    data class UpdateCategory(val category: FoodCategory) : EditFridgeIngredientIntent
    data class UpdateStorageLocation(val location: StorageLocation) : EditFridgeIngredientIntent
    data class UpdateExpiryDate(val date: LocalDate) : EditFridgeIngredientIntent
    data object ConfirmClick : EditFridgeIngredientIntent
    data object CloseClick : EditFridgeIngredientIntent
    data object CategoryFieldClick : EditFridgeIngredientIntent
    data object StorageLocationFieldClick : EditFridgeIngredientIntent
    data object ExpiryDateFieldClick : EditFridgeIngredientIntent
}
