package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.UserApi
import com.anddd.nevera.data.model.user.OnboardingStatusResponse
import com.anddd.nevera.data.model.user.ProfileResponse
import com.anddd.nevera.data.model.user.UpdateNicknameRequest
import javax.inject.Inject

internal class UserRemoteDataSourceImpl @Inject constructor(
    private val userApi: UserApi,
) : UserRemoteDataSource {

    override suspend fun getProfile(): ApiResponse<ProfileResponse> {
        return userApi.getProfile()
    }

    override suspend fun updateNickname(nickname: String): ApiResponse<ProfileResponse> {
        return userApi.updateNickname(UpdateNicknameRequest(nickname))
    }

    override suspend fun getOnboardingStatus(): ApiResponse<OnboardingStatusResponse> {
        return userApi.getOnboardingStatus()
    }
}
