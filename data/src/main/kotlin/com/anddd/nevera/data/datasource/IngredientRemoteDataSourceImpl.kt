package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.IngredientApi
import com.anddd.nevera.data.model.fridge.FridgeIngredientResponse
import com.anddd.nevera.data.model.ingredient.EditIngredientRequest
import com.anddd.nevera.data.model.ingredient.IngredientResponse
import com.anddd.nevera.data.model.ingredient.RegisterIngredientRequest
import javax.inject.Inject

internal class IngredientRemoteDataSourceImpl @Inject constructor(
    private val ingredientApi: IngredientApi,
) : IngredientRemoteDataSource {

    override suspend fun registerIngredients(
        items: List<RegisterIngredientRequest>,
    ): ApiResponse<Boolean> = ingredientApi.registerIngredients(items)

    override suspend fun editIngredient(
        id: Long,
        request: EditIngredientRequest,
    ): ApiResponse<FridgeIngredientResponse> = ingredientApi.editIngredient(id, request)

    override suspend fun getRescuedIngredients(
        offset: Int,
        limit: Int,
    ): ApiResponse<List<IngredientResponse>> {
        return ingredientApi.getRescuedIngredients(offset, limit)
    }

    override suspend fun getDisposedIngredients(
        offset: Int,
        limit: Int,
    ): ApiResponse<List<IngredientResponse>> {
        return ingredientApi.getDisposedIngredients(offset, limit)
    }
}
