package com.anddd.nevera.data.api

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.ingredient.IngredientResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface IngredientApi {

    @GET("api/v1/savings/consumed")
    suspend fun getRescuedIngredients(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): ApiResponse<List<IngredientResponse>>
}
