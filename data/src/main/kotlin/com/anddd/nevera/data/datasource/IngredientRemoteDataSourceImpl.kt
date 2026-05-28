package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.IngredientApi
import com.anddd.nevera.data.model.ingredient.DisposedIngredientResponse
import com.anddd.nevera.data.model.ingredient.IngredientResponse
import javax.inject.Inject

internal class IngredientRemoteDataSourceImpl @Inject constructor(
    private val ingredientApi: IngredientApi,
) : IngredientRemoteDataSource {

    override suspend fun getRescuedIngredients(
        offset: Int,
        limit: Int,
    ): ApiResponse<List<IngredientResponse>> {
        return ingredientApi.getRescuedIngredients(offset, limit)
    }

    override suspend fun getDisposedIngredients(
        offset: Int,
        limit: Int,
    ): ApiResponse<List<DisposedIngredientResponse>> {
        return ingredientApi.getDisposedIngredients(offset, limit)
    }
}
