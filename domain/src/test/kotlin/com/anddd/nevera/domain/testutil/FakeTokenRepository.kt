package com.anddd.nevera.domain.testutil

import com.anddd.nevera.domain.model.auth.LoginProvider
import com.anddd.nevera.domain.repository.TokenRepository

class FakeTokenRepository(
    private var accessToken: String?,
) : TokenRepository {

    override suspend fun getAccessToken(): String? = accessToken

    override suspend fun setAccessToken(accessToken: String) {
        this.accessToken = accessToken
    }

    override suspend fun getRefreshToken(): String? = null

    override suspend fun setRefreshToken(refreshToken: String) = Unit

    override suspend fun setTokens(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
    }

    override suspend fun getProvider(): LoginProvider? = null

    override suspend fun setProvider(provider: LoginProvider) = Unit

    override suspend fun setLoginInfo(
        accessToken: String,
        refreshToken: String,
        provider: LoginProvider,
    ) {
        this.accessToken = accessToken
    }

    override suspend fun clearLoginInfo() {
        accessToken = null
    }
}
