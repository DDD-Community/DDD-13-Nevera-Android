package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.IngredientRemoteDataSource
import com.anddd.nevera.data.datasource.OcrDataSource
import com.anddd.nevera.data.datasource.OcrProgressDataSource
import com.anddd.nevera.data.datasource.OcrProgressResponse
import com.anddd.nevera.data.mapper.error.toCommonError
import com.anddd.nevera.data.mapper.error.toOcrExtractError
import com.anddd.nevera.data.mapper.error.toRegisterIngredientError
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.data.mapper.toProgressResult
import com.anddd.nevera.data.mapper.toRequest
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.ingredient.Ingredient
import com.anddd.nevera.domain.model.ingredient.OcrExtractError
import com.anddd.nevera.domain.model.ingredient.OcrJobId
import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.domain.model.ingredient.OcrProgressResult
import com.anddd.nevera.domain.model.ingredient.RegisterIngredientError
import com.anddd.nevera.domain.repository.IngredientRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class IngredientRepositoryImpl @Inject constructor(
    private val ocrDataSource: OcrDataSource,
    private val ocrProgressDataSource: OcrProgressDataSource,
    private val remoteDataSource: IngredientRemoteDataSource,
    private val apiCall: ApiCallExecutor,
) : IngredientRepository {

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
            remoteDataSource.registerIngredients(items.map { it.toRequest() })
        }.map(
            transformSuccess = { },
            transformFailure = { it.toRegisterIngredientError() },
        )

    override suspend fun getRescuedIngredients(
        offset: Int,
        limit: Int,
    ): NeveraResult<List<Ingredient>, CommonError> {
        return apiCall {
            remoteDataSource.getRescuedIngredients(offset, limit)
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
            remoteDataSource.getDisposedIngredients(offset, limit)
        }.map(
            transformSuccess = { list -> list.map { it.toDomain() } },
            transformFailure = { it.toCommonError() },
        )
    }
}
