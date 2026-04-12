package com.anddd.nevera.domain.usecase.auth

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.domain.model.common.MessageResult
import com.anddd.nevera.domain.repository.TokenRepository
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
) {

    suspend operator fun invoke(): NeveraResult<MessageResult, NetworkError> {
        return userRepository.logout()
            .onSuccess { tokenRepository.clearLoginInfo() }
    }
}
