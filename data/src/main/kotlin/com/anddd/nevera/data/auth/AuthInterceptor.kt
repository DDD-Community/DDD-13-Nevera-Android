package com.anddd.nevera.data.auth

import com.anddd.nevera.core.network.auth.SessionEventBus
import com.anddd.nevera.core.network.auth.TokenProvider
import com.anddd.nevera.data.api.AuthApi
import com.anddd.nevera.data.api.RefreshRequest
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * API 요청에 인증 헤더를 자동으로 주입하고, 토큰 만료 시 갱신을 처리하는 OkHttp 인터셉터.
 *
 * 동작 흐름:
 * 1. 모든 요청에 "Authorization: Bearer {accessToken}" 헤더 추가
 * 2. 일반 API 요청에서 401 응답 수신 시 refresh token으로 토큰 갱신 시도
 * 3. 갱신 성공 → 새 토큰으로 원래 요청 재시도
 * 4. refresh API가 401을 반환하면 세션 만료로 간주하고 토큰을 삭제한 뒤 세션 만료 이벤트를 발행
 * 5. 그 외 갱신 실패는 예외를 그대로 전파 (apiCall이 변환)
 *
 * [TokenProvider]와 [AuthApi]는 Dagger [Lazy]로 주입한다.
 * OkHttpClient 생성 시점에 이 인터셉터가 먼저 만들어지면서 아직 완성되지 않은
 * 다른 싱글톤을 참조하는 순환 의존성 문제를 방지하기 위함이다.
 */
internal class AuthInterceptor @Inject constructor(
    private val tokenProvider: Lazy<TokenProvider>,
    private val authApi: Lazy<AuthApi>,
    private val sessionEventBus: SessionEventBus
) : Interceptor {

    // 여러 요청이 동시에 401을 받을 때 refresh를 단 한 번만 수행하기 위한 Mutex
    private val refreshMutex = Mutex()

    // OkHttp Interceptor는 동기 API이므로, suspend 함수 호출을 위해 runBlocking을 사용한다.
    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenBefore = runBlocking { tokenProvider.get().getAccessToken() }
        val request = chain.request()
        val authedRequest = request.newBuilder()
            .apply { tokenBefore?.let { header("Authorization", "Bearer $it") } }
            .build()
        val response = chain.proceed(authedRequest)

        if (response.code != UNAUTHORIZED_STATUS_CODE) return response

        // 401 Unauthorized : 인증 필요 (토큰 없음·만료)
        return runBlocking {
            refreshMutex.withLock {
                val tokenAfterLock = tokenProvider.get().getAccessToken()

                // refresh API에서 401을 받은 첫 번째 요청만 세션 만료 처리.
                // clearTokens() 이후에는 accessToken도 null이 되므로,
                // 대기 중이던 후속 요청들은 이 분기를 타지 않는다.
                if (tokenAfterLock != null && request.url.isRefreshTokenRequest()) {
                    tokenProvider.get().clearTokens()
                    sessionEventBus.emitSessionExpired()
                    return@withLock response
                }

                if (tokenAfterLock != null && tokenAfterLock != tokenBefore) {
                    // response 불필요
                    response.close()

                    // Mutex 대기 중 다른 스레드가 이미 refresh를 완료한 경우 → 새 토큰으로 재시도
                    return@withLock chain.proceed(
                        request.newBuilder()
                            .header("Authorization", "Bearer $tokenAfterLock")
                            .build()
                    )
                }

                val refreshToken = tokenProvider.get().getRefreshToken()
                if (refreshToken != null) {
                    // response 불필요
                    response.close()

                    val newTokens = authApi.get().refresh(RefreshRequest(refreshToken))
                    tokenProvider.get().saveTokens(newTokens.accessToken, newTokens.refreshToken)

                    return@withLock chain.proceed(
                        request.newBuilder()
                            .header("Authorization", "Bearer ${newTokens.accessToken}")
                            .build()
                    )
                } else {
                    return@withLock response
                }
            }
        }
    }

    private fun HttpUrl.isRefreshTokenRequest(): Boolean {
        return encodedPath == REFRESH_TOKEN_PATH
    }

    companion object {
        internal const val UNAUTHORIZED_STATUS_CODE = 401
        private const val REFRESH_TOKEN_PATH = "/auth/refresh"
    }
}
