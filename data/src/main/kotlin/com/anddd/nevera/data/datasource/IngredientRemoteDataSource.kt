package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.ingredient.IngredientResponse
import com.anddd.nevera.data.model.ingredient.RegisterIngredientRequest

internal interface IngredientRemoteDataSource {
    suspend fun registerIngredients(items: List<RegisterIngredientRequest>): ApiResponse<Boolean>

    suspend fun getRescuedIngredients(
        offset: Int,
        limit: Int,
    ): ApiResponse<List<IngredientResponse>>

    suspend fun getDisposedIngredients(
        offset: Int,
        limit: Int,
    ): ApiResponse<List<IngredientResponse>>
}
