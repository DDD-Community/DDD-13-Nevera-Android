package com.anddd.nevera.domain.usecase.auth

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.auth.SignupError
import com.anddd.nevera.domain.model.common.MessageResult
import com.anddd.nevera.domain.repository.AuthRepository
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String,
    ): NeveraResult<MessageResult, SignupError> {
        return authRepository.signup(email, password)
    }
}
