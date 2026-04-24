package com.anddd.nevera.data.repository

import com.anddd.nevera.data.datasource.TokenDataSource
import com.anddd.nevera.domain.model.auth.LoginProvider
import com.anddd.nevera.domain.repository.TokenRepository
import javax.inject.Inject

internal class TokenRepositoryImpl @Inject constructor(
    private val tokenDataSource: TokenDataSource,
) : TokenRepository {

    override suspend fun getAccessToken(): String? =
        tokenDataSource.getAccessToken()

    override suspend fun setAccessToken(accessToken: String) =
        tokenDataSource.setAccessToken(accessToken)

    override suspend fun getRefreshToken(): String? =
        tokenDataSource.getRefreshToken()

    override suspend fun setRefreshToken(refreshToken: String) =
        tokenDataSource.setRefreshToken(refreshToken)

    override suspend fun setTokens(accessToken: String, refreshToken: String) {
        tokenDataSource.setTokens(accessToken, refreshToken)
    }

    override suspend fun getProvider(): LoginProvider? {
        return tokenDataSource.getProvider()
    }

    override suspend fun setProvider(provider: LoginProvider) {
        tokenDataSource.setProvider(provider)
    }

    override suspend fun setLoginInfo(
        accessToken: String,
        refreshToken: String,
        provider: LoginProvider
    ) {
        tokenDataSource.setLoginInfo(accessToken, refreshToken, provider)
    }

    override suspend fun clearLoginInfo() = tokenDataSource.clearLoginInfo()
}
