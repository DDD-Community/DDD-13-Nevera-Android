package com.anddd.nevera.domain.usecase.wish

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.wish.UpdateWishError
import com.anddd.nevera.domain.model.wish.Wish
import com.anddd.nevera.domain.repository.WishRepository
import javax.inject.Inject

class UpdateWishUseCase @Inject constructor(
    private val wishRepository: WishRepository,
) {
    suspend operator fun invoke(wishId: Long, name: String, amount: Long): NeveraResult<Wish, UpdateWishError> =
        wishRepository.updateWish(wishId, name, amount)
}
