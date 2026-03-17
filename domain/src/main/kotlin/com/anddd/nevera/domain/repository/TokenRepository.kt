package com.anddd.nevera.domain.repository

interface TokenRepository {
    suspend fun saveSession(token: String, userId: String)
    suspend fun getSession(): Pair<String?, String?>
    suspend fun getToken(): String?
    suspend fun getUserId(): String?
    suspend fun clearSession()
}
