package com.anddd.nevera.data.api

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.fridge.FridgeIngredientResponse
import com.anddd.nevera.data.model.ingredient.EditIngredientRequest
import com.anddd.nevera.data.model.ingredient.IngredientResponse
import com.anddd.nevera.data.model.ingredient.OcrIngredientDto
import com.anddd.nevera.data.model.ingredient.OcrJobResponse
import com.anddd.nevera.data.model.ingredient.RegisterIngredientRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

internal interface IngredientApi {

    @POST("api/v1/ocr/jobs")
    suspend fun createOcrJob(): ApiResponse<OcrJobResponse>

    @Multipart
    @POST("api/v1/ocr/extract")
    suspend fun extractIngredients(
        @Query("jobId") jobId: String,
        @Part file: MultipartBody.Part,
    ): ApiResponse<List<OcrIngredientDto>>

    @POST("api/v1/inventory")
    suspend fun registerIngredients(
        @Body items: List<RegisterIngredientRequest>,
    ): ApiResponse<Boolean>

    @PUT("api/v1/inventory/{id}")
    suspend fun editIngredient(
        @Path("id") id: Long,
        @Body request: EditIngredientRequest,
    ): ApiResponse<FridgeIngredientResponse>

    @GET("api/v1/savings/consumed")
    suspend fun getRescuedIngredients(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): ApiResponse<List<IngredientResponse>>

    @GET("api/v1/savings/wasted")
    suspend fun getDisposedIngredients(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): ApiResponse<List<IngredientResponse>>
}
