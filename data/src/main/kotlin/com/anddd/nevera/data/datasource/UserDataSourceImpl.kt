package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.di.AuthOkHttpClient
import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.api.UserApi
import com.anddd.nevera.data.model.auth.EmailRequest
import com.anddd.nevera.data.model.auth.TokenResponse
import com.anddd.nevera.data.model.auth.EmailVerifyRequest
import com.anddd.nevera.data.model.auth.LoginRequest
import com.anddd.nevera.data.model.auth.SignupRequest
import com.anddd.nevera.data.model.auth.SnsLoginRequest
import javax.inject.Inject

internal class UserDataSourceImpl @Inject constructor(
    @param:AuthOkHttpClient private val userApi: UserApi
) : UserDataSource {

    override suspend fun login(
        email: String,
        password: String
    ): ApiResponse<TokenResponse> {
        val request = LoginRequest(email, password)
        return userApi.login(request)
    }

    override suspend fun signup(
        email: String,
        password: String,
        passwordCheck: String,
        name: String,
        passwordMatch: Boolean
    ): ApiResponse<String> {
        val request = SignupRequest(
            email,
            password,
            passwordCheck,
            name,
            passwordMatch
        )
        return userApi.signup(request)
    }

    override suspend fun snsLogin(idToken: String): ApiResponse<TokenResponse> {
        val request = SnsLoginRequest(idToken)
        return userApi.googleLogin(request)
    }

    override suspend fun emailRequest(email: String): ApiResponse<String> {
        val request = EmailRequest(email)
        return userApi.emailRequest(request)
    }

    override suspend fun emailVerify(
        email: String,
        authCode: String
    ): ApiResponse<String> {
        val request = EmailVerifyRequest(email, authCode)
        return userApi.emailVerify(request)
    }

    override suspend fun logout(): ApiResponse<String> {
        return userApi.logout()
    }

    override suspend fun withdraw(): ApiResponse<String> {
        return userApi.withdraw()
    }
}