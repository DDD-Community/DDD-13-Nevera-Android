package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.ingredient.Ingredient
import com.anddd.nevera.domain.model.ingredient.OcrExtractError
import com.anddd.nevera.domain.model.ingredient.OcrJobId
import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.domain.model.ingredient.OcrProgressResult
import com.anddd.nevera.domain.model.ingredient.RegisterIngredientError
import kotlinx.coroutines.flow.Flow

interface IngredientRepository {

    suspend fun createOcrJob(): NeveraResult<OcrJobId, OcrExtractError>

    fun observeOcrProgress(jobId: OcrJobId): Flow<OcrProgressResult>

    suspend fun extractIngredients(
        jobId: OcrJobId,
        imageUri: String,
    ): NeveraResult<List<OcrIngredient>, OcrExtractError>

    suspend fun registerIngredients(items: List<OcrIngredient>): NeveraResult<Unit, RegisterIngredientError>

    suspend fun getRescuedIngredients(
        offset: Int,
        limit: Int,
    ): NeveraResult<List<Ingredient>, CommonError>

    suspend fun getDisposedIngredients(
        offset: Int,
        limit: Int,
    ): NeveraResult<List<Ingredient>, CommonError>
}
