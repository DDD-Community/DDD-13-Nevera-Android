package com.anddd.nevera.domain.usecase

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.repository.TokenRepository
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
) {
    suspend operator fun invoke(): ApiResult<Unit> {
        val result = userRepository.logout()
        if (result is ApiResult.Success) {
            tokenRepository.clearLoginInfo()
        }
        return result
    }
}
