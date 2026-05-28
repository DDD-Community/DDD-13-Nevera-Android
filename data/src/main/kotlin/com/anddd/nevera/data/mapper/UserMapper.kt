package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.auth.MessageResponse
import com.anddd.nevera.data.model.auth.TokenResponse
import com.anddd.nevera.data.model.user.OnboardingStatusResponse
import com.anddd.nevera.data.model.user.ProfileResponse
import com.anddd.nevera.domain.model.auth.LoginResult
import com.anddd.nevera.domain.model.common.MessageResult
import com.anddd.nevera.domain.model.user.OnboardingStatus
import com.anddd.nevera.domain.model.user.Profile

internal fun TokenResponse.toDomain(): LoginResult = LoginResult(
    accessToken = accessToken,
    refreshToken = refreshToken
)

internal fun MessageResponse.toDomain(): MessageResult = MessageResult(
    message = message
)

internal fun ProfileResponse.toDomain(): Profile = Profile(
    profileImageUrl = profileImageUrl,
    nickname = nickname,
    email = email,
    hasWish = hasWish,
)

internal fun OnboardingStatusResponse.toDomain(): OnboardingStatus = OnboardingStatus(
    isCompleteOnboarding = changedNickname,
)