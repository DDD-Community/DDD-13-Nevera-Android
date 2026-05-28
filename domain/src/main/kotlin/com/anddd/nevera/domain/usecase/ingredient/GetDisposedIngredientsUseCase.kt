package com.anddd.nevera.domain.usecase.ingredient

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.ingredient.Ingredient
import com.anddd.nevera.domain.repository.IngredientRepository
import javax.inject.Inject

class GetDisposedIngredientsUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository,
) {
    suspend operator fun invoke(
        offset: Int = 0,
        limit: Int = 10,
    ): NeveraResult<List<Ingredient>, CommonError> {
        return ingredientRepository.getDisposedIngredients(offset, limit)
    }
}
