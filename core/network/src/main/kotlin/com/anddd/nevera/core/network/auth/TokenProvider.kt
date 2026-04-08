package com.anddd.nevera.core.network.auth

/**
 * core:network 레이어에서 토큰에 접근하기 위한 포트 인터페이스.
 *
 * core:network 모듈은 domain 모듈에 의존할 수 없기 때문에 (순환 의존성),
 * 이 인터페이스를 경계로 정의하고 data 레이어에서 [TokenRepository]를 통해 구현한다.
 * (Ports & Adapters 패턴 — 구현체: TokenProviderAdapter)
 */
interface TokenProvider {
    suspend fun getAccessToken(): String?
    suspend fun setAccessToken(accessToken: String)
    suspend fun getRefreshToken(): String?
    suspend fun setRefreshToken(refreshToken: String)
    suspend fun setTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
}
