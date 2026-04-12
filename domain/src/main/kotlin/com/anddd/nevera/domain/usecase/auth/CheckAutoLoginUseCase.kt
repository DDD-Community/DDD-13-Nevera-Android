package com.anddd.nevera.domain.usecase.auth

import com.anddd.nevera.domain.repository.TokenRepository
import javax.inject.Inject

class CheckAutoLoginUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {

    suspend operator fun invoke(): String? {
        val token = tokenRepository.getAccessToken()
        return if (!token.isNullOrEmpty()) token else null
    }
}
