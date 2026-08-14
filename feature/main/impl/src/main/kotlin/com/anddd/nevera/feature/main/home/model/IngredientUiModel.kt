package com.anddd.nevera.feature.main.home.model

import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.Ingredient
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

data class IngredientUiModel(
    val id: Long,
    val name: String,
    val category: FoodCategory,
    val categoryName: String,
    val quantity: Int,
    val cost: Int,
)

internal fun Ingredient.toUiModel(): IngredientUiModel = IngredientUiModel(
    id = id,
    name = name,
    category = category,
    categoryName = categoryName,
    quantity = quantity,
    cost = cost,
)

internal fun List<Ingredient>.toUiModel(): PersistentList<IngredientUiModel> = map { it.toUiModel() }.toPersistentList()
