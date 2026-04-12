package com.anddd.nevera.domain.usecase.email

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.MessageResult
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

class EmailVerifyUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(
        email: String,
        authCode: String
    ): NeveraResult<MessageResult, NetworkError> {
        return userRepository.emailVerify(email, authCode)
    }
}
