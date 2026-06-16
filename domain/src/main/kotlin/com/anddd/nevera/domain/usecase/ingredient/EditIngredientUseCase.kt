package com.anddd.nevera.domain.usecase.ingredient

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.ingredient.EditIngredientError
import com.anddd.nevera.domain.model.ingredient.EditIngredientInput
import com.anddd.nevera.domain.model.ingredient.FridgeIngredient
import com.anddd.nevera.domain.repository.IngredientRepository
import javax.inject.Inject

class EditIngredientUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository,
) {
    suspend operator fun invoke(
        id: Long,
        input: EditIngredientInput,
    ): NeveraResult<FridgeIngredient, EditIngredientError> =
        ingredientRepository.editIngredient(id, input)
}
