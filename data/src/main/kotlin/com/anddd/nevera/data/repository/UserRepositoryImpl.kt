package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.common.map
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.data.datasource.UserRemoteDataSource
import com.anddd.nevera.data.mapper.error.toOnboardingStatusError
import com.anddd.nevera.data.mapper.error.toProfileError
import com.anddd.nevera.data.mapper.error.toUpdateNicknameError
import com.anddd.nevera.data.mapper.toDomain
import com.anddd.nevera.domain.model.user.OnboardingStatus
import com.anddd.nevera.domain.model.user.OnboardingStatusError
import com.anddd.nevera.domain.model.user.Profile
import com.anddd.nevera.domain.model.user.ProfileError
import com.anddd.nevera.domain.model.user.UpdateNicknameError
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val apiCall: ApiCallExecutor,
) : UserRepository {

    override suspend fun getProfile(): NeveraResult<Profile, ProfileError> {
        return apiCall {
            userRemoteDataSource.getProfile()
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toProfileError() },
        )
    }

    override suspend fun updateNickname(nickname: String): NeveraResult<Profile, UpdateNicknameError> {
        return apiCall {
            userRemoteDataSource.updateNickname(nickname)
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toUpdateNicknameError() },
        )
    }

    override suspend fun getOnboardingStatus(): NeveraResult<OnboardingStatus, OnboardingStatusError> {
        return apiCall {
            userRemoteDataSource.getOnboardingStatus()
        }.map(
            transformSuccess = { it.toDomain() },
            transformFailure = { it.toOnboardingStatusError() },
        )
    }
}
