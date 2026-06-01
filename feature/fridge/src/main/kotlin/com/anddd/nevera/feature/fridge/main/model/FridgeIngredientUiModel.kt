package com.anddd.nevera.feature.fridge.main.model

import com.anddd.nevera.domain.model.ingredient.FoodCategory
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class FridgeIngredientUiModel(
    val id: Long,
    val name: String,
    val category: FoodCategory,
    val quantity: Int,
    val cost: Int,
    val expiryDate: LocalDate?,
) {
    val dDayLabel: String
        get() {
            val days = expiryDate?.let { ChronoUnit.DAYS.between(LocalDate.now(), it) }
                ?: return ""
            return when {
                days > 0L -> "D-$days"
                days == 0L -> "D-Day"
                else -> "D+${-days}"
            }
        }
}
