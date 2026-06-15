package com.anddd.nevera.feature.fridge.edit.model

import androidx.compose.runtime.Immutable
import com.anddd.nevera.core.mvi.NeveraState
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import java.time.LocalDate

@Immutable
data class EditFridgeIngredientUiState(
    val isLoading: Boolean = false,
    val name: String = "",
    val quantity: Int = 0,
    val cost: Int = 0,
    val category: FoodCategory = FoodCategory.Etc,
    val storageLocation: StorageLocation = StorageLocation.Fridge,
    val expiryDate: LocalDate = LocalDate.now(),
) : NeveraState
