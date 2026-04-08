package com.anddd.nevera.domain.usecase

import com.anddd.nevera.domain.repository.GoogleLoginRepository
import javax.inject.Inject

class GoogleLoginUseCase @Inject constructor(
    private val googleLoginRepository: GoogleLoginRepository,
) {

    suspend operator fun invoke(): Result<String> {
        return try {
            Result.success(googleLoginRepository.googleLogin())
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}
