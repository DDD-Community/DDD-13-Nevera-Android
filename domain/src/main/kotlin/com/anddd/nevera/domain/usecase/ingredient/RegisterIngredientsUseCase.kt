package com.anddd.nevera.domain.usecase.ingredient

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.domain.model.ingredient.RegisterIngredientError
import com.anddd.nevera.domain.repository.IngredientRepository
import javax.inject.Inject

class RegisterIngredientsUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository,
) {
    suspend operator fun invoke(items: List<OcrIngredient>): NeveraResult<Unit, RegisterIngredientError> =
        ingredientRepository.registerIngredients(items)
}
