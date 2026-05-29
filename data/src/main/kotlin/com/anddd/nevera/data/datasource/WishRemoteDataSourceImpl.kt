package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.WishApi
import com.anddd.nevera.data.model.wish.CreateWishRequest
import com.anddd.nevera.data.model.wish.UpdateWishRequest
import com.anddd.nevera.data.model.wish.WishResponse
import javax.inject.Inject

internal class WishRemoteDataSourceImpl @Inject constructor(
    private val wishApi: WishApi,
) : WishRemoteDataSource {

    override suspend fun createWish(name: String, amount: Long): ApiResponse<WishResponse> {
        return wishApi.createWish(CreateWishRequest(name, amount))
    }

    override suspend fun updateWish(wishId: Long, name: String, amount: Long): ApiResponse<WishResponse> {
        return wishApi.updateWish(wishId, UpdateWishRequest(name, amount))
    }
}
