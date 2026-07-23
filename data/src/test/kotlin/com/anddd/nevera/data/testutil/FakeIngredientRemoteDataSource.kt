package com.anddd.nevera.data.testutil

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.datasource.IngredientRemoteDataSource
import com.anddd.nevera.data.model.fridge.FridgeIngredientResponse
import com.anddd.nevera.data.model.ingredient.EditIngredientRequest
import com.anddd.nevera.data.model.ingredient.IngredientResponse
import com.anddd.nevera.data.model.ingredient.RegisterIngredientRequest

/**
 * [IngredientRemoteDataSource]의 테스트용 대역.
 *
 * `editIngredient`의 반환 타입이 [FridgeIngredientResponse]이므로 기본값도 그 응답 빌더를 쓴다.
 * [editRequests]는 저장소가 어떤 요청을 실제로 보냈는지 검증할 때 사용한다.
 */
internal class FakeIngredientRemoteDataSource(
    var editResponse: ApiResponse<FridgeIngredientResponse> = ApiResponse(
        result = fridgeIngredientResponse(),
        error = null,
    ),
    var rescuedResponse: ApiResponse<List<IngredientResponse>> = ApiResponse(emptyList(), null),
    var disposedResponse: ApiResponse<List<IngredientResponse>> = ApiResponse(emptyList(), null),
) : IngredientRemoteDataSource {

    val editRequests = mutableListOf<Pair<Long, EditIngredientRequest>>()

    override suspend fun editIngredient(
        id: Long,
        request: EditIngredientRequest,
    ): ApiResponse<FridgeIngredientResponse> {
        editRequests += id to request
        return editResponse
    }

    override suspend fun getRescuedIngredients(
        offset: Int,
        limit: Int,
    ): ApiResponse<List<IngredientResponse>> = rescuedResponse

    override suspend fun getDisposedIngredients(
        offset: Int,
        limit: Int,
    ): ApiResponse<List<IngredientResponse>> = disposedResponse

    override suspend fun registerIngredients(
        items: List<RegisterIngredientRequest>,
    ): ApiResponse<Boolean> = ApiResponse(result = true, error = null)
}
