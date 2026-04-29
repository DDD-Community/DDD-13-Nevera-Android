package com.anddd.nevera.infra.notification.testutil

import com.anddd.nevera.domain.model.auth.LoginProvider
import com.anddd.nevera.domain.repository.TokenRepository

class FakeTokenRepository(accessToken: String? = null) : TokenRepository {

    private var _accessToken: String? = accessToken
    private var _refreshToken: String? = null
    private var _provider: LoginProvider? = null

    override suspend fun getAccessToken(): String? = _accessToken
    override suspend fun setAccessToken(accessToken: String) { _accessToken = accessToken }
    override suspend fun getRefreshToken(): String? = _refreshToken
    override suspend fun setRefreshToken(refreshToken: String) { _refreshToken = refreshToken }
    override suspend fun setTokens(accessToken: String, refreshToken: String) {
        _accessToken = accessToken
        _refreshToken = refreshToken
    }
    override suspend fun getProvider(): LoginProvider? = _provider
    override suspend fun setProvider(provider: LoginProvider) { _provider = provider }
    override suspend fun setLoginInfo(accessToken: String, refreshToken: String, provider: LoginProvider) {
        _accessToken = accessToken
        _refreshToken = refreshToken
        _provider = provider
    }
    override suspend fun clearLoginInfo() {
        _accessToken = null
        _refreshToken = null
        _provider = null
    }
}
