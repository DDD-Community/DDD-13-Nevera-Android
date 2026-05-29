package com.anddd.nevera.domain.usecase.wish

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.wish.CreateWishError
import com.anddd.nevera.domain.model.wish.Wish
import com.anddd.nevera.domain.repository.WishRepository
import javax.inject.Inject

class CreateWishUseCase @Inject constructor(
    private val wishRepository: WishRepository,
) {
    suspend operator fun invoke(name: String, amount: Long): NeveraResult<Wish, CreateWishError> {
        return wishRepository.createWish(name, amount)
    }
}
