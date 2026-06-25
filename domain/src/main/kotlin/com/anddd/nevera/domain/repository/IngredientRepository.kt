package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.ingredient.EditIngredientError
import com.anddd.nevera.domain.model.ingredient.EditIngredientInput
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.FridgeIngredient
import com.anddd.nevera.domain.model.ingredient.Ingredient
import com.anddd.nevera.domain.model.ingredient.IngredientProcessResult
import com.anddd.nevera.domain.model.ingredient.IngredientSortOrder
import com.anddd.nevera.domain.model.ingredient.OcrExtractError
import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.domain.model.ingredient.OcrJobId
import com.anddd.nevera.domain.model.ingredient.OcrProgressResult
import com.anddd.nevera.domain.model.ingredient.ProcessIngredientError
import com.anddd.nevera.domain.model.ingredient.ProcessRatio
import com.anddd.nevera.domain.model.ingredient.ProcessType
import com.anddd.nevera.domain.model.ingredient.RegisterIngredientError
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import kotlinx.coroutines.flow.Flow

interface IngredientRepository {

    suspend fun createOcrJob(): NeveraResult<OcrJobId, OcrExtractError>

    fun observeOcrProgress(jobId: OcrJobId): Flow<OcrProgressResult>

    suspend fun extractIngredients(
        jobId: OcrJobId,
        imageUri: String,
    ): NeveraResult<List<OcrIngredient>, OcrExtractError>

    suspend fun registerIngredients(items: List<OcrIngredient>): NeveraResult<Unit, RegisterIngredientError>

    suspend fun editIngredient(
        id: Long,
        input: EditIngredientInput,
    ): NeveraResult<FridgeIngredient, EditIngredientError>

    suspend fun getFridgeIngredients(
        storageLocation: StorageLocation?,
        category: FoodCategory?,
        sortOrder: IngredientSortOrder,
        page: Int,
        size: Int,
    ): NeveraResult<List<FridgeIngredient>, CommonError>

    suspend fun getFridgeIngredientById(id: Long): NeveraResult<FridgeIngredient, CommonError>

    suspend fun getRescuedIngredients(
        offset: Int,
        limit: Int,
    ): NeveraResult<List<Ingredient>, CommonError>

    suspend fun getDisposedIngredients(
        offset: Int,
        limit: Int,
    ): NeveraResult<List<Ingredient>, CommonError>

    fun observeFridgeIngredients(): Flow<List<FridgeIngredient>>

    fun observeRescuedIngredients(): Flow<List<Ingredient>>

    fun observeDisposedIngredients(): Flow<List<Ingredient>>

    suspend fun loadProcessedIngredients()

    suspend fun processIngredient(
        inventoryId: Long,
        processType: ProcessType,
        ratio: ProcessRatio,
    ): NeveraResult<IngredientProcessResult, ProcessIngredientError>
}
