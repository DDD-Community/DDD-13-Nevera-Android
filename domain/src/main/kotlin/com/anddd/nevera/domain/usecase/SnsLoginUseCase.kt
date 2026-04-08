package com.anddd.nevera.domain.usecase

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.model.LoginResult
import com.anddd.nevera.domain.model.SnsProvider
import com.anddd.nevera.domain.repository.TokenRepository
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

class SnsLoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) {

    suspend operator fun invoke(provider: SnsProvider, token: String): ApiResult<LoginResult> {
        val result = userRepository.snsLogin(provider, token)
        if (result is ApiResult.Success) {
            tokenRepository.setTokens(
                result.data.token,
                result.data.refreshToken
            )
        }
        return result
    }
}
