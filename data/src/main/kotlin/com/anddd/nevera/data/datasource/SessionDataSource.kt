package com.anddd.nevera.data.datasource

internal interface SessionDataSource {
    suspend fun getAccessToken(): String?
    suspend fun setAccessToken(accessToken: String)
    suspend fun getRefreshToken(): String?
    suspend fun setRefreshToken(refreshToken: String)
    suspend fun setTokens(accessToken: String, refreshToken: String)
    suspend fun clearLoginData()
}
