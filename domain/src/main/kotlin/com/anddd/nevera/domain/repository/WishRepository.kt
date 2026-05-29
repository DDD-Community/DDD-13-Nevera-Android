package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.wish.CreateWishError
import com.anddd.nevera.domain.model.wish.UpdateWishError
import com.anddd.nevera.domain.model.wish.Wish

interface WishRepository {

    suspend fun createWish(name: String, amount: Long): NeveraResult<Wish, CreateWishError>

    suspend fun updateWish(wishId: Long, name: String, amount: Long): NeveraResult<Wish, UpdateWishError>
}
