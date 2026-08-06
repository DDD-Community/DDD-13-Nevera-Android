package com.anddd.nevera.domain.testutil

import com.anddd.nevera.domain.model.auth.LoginProvider
import com.anddd.nevera.domain.repository.TokenRepository

/**
 * [TokenRepository]의 테스트용 구현.
 *
 * 저장된 값을 실제로 들고 있으므로 "저장한 뒤 읽으면 그 값이 나온다"까지 그대로 재현한다.
 * [loginInfoCalls]는 로그인 성공 시에만 토큰이 저장되는지 검증하는 데 쓴다.
 */
class FakeTokenRepository(
    var accessToken: String? = null,
    var refreshToken: String? = null,
    var provider: LoginProvider? = null,
) : TokenRepository {

    data class LoginInfo(
        val accessToken: String,
        val refreshToken: String,
        val provider: LoginProvider,
    )

    val loginInfoCalls = mutableListOf<LoginInfo>()
    var clearLoginInfoCount: Int = 0
        private set

    override suspend fun getAccessToken(): String? = accessToken

    override suspend fun setAccessToken(accessToken: String) {
        this.accessToken = accessToken
    }

    override suspend fun getRefreshToken(): String? = refreshToken

    override suspend fun setRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }

    override suspend fun setTokens(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    override suspend fun getProvider(): LoginProvider? = provider

    override suspend fun setProvider(provider: LoginProvider) {
        this.provider = provider
    }

    override suspend fun setLoginInfo(
        accessToken: String,
        refreshToken: String,
        provider: LoginProvider,
    ) {
        loginInfoCalls += LoginInfo(accessToken, refreshToken, provider)
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.provider = provider
    }

    override suspend fun clearLoginInfo() {
        clearLoginInfoCount++
        accessToken = null
        refreshToken = null
        provider = null
    }
}
