package com.anddd.nevera.data.api

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.wish.CreateWishRequest
import com.anddd.nevera.data.model.wish.UpdateWishRequest
import com.anddd.nevera.data.model.wish.WishResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

internal interface WishApi {

    @POST("api/v1/savings/wish")
    suspend fun createWish(@Body request: CreateWishRequest): ApiResponse<WishResponse>

    @PUT("api/v1/savings/wish/{wishId}")
    suspend fun updateWish(
        @Path("wishId") wishId: Long,
        @Body request: UpdateWishRequest
    ): ApiResponse<WishResponse>
}
