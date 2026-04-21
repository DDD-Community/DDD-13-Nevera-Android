package com.anddd.nevera.domain.usecase.notification

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.auth.LoginProvider
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.notification.FcmTokenError
import com.anddd.nevera.domain.repository.TokenRepository
import com.anddd.nevera.domain.testutil.FakeFcmTokenRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UpdateFcmTokenUseCaseTest {

    @Test
    fun `저장된 토큰과 동일하면 아무 작업도 하지 않고 성공을 반환한다`() = runTest {
        val fcmTokenRepository = FakeFcmTokenRepository(
            storedToken = "same-token",
            syncNeeded = false,
        )
        val tokenRepository = FakeTokenRepository(accessToken = "access-token")
        val useCase = UpdateFcmTokenUseCase(fcmTokenRepository, tokenRepository)

        val result = useCase("same-token")

        assertEquals(NeveraResult.Success(Unit), result)
        assertTrue(fcmTokenRepository.markedTokens.isEmpty())
        assertTrue(fcmTokenRepository.registeredTokens.isEmpty())
        assertFalse(fcmTokenRepository.syncNeeded)
    }

    @Test
    fun `미로그인 상태면 동기화 대상으로 표시만 하고 성공을 반환한다`() = runTest {
        val fcmTokenRepository = FakeFcmTokenRepository(
            storedToken = "old-token",
            syncNeeded = false,
        )
        val tokenRepository = FakeTokenRepository(accessToken = null)
        val useCase = UpdateFcmTokenUseCase(fcmTokenRepository, tokenRepository)

        val result = useCase("new-token")

        assertEquals(NeveraResult.Success(Unit), result)
        assertEquals(listOf("new-token"), fcmTokenRepository.markedTokens)
        assertTrue(fcmTokenRepository.registeredTokens.isEmpty())
        assertTrue(fcmTokenRepository.syncNeeded)
    }

    @Test
    fun `로그인 상태에서 등록 성공 시 needsSync를 해제하고 성공을 반환한다`() = runTest {
        val fcmTokenRepository = FakeFcmTokenRepository(
            storedToken = "old-token",
            syncNeeded = false,
        )
        val tokenRepository = FakeTokenRepository(accessToken = "access-token")
        val useCase = UpdateFcmTokenUseCase(fcmTokenRepository, tokenRepository)

        val result = useCase("new-token")

        assertEquals(NeveraResult.Success(Unit), result)
        assertEquals(listOf("new-token"), fcmTokenRepository.markedTokens)
        assertEquals(listOf("new-token"), fcmTokenRepository.registeredTokens)
        assertFalse(fcmTokenRepository.syncNeeded)
    }

    @Test
    fun `로그인 상태에서 등록 실패 시 실패를 반환하고 needsSync를 유지한다`() = runTest {
        val error = FcmTokenError.Common(CommonError.Unknown)
        val fcmTokenRepository = FakeFcmTokenRepository(
            storedToken = "old-token",
            syncNeeded = false,
            registerResult = NeveraResult.Failure(error),
        )
        val tokenRepository = FakeTokenRepository(accessToken = "access-token")
        val useCase = UpdateFcmTokenUseCase(fcmTokenRepository, tokenRepository)

        val result = useCase("new-token")

        assertEquals(NeveraResult.Failure(error), result)
        assertEquals(listOf("new-token"), fcmTokenRepository.markedTokens)
        assertEquals(listOf("new-token"), fcmTokenRepository.registeredTokens)
        assertTrue(fcmTokenRepository.syncNeeded)
    }
}

private class FakeTokenRepository(
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
