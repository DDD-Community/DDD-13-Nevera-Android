package com.anddd.nevera.domain.usecase.ingredient

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.ingredient.IngredientProcessResult
import com.anddd.nevera.domain.model.ingredient.ProcessIngredientError
import com.anddd.nevera.domain.model.ingredient.ProcessRatio
import com.anddd.nevera.domain.model.ingredient.ProcessType
import com.anddd.nevera.domain.repository.HomeRepository
import com.anddd.nevera.domain.repository.IngredientRepository
import javax.inject.Inject

class ProcessIngredientUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository,
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(
        inventoryId: Long,
        processType: ProcessType,
        ratio: ProcessRatio,
    ): NeveraResult<IngredientProcessResult, ProcessIngredientError> {
        val result = ingredientRepository.processIngredient(inventoryId, processType, ratio)
        if (result is NeveraResult.Success) {
            homeRepository.loadSummary()
            ingredientRepository.loadProcessedIngredients()
        }
        return result
    }
}
