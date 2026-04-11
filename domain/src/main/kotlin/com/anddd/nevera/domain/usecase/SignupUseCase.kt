package com.anddd.nevera.domain.usecase

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(email: String, password: String, name: String): ApiResult<Unit> {
        return userRepository.signup(email, password, name)
    }
}
