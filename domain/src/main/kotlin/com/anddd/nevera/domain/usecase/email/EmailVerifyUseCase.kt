package com.anddd.nevera.domain.usecase.email

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.auth.EmailVerifyError
import com.anddd.nevera.domain.model.common.MessageResult
import com.anddd.nevera.domain.repository.AuthRepository
import javax.inject.Inject

class EmailVerifyUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        email: String,
        authCode: String,
    ): NeveraResult<MessageResult, EmailVerifyError> {
        return authRepository.emailVerify(email, authCode)
    }
}
