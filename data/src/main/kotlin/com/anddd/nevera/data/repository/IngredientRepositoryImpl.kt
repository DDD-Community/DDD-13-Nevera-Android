package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.IngredientRemoteDataSource
import com.anddd.nevera.data.datasource.OcrDataSource
import com.anddd.nevera.data.mapper.error.toCommonError
import com.anddd.nevera.data.mapper.error.toOcrExtractError
import com.anddd.nevera.data.mapper.error.toRegisterIngredientError
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.data.mapper.toRequest
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.ingredient.Ingredient
import com.anddd.nevera.domain.model.ingredient.OcrExtractError
import com.anddd.nevera.domain.model.ingredient.OcrIngredient
import com.anddd.nevera.domain.model.ingredient.RegisterIngredientError
import com.anddd.nevera.domain.repository.IngredientRepository
import javax.inject.Inject

internal class IngredientRepositoryImpl @Inject constructor(
    private val ocrDataSource: OcrDataSource,
    private val remoteDataSource: IngredientRemoteDataSource,
    private val apiCall: ApiCallExecutor,
) : IngredientRepository {

    override suspend fun extractIngredients(
        imageUri: String,
    ): NeveraResult<List<OcrIngredient>, OcrExtractError> =
        apiCall {
            ocrDataSource.extractIngredients(imageUri)
        }.map(
            transformSuccess = { list -> list.map { it.toDomain() } },
            transformFailure = { it.toOcrExtractError() },
        )

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