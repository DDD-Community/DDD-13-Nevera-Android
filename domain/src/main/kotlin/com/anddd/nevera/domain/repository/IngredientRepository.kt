package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.ingredient.Ingredient

interface IngredientRepository {
    suspend fun getRescuedIngredients(
        offset: Int,
        limit: Int,
    ): NeveraResult<List<Ingredient>, CommonError>

    suspend fun getDisposedIngredients(
        offset: Int,
        limit: Int,
    ): NeveraResult<List<Ingredient>, CommonError>
}
