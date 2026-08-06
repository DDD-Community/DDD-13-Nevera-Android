package com.anddd.nevera.data.testutil

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.datasource.FridgeRemoteDataSource
import com.anddd.nevera.data.model.fridge.FridgeIngredientResponse
import com.anddd.nevera.data.model.fridge.FridgeIngredientsResponse
import com.anddd.nevera.data.model.fridge.ProcessIngredientResponse

/**
 * [FridgeRemoteDataSource]의 테스트용 대역.
 *
 * 서버 호출을 흉내 내므로 미리 정해 둔 [ApiResponse]를 그대로 돌려준다.
 */
internal class FakeFridgeRemoteDataSource(
    var fridgeIngredientsResponse: ApiResponse<FridgeIngredientsResponse> = ApiResponse(
        result = FridgeIngredientsResponse(content = emptyList(), last = true, number = 0),
        error = null,
    ),
    var processResponse: ApiResponse<ProcessIngredientResponse> = ApiResponse(
        result = processIngredientResponse(),
        error = null,
    ),
) : FridgeRemoteDataSource {

    override suspend fun getFridgeIngredients(
        storageLocation: String?,
        category: String?,
        sortType: String,
        page: Int,
        size: Int,
    ): ApiResponse<FridgeIngredientsResponse> = fridgeIngredientsResponse

    override suspend fun getFridgeIngredientById(id: Long): ApiResponse<FridgeIngredientResponse> =
        ApiResponse(result = fridgeIngredientResponse(id = id), error = null)

    override suspend fun processIngredient(
        inventoryId: Long,
        status: String,
        ratio: Int,
    ): ApiResponse<ProcessIngredientResponse> = processResponse
}
