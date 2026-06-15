package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.FridgeRemoteDataSource
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.data.mapper.toApiString
import com.anddd.nevera.data.mapper.error.toCommonError
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.FridgeIngredient
import com.anddd.nevera.domain.model.ingredient.IngredientSortOrder
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import com.anddd.nevera.domain.repository.FridgeRepository
import javax.inject.Inject

internal class FridgeRepositoryImpl @Inject constructor(
    private val remoteDataSource: FridgeRemoteDataSource,
    private val apiCall: ApiCallExecutor,
) : FridgeRepository {

    override suspend fun getFridgeIngredients(
        storageLocation: StorageLocation?,
        category: FoodCategory?,
        sortOrder: IngredientSortOrder,
        page: Int,
        size: Int,
    ): NeveraResult<List<FridgeIngredient>, CommonError> =
        apiCall {
            remoteDataSource.getFridgeIngredients(
                storageLocation = storageLocation?.toApiString(),
                category = category?.toApiString(),
                sortType = sortOrder.toApiString(),
                page = page,
                size = size,
            )
        }.map(
            transformSuccess = { it.content.map { dto -> dto.toDomain() } },
            transformFailure = { it.toCommonError() },
        )

    override suspend fun getFridgeIngredientById(id: Long): NeveraResult<FridgeIngredient, CommonError> =
        apiCall {
            remoteDataSource.getFridgeIngredientById(id)
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toCommonError() },
        )
}
