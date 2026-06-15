package com.anddd.nevera.domain.usecase.ingredient

import javax.inject.Inject

class RequestIngredientFocusUseCase @Inject constructor(
    private val ingredientFocusEventBus: IngredientFocusEventBus,
) {
    suspend operator fun invoke(ingredientId: Long) {
        ingredientFocusEventBus.requestFocus(ingredientId)
    }
}
