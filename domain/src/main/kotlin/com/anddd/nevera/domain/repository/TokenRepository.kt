package com.anddd.nevera.domain.repository

import com.anddd.nevera.domain.model.LoginProvider

interface TokenRepository {
    suspend fun getAccessToken(): String?
    suspend fun setAccessToken(accessToken: String)
    suspend fun getRefreshToken(): String?
    suspend fun setRefreshToken(refreshToken: String)
    suspend fun setTokens(accessToken: String, refreshToken: String)
    suspend fun getProvider(): LoginProvider?
    suspend fun setProvider(provider: LoginProvider)
    suspend fun setLoginInfo(
        accessToken: String,
        refreshToken: String,
        provider: LoginProvider
    )
    suspend fun clearLoginInfo()
}
