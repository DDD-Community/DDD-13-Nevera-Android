package com.anddd.nevera.data.api

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.user.OnboardingStatusResponse
import com.anddd.nevera.data.model.user.ProfileResponse
import com.anddd.nevera.data.model.user.UpdateNicknameRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

internal interface UserApi {

    @GET("api/v1/mypage/me/profile")
    suspend fun getProfile(): ApiResponse<ProfileResponse>

    @PUT("api/v1/mypage/nickname")
    suspend fun updateNickname(@Body request: UpdateNicknameRequest): ApiResponse<ProfileResponse>

    @GET("api/v1/mypage/onboarding/complete")
    suspend fun getOnboardingStatus(): ApiResponse<OnboardingStatusResponse>
}
