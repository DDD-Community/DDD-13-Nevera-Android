package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.user.OnboardingStatus
import com.anddd.nevera.domain.model.user.OnboardingStatusError
import com.anddd.nevera.domain.model.user.Profile
import com.anddd.nevera.domain.model.user.ProfileError
import com.anddd.nevera.domain.model.user.UpdateNicknameError

interface UserRepository {
    suspend fun getProfile(): NeveraResult<Profile, ProfileError>
    suspend fun updateNickname(nickname: String): NeveraResult<Profile, UpdateNicknameError>
    suspend fun getOnboardingStatus(): NeveraResult<OnboardingStatus, OnboardingStatusError>
}
