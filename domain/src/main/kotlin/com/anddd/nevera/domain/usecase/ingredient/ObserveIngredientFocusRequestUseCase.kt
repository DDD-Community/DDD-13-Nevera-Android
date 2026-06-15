package com.anddd.nevera.domain.usecase.ingredient

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIngredientFocusRequestUseCase @Inject constructor(
    private val ingredientFocusEventBus: IngredientFocusEventBus,
) {
    operator fun invoke(): Flow<Long> = ingredientFocusEventBus.focusRequests
}
