package com.anddd.nevera.data.api

import com.anddd.nevera.core.common.ApiResponse
import com.anddd.nevera.data.model.LoginRequest
import com.anddd.nevera.data.model.LoginResponse
import com.anddd.nevera.data.model.SnsLoginRequest
import retrofit2.http.Body
import retrofit2.http.POST

internal interface UserApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("auth/sns-login")
    suspend fun snsLogin(@Body request: SnsLoginRequest): ApiResponse<LoginResponse>
}
