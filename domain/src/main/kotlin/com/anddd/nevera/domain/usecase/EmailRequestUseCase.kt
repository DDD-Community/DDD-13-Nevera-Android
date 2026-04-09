package com.anddd.nevera.domain.usecase

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

class EmailRequestUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String): ApiResult<Unit> =
        userRepository.emailRequest(email)
}
