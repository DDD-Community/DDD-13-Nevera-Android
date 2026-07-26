package com.anddd.nevera.domain.testutil

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
import com.anddd.nevera.domain.repository.IngredientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * [IngredientRepository]의 테스트용 구현.
 *
 * 이 마일스톤에서 쓰는 것은 처리(`processIngredient`)와 처리 목록 재적재
 * (`loadProcessedIngredients`) 두 가지다. 나머지는 호출되면 원인이 드러나도록
 * 예외를 던지거나 빈 값을 돌려준다.
 */
class FakeIngredientRepository(
    var processResult: NeveraResult<IngredientProcessResult, ProcessIngredientError> =
        NeveraResult.Success(defaultProcessResult()),
) : IngredientRepository {

    data class ProcessCall(
        val inventoryId: Long,
        val processType: ProcessType,
        val ratio: ProcessRatio,
    )

    val processCalls = mutableListOf<ProcessCall>()
    var loadProcessedIngredientsCount: Int = 0
        private set

    private val fridgeIngredients = MutableStateFlow<List<FridgeIngredient>>(emptyList())
    private val rescuedIngredients = MutableStateFlow<List<Ingredient>>(emptyList())
    private val disposedIngredients = MutableStateFlow<List<Ingredient>>(emptyList())

    override suspend fun processIngredient(
        inventoryId: Long,
        processType: ProcessType,
        ratio: ProcessRatio,
    ): NeveraResult<IngredientProcessResult, ProcessIngredientError> {
        processCalls += ProcessCall(inventoryId, processType, ratio)
        return processResult
    }

    override suspend fun loadProcessedIngredients() {
        loadProcessedIngredientsCount++
    }

    override fun observeFridgeIngredients(): Flow<List<FridgeIngredient>> = fridgeIngredients.asStateFlow()

    override fun observeRescuedIngredients(): Flow<List<Ingredient>> = rescuedIngredients.asStateFlow()

    override fun observeDisposedIngredients(): Flow<List<Ingredient>> = disposedIngredients.asStateFlow()

    override suspend fun getRescuedIngredients(
        offset: Int,
        limit: Int,
    ): NeveraResult<List<Ingredient>, CommonError> = NeveraResult.Success(emptyList())

    override suspend fun getDisposedIngredients(
        offset: Int,
        limit: Int,
    ): NeveraResult<List<Ingredient>, CommonError> = NeveraResult.Success(emptyList())

    override suspend fun getFridgeIngredients(
        storageLocation: StorageLocation?,
        category: FoodCategory?,
        sortOrder: IngredientSortOrder,
        page: Int,
        size: Int,
    ): NeveraResult<List<FridgeIngredient>, CommonError> = NeveraResult.Success(emptyList())

    override suspend fun createOcrJob(): NeveraResult<OcrJobId, OcrExtractError> = notStubbed("createOcrJob")

    override fun observeOcrProgress(jobId: OcrJobId): Flow<OcrProgressResult> = notStubbed("observeOcrProgress")

    override suspend fun extractIngredients(
        jobId: OcrJobId,
        imageUri: String,
    ): NeveraResult<List<OcrIngredient>, OcrExtractError> = notStubbed("extractIngredients")

    override suspend fun registerIngredients(
        items: List<OcrIngredient>,
    ): NeveraResult<Unit, RegisterIngredientError> = notStubbed("registerIngredients")

    override suspend fun editIngredient(
        id: Long,
        input: EditIngredientInput,
    ): NeveraResult<FridgeIngredient, EditIngredientError> = notStubbed("editIngredient")

    override suspend fun getFridgeIngredientById(id: Long): NeveraResult<FridgeIngredient, CommonError> =
        notStubbed("getFridgeIngredientById")

    private fun notStubbed(name: String): Nothing =
        throw UnsupportedOperationException("FakeIngredientRepository.$name 은 이 테스트에서 준비되지 않았다")

    companion object {
        fun defaultProcessResult(
            inventoryId: Long = 1L,
            completed: Boolean = false,
            remainingAmount: Int = 5_000,
        ): IngredientProcessResult = IngredientProcessResult(
            inventoryId = inventoryId,
            processedStatus = ProcessType.Consumed,
            processedRatio = 50,
            processedAmount = 5_000,
            consumedRatio = 50,
            wastedRatio = 0,
            remainingRatio = 50,
            remainingAmount = remainingAmount,
            inventoryStatus = "ACTIVE",
            completed = completed,
        )
    }
}
