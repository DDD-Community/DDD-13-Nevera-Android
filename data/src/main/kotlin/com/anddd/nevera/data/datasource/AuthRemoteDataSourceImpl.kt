package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.AuthApi
import com.anddd.nevera.data.model.auth.EmailRequest
import com.anddd.nevera.data.model.auth.EmailVerifyRequest
import com.anddd.nevera.data.model.auth.LoginRequest
import com.anddd.nevera.data.model.auth.MessageResponse
import com.anddd.nevera.data.model.auth.SignupRequest
import com.anddd.nevera.data.model.auth.SnsLoginRequest
import com.anddd.nevera.data.model.auth.TokenResponse
import javax.inject.Inject

internal class AuthRemoteDataSourceImpl @Inject constructor(
    private val authApi: AuthApi,
) : AuthRemoteDataSource {

    override suspend fun loginWithEmail(
        email: String,
        password: String,
    ): ApiResponse<TokenResponse> {
        val request = LoginRequest(email, password)
        return authApi.login(request)
    }

    override suspend fun signup(
        email: String,
        password: String,
    ): ApiResponse<MessageResponse> {
        val request = SignupRequest(email, password)
        return authApi.signup(request)
    }

    override suspend fun loginWithGoogle(idToken: String): ApiResponse<TokenResponse> {
        val request = SnsLoginRequest(idToken)
        return authApi.googleLogin(request)
    }

    override suspend fun emailRequest(email: String): ApiResponse<MessageResponse> {
        val request = EmailRequest(email)
        return authApi.emailRequest(request)
    }

    override suspend fun emailVerify(
        email: String,
        authCode: String,
    ): ApiResponse<MessageResponse> {
        val request = EmailVerifyRequest(email, authCode)
        return authApi.emailVerify(request)
    }

    override suspend fun logout(): ApiResponse<MessageResponse> {
        return authApi.logout()
    }

    override suspend fun withdraw(): ApiResponse<MessageResponse> {
        return authApi.withdraw()
    }
}
