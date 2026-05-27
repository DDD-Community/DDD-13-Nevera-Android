package com.anddd.nevera.domain.model.ingredient

data class Ingredient(
    val id: Long,
    val name: String,
    val category: FoodCategory,
    val categoryName: String,
    val quantity: Int,
    val cost: Int,
)
