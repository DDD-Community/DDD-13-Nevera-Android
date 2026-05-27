package com.anddd.nevera.data.datasource

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.auth.MessageResponse
import com.anddd.nevera.data.model.auth.TokenResponse

internal interface AuthRemoteDataSource {
    suspend fun loginWithEmail(email: String, password: String): ApiResponse<TokenResponse>
    suspend fun signup(
        email: String,
        password: String,
    ): ApiResponse<MessageResponse>
    suspend fun loginWithGoogle(idToken: String): ApiResponse<TokenResponse>
    suspend fun emailRequest(email: String): ApiResponse<MessageResponse>
    suspend fun emailVerify(email: String, authCode: String): ApiResponse<MessageResponse>
    suspend fun logout(): ApiResponse<MessageResponse>
    suspend fun withdraw(): ApiResponse<MessageResponse>
}
