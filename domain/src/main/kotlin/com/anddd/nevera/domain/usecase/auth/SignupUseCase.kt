package com.anddd.nevera.domain.usecase.auth

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.MessageResult
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String,
        name: String
    ): NeveraResult<MessageResult, NetworkError> {
        return userRepository.signup(email, password, name)
    }
}
