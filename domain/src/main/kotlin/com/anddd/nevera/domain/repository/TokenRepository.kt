package com.anddd.nevera.domain.repository

interface TokenRepository {
    suspend fun saveSession(accessToken: String, refreshToken: String, userId: String)
    suspend fun getSession(): Triple<String?, String?, String?>
    suspend fun getToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun getUserId(): String?
    suspend fun clearSession()
}
