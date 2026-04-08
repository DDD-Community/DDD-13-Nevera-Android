package com.anddd.nevera.domain.usecase

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.model.LoginResult
import com.anddd.nevera.domain.model.LoginProvider
import com.anddd.nevera.domain.repository.UserRepository
import com.anddd.nevera.domain.repository.TokenRepository
import javax.inject.Inject

class SnsLoginUseCase @Inject constructor(
    private val authRepository: UserRepository,
    private val tokenRepository: TokenRepository
) {

    suspend operator fun invoke(token: String): ApiResult<LoginResult> {
        val result = authRepository.snsLogin(token)
        if (result is ApiResult.Success) {
            tokenRepository.setTokens(
                result.data.accessToken,
                result.data.refreshToken
            )
            tokenRepository.setProvider(LoginProvider.GOOGLE)
        }
        return result
    }
}
