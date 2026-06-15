package com.anddd.nevera.domain.model.ingredient

import java.time.Instant
import java.time.LocalDate

data class FridgeIngredient(
    val id: Long,
    val name: String,
    val category: FoodCategory,
    val storageLocation: StorageLocation,
    val quantity: Int,
    val cost: Int,
    val expiryDate: LocalDate,
    val createdAt: Instant,
)
