package com.anddd.nevera.domain.usecase

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.model.LoginResult
import com.anddd.nevera.domain.model.LoginProvider
import com.anddd.nevera.domain.repository.UserRepository
import com.anddd.nevera.domain.repository.TokenRepository
import javax.inject.Inject

class GoogleLoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) {

    suspend operator fun invoke(token: String): ApiResult<LoginResult> {
        val result = userRepository.loginWithGoogle(token)
        if (result is ApiResult.Success) {
            tokenRepository.setLoginInfo(
                accessToken = result.data.accessToken,
                refreshToken = result.data.refreshToken,
                provider = LoginProvider.GOOGLE
            )
        }
        return result
    }
}
