package com.anddd.nevera.domain.usecase.auth

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.mapSuccess
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.domain.model.auth.GoogleLoginError
import com.anddd.nevera.domain.model.auth.LoginProvider
import com.anddd.nevera.domain.repository.TokenRepository
import com.anddd.nevera.domain.repository.AuthRepository
import javax.inject.Inject

class GoogleLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository
) {

    suspend operator fun invoke(token: String): NeveraResult<Unit, GoogleLoginError> {
        return authRepository.loginWithGoogle(token)
            .onSuccess { result ->
                tokenRepository.setLoginInfo(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                    provider = LoginProvider.GOOGLE
                )
            }.mapSuccess { Unit }
    }
}
