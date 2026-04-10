package com.anddd.nevera.domain.usecase

import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.model.LoginResult
import com.anddd.nevera.domain.repository.GoogleLoginRepository
import javax.inject.Inject

class GoogleLoginUseCase @Inject constructor(
    private val googleLoginRepository: GoogleLoginRepository,
) {

    suspend operator fun invoke(): ApiResult<Unit> {
        return googleLoginRepository.googleLogin()
    }
}
