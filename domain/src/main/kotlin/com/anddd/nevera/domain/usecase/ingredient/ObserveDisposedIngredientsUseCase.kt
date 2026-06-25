package com.anddd.nevera.domain.usecase.ingredient

import com.anddd.nevera.domain.model.ingredient.Ingredient
import com.anddd.nevera.domain.repository.IngredientRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDisposedIngredientsUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository,
) {
    operator fun invoke(): Flow<List<Ingredient>> = ingredientRepository.observeDisposedIngredients()
}
