package com.anddd.nevera.data.datasource

import com.anddd.nevera.domain.model.auth.LoginProvider

internal interface TokenDataSource {
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
