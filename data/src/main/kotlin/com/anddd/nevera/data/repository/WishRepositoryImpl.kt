package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.WishRemoteDataSource
import com.anddd.nevera.data.mapper.error.toCreateWishError
import com.anddd.nevera.data.mapper.error.toUpdateWishError
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.domain.model.wish.CreateWishError
import com.anddd.nevera.domain.model.wish.UpdateWishError
import com.anddd.nevera.domain.model.wish.Wish
import com.anddd.nevera.domain.repository.WishRepository
import javax.inject.Inject

internal class WishRepositoryImpl @Inject constructor(
    private val wishRemoteDataSource: WishRemoteDataSource,
    private val apiCall: ApiCallExecutor,
) : WishRepository {

    override suspend fun createWish(name: String, amount: Long): NeveraResult<Wish, CreateWishError> {
        return apiCall {
            wishRemoteDataSource.createWish(name, amount)
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toCreateWishError() },
        )
    }

    override suspend fun updateWish(wishId: Long, name: String, amount: Long): NeveraResult<Wish, UpdateWishError> {
        return apiCall {
            wishRemoteDataSource.updateWish(wishId, name, amount)
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toUpdateWishError() },
        )
    }
}
