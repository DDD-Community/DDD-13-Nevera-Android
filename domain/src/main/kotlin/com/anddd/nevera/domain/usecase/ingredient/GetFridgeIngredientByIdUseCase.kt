package com.anddd.nevera.domain.usecase.ingredient

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.ingredient.FridgeIngredient
import com.anddd.nevera.domain.repository.IngredientRepository
import javax.inject.Inject

class GetFridgeIngredientByIdUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository,
) {
    suspend operator fun invoke(id: Long): NeveraResult<FridgeIngredient, CommonError> =
        ingredientRepository.getFridgeIngredientById(id)
}
