package com.anddd.nevera.domain.usecase.user

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.user.Profile
import com.anddd.nevera.domain.model.user.ProfileError
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): NeveraResult<Profile, ProfileError> {
        return userRepository.getProfile()
    }
}
