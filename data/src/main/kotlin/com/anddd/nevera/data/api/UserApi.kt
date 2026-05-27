package com.anddd.nevera.data.api

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.user.ProfileResponse
import retrofit2.http.GET

internal interface UserApi {

    @GET("api/v1/mypage/me/profile")
    suspend fun getProfile(): ApiResponse<ProfileResponse>
}
