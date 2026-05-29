package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.wish.WishResponse

internal interface WishRemoteDataSource {

    suspend fun createWish(name: String, amount: Long): ApiResponse<WishResponse>

    suspend fun updateWish(wishId: Long, name: String, amount: Long): ApiResponse<WishResponse>
}
