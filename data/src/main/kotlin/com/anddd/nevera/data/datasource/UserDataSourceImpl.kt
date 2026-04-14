package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.UserApi
import com.anddd.nevera.data.model.auth.EmailRequest
import com.anddd.nevera.data.model.auth.MessageResponse
import com.anddd.nevera.data.model.auth.TokenResponse
import com.anddd.nevera.data.model.auth.EmailVerifyRequest
import com.anddd.nevera.data.model.auth.LoginRequest
import com.anddd.nevera.data.model.auth.SignupRequest
import com.anddd.nevera.data.model.auth.SnsLoginRequest
import javax.inject.Inject

internal class UserDataSourceImpl @Inject constructor(
    private val userApi: UserApi
) : UserDataSource {

    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): ApiResponse<TokenResponse> {
        val request = LoginRequest(email, password)
        return userApi.login(request)
    }

    override suspend fun signup(
        email: String,
        password: String,
        name: String
    ): ApiResponse<MessageResponse> {
        val request = SignupRequest(email, password, name)
        return userApi.signup(request)
    }

    override suspend fun loginWithGoogle(idToken: String): ApiResponse<TokenResponse> {
        val request = SnsLoginRequest(idToken)
        return userApi.googleLogin(request)
    }

    override suspend fun emailRequest(email: String): ApiResponse<MessageResponse> {
        val request = EmailRequest(email)
        return userApi.emailRequest(request)
    }

    override suspend fun emailVerify(
        email: String,
        authCode: String
    ): ApiResponse<MessageResponse> {
        val request = EmailVerifyRequest(email, authCode)
        return userApi.emailVerify(request)
    }

    override suspend fun logout(): ApiResponse<MessageResponse> {
        return userApi.logout()
    }

    override suspend fun withdraw(): ApiResponse<MessageResponse> {
        return userApi.withdraw()
    }
}