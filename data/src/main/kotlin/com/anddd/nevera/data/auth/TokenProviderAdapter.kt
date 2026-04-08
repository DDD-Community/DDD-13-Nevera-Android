package com.anddd.nevera.data.auth

import com.anddd.nevera.core.network.auth.TokenProvider
import com.anddd.nevera.domain.repository.TokenRepository
import javax.inject.Inject

/**
 * [TokenProvider] (core:network) 와 [TokenRepository] (domain) 를 연결하는 어댑터.
 *
 * core:network 는 domain 에 의존할 수 없으므로, data 레이어에서 두 인터페이스를 브릿지한다.
 * [AuthInterceptor]는 이 어댑터를 통해 domain 계층의 토큰 저장소에 간접적으로 접근한다.
 */
internal class TokenProviderAdapter @Inject constructor(
    private val tokenRepository: TokenRepository
) : TokenProvider {
    override suspend fun getAccessToken(): String? = tokenRepository.getAccessToken()

    override suspend fun setAccessToken(accessToken: String) {
        tokenRepository.setAccessToken(accessToken)
    }

    override suspend fun getRefreshToken(): String? = tokenRepository.getRefreshToken()

    override suspend fun setRefreshToken(refreshToken: String) {
        tokenRepository.setRefreshToken(refreshToken)
    }

    override suspend fun setTokens(accessToken: String, refreshToken: String) =
        tokenRepository.setTokens(accessToken, refreshToken)

    override suspend fun clearTokens() = tokenRepository.clearLoginData()
}
