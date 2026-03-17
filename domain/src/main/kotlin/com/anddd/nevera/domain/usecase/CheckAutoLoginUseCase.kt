package com.anddd.nevera.domain.usecase

import com.anddd.nevera.domain.repository.TokenRepository
import javax.inject.Inject

class CheckAutoLoginUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {

    suspend operator fun invoke(): String? {
        val (token, userId) = tokenRepository.getSession()
        return if (token != null) userId else null
    }
}
