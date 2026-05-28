package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.ingredient.DisposedIngredientResponse
import com.anddd.nevera.domain.model.ingredient.Ingredient

internal fun DisposedIngredientResponse.toDomain(): Ingredient = Ingredient(
    id = id,
    name = name,
    category = category.toFoodCategory(),
    categoryName = categoryDisplayName,
    quantity = quantity,
    cost = cost,
)
