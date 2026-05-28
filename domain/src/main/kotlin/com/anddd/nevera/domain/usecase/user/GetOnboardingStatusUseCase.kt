package com.anddd.nevera.domain.usecase.user

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.user.OnboardingStatus
import com.anddd.nevera.domain.model.user.OnboardingStatusError
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

class GetOnboardingStatusUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): NeveraResult<OnboardingStatus, OnboardingStatusError> {
        return userRepository.getOnboardingStatus()
    }
}
