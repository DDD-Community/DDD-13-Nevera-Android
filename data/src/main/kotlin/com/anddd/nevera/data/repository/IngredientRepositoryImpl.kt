package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.FridgeRemoteDataSource
import com.anddd.nevera.data.datasource.IngredientRemoteDataSource
import com.anddd.nevera.data.datasource.OcrDataSource
import com.anddd.nevera.data.datasource.OcrProgressDataSource
import com.anddd.nevera.data.datasource.OcrProgressResponse
import com.anddd.nevera.data.mapper.error.toCommonError
import com.anddd.nevera.data.mapper.error.toEditIngredientError
import com.anddd.nevera.data.mapper.error.toOcrExtractError
import com.anddd.nevera.data.mapper.error.toRegisterIngredientError
import com.anddd.nevera.data.mapper.toApiString
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.data.mapper.toProcessIngredientError
import com.anddd.nevera.data.mapper.toProgressResult
import com.anddd.nevera.data.mapper.toRequest
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class IngredientRepositoryImpl @Inject constructor(
    private val ocrDataSource: OcrDataSource,
    private val ocrProgressDataSource: OcrProgressDataSource,
    private val ingredientRemoteDataSource: IngredientRemoteDataSource,
    private val fridgeRemoteDataSource: FridgeRemoteDataSource,
    private val apiCall: ApiCallExecutor,
) : IngredientRepository {

    private val _fridgeIngredients = MutableStateFlow<List<FridgeIngredient>>(emptyList())
    private val _rescuedIngredients = MutableStateFlow<List<Ingredient>?>(null)
    private val _disposedIngredients = MutableStateFlow<List<Ingredient>?>(null)

    override suspend fun createOcrJob(): NeveraResult<OcrJobId, OcrExtractError> {
        return apiCall {
            ocrDataSource.createOcrJob()
        }.map(
            transformSuccess = { OcrJobId(it.jobId) },
            transformFailure = { it.toOcrExtractError() },
        )
    }

    override fun observeOcrProgress(jobId: OcrJobId): Flow<OcrProgressResult> =
        ocrProgressDataSource.observeOcrProgress(jobId.value)
            .map { response ->
                when (response) {
                    OcrProgressResponse.Opened -> OcrProgressResult.Opened
                    is OcrProgressResponse.Progress ->
                        OcrProgressResult.Progress(response.response.toProgressResult())
                }
            }
            .catch { throwable ->
                if (throwable is CancellationException) throw throwable
                emit(
                    OcrProgressResult.Progress(
                        NeveraResult.Failure(
                            NetworkError.UnknownError(throwable = throwable).toOcrExtractError(),
                        ),
                    ),
                )
            }

    override suspend fun extractIngredients(
        jobId: OcrJobId,
        imageUri: String,
    ): NeveraResult<List<OcrIngredient>, OcrExtractError> {
        return apiCall {
            ocrDataSource.extractIngredients(jobId.value, imageUri)
        }.map(
            transformSuccess = { list -> list.map { it.toDomain() } },
            transformFailure = { it.toOcrExtractError() },
        )
    }

    override suspend fun registerIngredients(
        items: List<OcrIngredient>,
    ): NeveraResult<Unit, RegisterIngredientError> =
        apiCall {
            ingredientRemoteDataSource.registerIngredients(items.map { it.toRequest() })
        }.map(
            transformSuccess = { },
            transformFailure = { it.toRegisterIngredientError() },
        )

    override suspend fun editIngredient(
        id: Long,
        input: EditIngredientInput,
    ): NeveraResult<FridgeIngredient, EditIngredientError> =
        apiCall {
            ingredientRemoteDataSource.editIngredient(id = id, request = input.toRequest())
        }.map(
            transformSuccess = { response ->
                val updated = response.toDomain()
                _fridgeIngredients.update { current ->
                    val index = current.indexOfFirst { it.id == updated.id }
                    if (index < 0) return@update current
                    val existing = current[index]
                    // 필터 키가 바뀌면 해당 항목을 캐시에서 제거, 안 바뀌면 교체
                    if (existing.storageLocation != updated.storageLocation ||
                        existing.category != updated.category
                    ) {
                        current.toMutableList().also { it.removeAt(index) }
                    } else {
                        current.toMutableList().also { it[index] = updated }
                    }
                }
                updated
            },
            transformFailure = { it.toEditIngredientError() },
        )

    override suspend fun getFridgeIngredients(
        storageLocation: StorageLocation?,
        category: FoodCategory?,
        sortOrder: IngredientSortOrder,
        page: Int,
        size: Int,
    ): NeveraResult<List<FridgeIngredient>, CommonError> =
        apiCall {
            fridgeRemoteDataSource.getFridgeIngredients(
                storageLocation = storageLocation?.toApiString(),
                category = category?.toApiString(),
                sortType = sortOrder.toApiString(),
                page = page,
                size = size,
            )
        }.map(
            transformSuccess = { response ->
                val items = response.content.map { dto -> dto.toDomain() }
                _fridgeIngredients.value = items
                items
            },
            transformFailure = { it.toCommonError() },
        )

    override fun observeFridgeIngredients(): Flow<List<FridgeIngredient>> = _fridgeIngredients.asStateFlow()

    override fun observeRescuedIngredients(): Flow<List<Ingredient>> = _rescuedIngredients.filterNotNull()

    override fun observeDisposedIngredients(): Flow<List<Ingredient>> = _disposedIngredients.filterNotNull()

    override suspend fun loadProcessedIngredients() {
        coroutineScope {
            launch {
                getRescuedIngredients(offset = 0, limit = PROCESSED_INGREDIENT_LIMIT)
                    .onSuccess { _rescuedIngredients.value = it }
            }
            launch {
                getDisposedIngredients(offset = 0, limit = PROCESSED_INGREDIENT_LIMIT)
                    .onSuccess { _disposedIngredients.value = it }
            }
        }
    }

    override suspend fun getFridgeIngredientById(id: Long): NeveraResult<FridgeIngredient, CommonError> =
        apiCall {
            fridgeRemoteDataSource.getFridgeIngredientById(id)
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toCommonError() },
        )

    override suspend fun getRescuedIngredients(
        offset: Int,
        limit: Int,
    ): NeveraResult<List<Ingredient>, CommonError> {
        return apiCall {
            ingredientRemoteDataSource.getRescuedIngredients(offset, limit)
        }.map(
            transformSuccess = { list -> list.map { it.toDomain() } },
            transformFailure = { it.toCommonError() },
        )
    }

    override suspend fun getDisposedIngredients(
        offset: Int,
        limit: Int,
    ): NeveraResult<List<Ingredient>, CommonError> {
        return apiCall {
            ingredientRemoteDataSource.getDisposedIngredients(offset, limit)
        }.map(
            transformSuccess = { list -> list.map { it.toDomain() } },
            transformFailure = { it.toCommonError() },
        )
    }

    private companion object {
        const val PROCESSED_INGREDIENT_LIMIT = 10
    }

    override suspend fun processIngredient(
        inventoryId: Long,
        processType: ProcessType,
        ratio: ProcessRatio,
    ): NeveraResult<IngredientProcessResult, ProcessIngredientError> =
        apiCall {
            fridgeRemoteDataSource.processIngredient(
                inventoryId = inventoryId,
                status = processType.toApiString(),
                ratio = ratio.value,
            )
        }.map(
            transformSuccess = { response ->
                val result = response.toDomain()
                if (result.completed) {
                    // 완료: 캐시에서 항목 제거
                    _fridgeIngredients.update { current ->
                        current.filter { it.id != inventoryId }
                    }
                } else {
                    // 부분 처리: 잔여 금액으로 cost 갱신
                    _fridgeIngredients.update { current ->
                        val index = current.indexOfFirst { it.id == inventoryId }
                        if (index < 0) return@update current
                        current.toMutableList().also {
                            it[index] = it[index].copy(cost = result.remainingAmount)
                        }
                    }
                }
                result
            },
            transformFailure = { it.toProcessIngredientError() },
        )
}
