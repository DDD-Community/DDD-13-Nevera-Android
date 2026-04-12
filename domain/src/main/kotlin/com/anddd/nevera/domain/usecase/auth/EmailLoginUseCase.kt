package com.anddd.nevera.domain.usecase.auth

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.auth.LoginError
import com.anddd.nevera.domain.model.auth.LoginProvider
import com.anddd.nevera.domain.repository.TokenRepository
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

/**
 * 이메일 로그인을 수행하고, 성공 시 토큰을 로컬에 저장한다.
 *
 * 로그인과 토큰 저장은 원자적으로 처리되어야 하는 하나의 비즈니스 시나리오이므로
 * 이 UseCase에서 두 Repository를 함께 orchestrate한다.
 */
class EmailLoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
) {

    suspend operator fun invoke(email: String, password: String): NeveraResult<Unit, LoginError> {
        return when (val result = userRepository.loginWithEmail(email, password)) {
            is NeveraResult.Success -> {
                tokenRepository.setLoginInfo(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    provider = LoginProvider.EMAIL
                )
                NeveraResult.Success(Unit)
            }
            is NeveraResult.Failure -> result
        }
    }
}
