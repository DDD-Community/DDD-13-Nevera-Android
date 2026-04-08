package com.anddd.nevera.domain.repository

interface TokenRepository {
    suspend fun getAccessToken(): String?
    suspend fun setAccessToken(accessToken: String)
    suspend fun getRefreshToken(): String?
    suspend fun setRefreshToken(refreshToken: String)
    suspend fun setTokens(accessToken: String, refreshToken: String)
    suspend fun clearLoginData()
}
