package com.anddd.nevera.data.api

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.fridge.FridgeIngredientResponse
import com.anddd.nevera.data.model.fridge.FridgeIngredientsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface FridgeApi {

    @GET("api/v1/fridge/ingredients")
    suspend fun getFridgeIngredients(
        @Query("storageLocation") storageLocation: String?,
        @Query("category") category: String?,
        @Query("sortType") sortType: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ApiResponse<FridgeIngredientsResponse>

    @GET("api/v1/fridge/inventory/{inventoryId}")
    suspend fun getFridgeIngredientById(
        @Path("inventoryId") id: Long,
    ): ApiResponse<FridgeIngredientResponse>
}
