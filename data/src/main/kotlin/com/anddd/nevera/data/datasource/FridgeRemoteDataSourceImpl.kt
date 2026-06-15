package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.FridgeApi
import com.anddd.nevera.data.model.fridge.FridgeIngredientResponse
import com.anddd.nevera.data.model.fridge.FridgeIngredientsResponse
import javax.inject.Inject

internal class FridgeRemoteDataSourceImpl @Inject constructor(
    private val fridgeApi: FridgeApi,
) : FridgeRemoteDataSource {

    override suspend fun getFridgeIngredients(
        storageLocation: String?,
        category: String?,
        sortType: String,
        page: Int,
        size: Int,
    ): ApiResponse<FridgeIngredientsResponse> =
        fridgeApi.getFridgeIngredients(
            storageLocation = storageLocation,
            category = category,
            sortType = sortType,
            page = page,
            size = size,
        )

    override suspend fun getFridgeIngredientById(id: Long): ApiResponse<FridgeIngredientResponse> =
        fridgeApi.getFridgeIngredientById(id)
}
