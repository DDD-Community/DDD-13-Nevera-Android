package com.anddd.nevera.data.api

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.auth.TokenResponse
import com.anddd.nevera.data.model.auth.EmailRequest
import com.anddd.nevera.data.model.auth.EmailVerifyRequest
import com.anddd.nevera.data.model.auth.LoginRequest
import com.anddd.nevera.data.model.auth.RefreshRequest
import com.anddd.nevera.data.model.auth.SignupRequest
import com.anddd.nevera.data.model.auth.MessageResponse
import com.anddd.nevera.data.model.auth.SnsLoginRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

internal interface UserApi {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<TokenResponse>

    @POST("api/v1/auth/signup")
    suspend fun signup(@Body request: SignupRequest): ApiResponse<MessageResponse>

    @POST("api/v1/auth/google")
    suspend fun googleLogin(@Body request: SnsLoginRequest): ApiResponse<TokenResponse>

    @POST("api/v1/auth/email-request")
    suspend fun emailRequest(@Body request: EmailRequest): ApiResponse<MessageResponse>

    @POST("api/v1/auth/email-verify")
    suspend fun emailVerify(@Body request: EmailVerifyRequest): ApiResponse<MessageResponse>

    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): ApiResponse<TokenResponse>

    @POST("api/v1/auth/logout")
    suspend fun logout(): ApiResponse<MessageResponse>

    @DELETE("api/v1/auth/withdraw")
    suspend fun withdraw(): ApiResponse<MessageResponse>
}
