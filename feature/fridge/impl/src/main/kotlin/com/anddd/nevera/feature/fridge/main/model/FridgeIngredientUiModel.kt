package com.anddd.nevera.feature.fridge.main.model

import androidx.compose.runtime.Stable
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.FridgeIngredient
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Stable
data class FridgeIngredientUiModel(
    val id: Long,
    val name: String,
    val category: FoodCategory,
    val quantity: Int,
    val cost: Int,
    val expiryDate: LocalDate,
) {
    val dDayLabel: String
        get() {
            val days = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate)
            return when {
                days > 0L -> "D-$days"
                days == 0L -> "D-Day"
                else -> "D+${-days}"
            }
        }
}

internal fun FridgeIngredient.toUiModel() = FridgeIngredientUiModel(
    id = id,
    name = name,
    category = category,
    quantity = quantity,
    cost = cost,
    expiryDate = expiryDate,
)