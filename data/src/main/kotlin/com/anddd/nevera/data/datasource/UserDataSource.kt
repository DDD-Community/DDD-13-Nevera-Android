package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.auth.TokenResponse

internal interface UserDataSource {
    suspend fun loginWithEmail(email: String, password: String): ApiResponse<TokenResponse>
    suspend fun signup(
        email: String,
        password: String,
        name: String
    ): ApiResponse<String>
    suspend fun loginWithGoogle(idToken: String): ApiResponse<TokenResponse>
    suspend fun emailRequest(email: String): ApiResponse<String>
    suspend fun emailVerify(email: String, authCode: String): ApiResponse<String>
    suspend fun logout(): ApiResponse<String>
    suspend fun withdraw(): ApiResponse<String>
}
