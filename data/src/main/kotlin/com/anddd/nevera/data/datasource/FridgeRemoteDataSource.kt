package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.fridge.FridgeIngredientResponse
import com.anddd.nevera.data.model.fridge.FridgeIngredientsResponse

internal interface FridgeRemoteDataSource {

    suspend fun getFridgeIngredients(
        storageLocation: String?,
        category: String?,
        sortType: String,
        page: Int,
        size: Int,
    ): ApiResponse<FridgeIngredientsResponse>

    suspend fun getFridgeIngredientById(id: Long): ApiResponse<FridgeIngredientResponse>
}
