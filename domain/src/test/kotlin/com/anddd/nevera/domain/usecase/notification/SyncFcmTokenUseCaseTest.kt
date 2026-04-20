package com.anddd.nevera.domain.usecase.notification

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.notification.FcmTokenError
import com.anddd.nevera.domain.repository.FcmTokenRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class
SyncFcmTokenUseCaseTest {

    @Test
    fun `needsSync가 false이고 저장된 토큰이 없으면 fallback 토큰을 저장하고 동기화한다`() = runTest {
        val repository = FakeFcmTokenRepository(
            storedToken = null,
            syncNeeded = false,
        )
        val useCase = SyncFcmTokenUseCase(repository)

        val result = useCase { "fallback-token" }

        assertEquals(NeveraResult.Success(Unit), result)
        assertEquals("fallback-token", repository.storedToken)
        assertEquals(listOf("fallback-token"), repository.markedTokens)
        assertEquals(listOf("fallback-token"), repository.registeredTokens)
        assertFalse(repository.syncNeeded)
    }

    @Test
    fun `needsSync가 true이고 저장된 토큰이 없으면 fallback 토큰으로 복구 후 동기화한다`() = runTest {
        val repository = FakeFcmTokenRepository(
            storedToken = null,
            syncNeeded = true,
        )
        val useCase = SyncFcmTokenUseCase(repository)

        val result = useCase { "restored-token" }

        assertEquals(NeveraResult.Success(Unit), result)
        assertEquals("restored-token", repository.storedToken)
        assertEquals(listOf("restored-token"), repository.markedTokens)
        assertEquals(listOf("restored-token"), repository.registeredTokens)
        assertFalse(repository.syncNeeded)
    }

    @Test
    fun `needsSync가 true이고 저장된 토큰이 있으면 저장된 토큰으로 동기화한다`() = runTest {
        val repository = FakeFcmTokenRepository(
            storedToken = "cached-token",
            syncNeeded = true,
        )
        val useCase = SyncFcmTokenUseCase(repository)

        val result = useCase { "unused-token" }

        assertEquals(NeveraResult.Success(Unit), result)
        assertEquals("cached-token", repository.storedToken)
        assertTrue(repository.markedTokens.isEmpty())
        assertEquals(listOf("cached-token"), repository.registeredTokens)
        assertFalse(repository.syncNeeded)
    }

    @Test
    fun `needsSync가 false이고 저장된 토큰이 있으면 아무 작업도 하지 않는다`() = runTest {
        val repository = FakeFcmTokenRepository(
            storedToken = "cached-token",
            syncNeeded = false,
        )
        val useCase = SyncFcmTokenUseCase(repository)

        val result = useCase { "unused-token" }

        assertEquals(NeveraResult.Success(Unit), result)
        assertEquals("cached-token", repository.storedToken)
        assertTrue(repository.markedTokens.isEmpty())
        assertTrue(repository.registeredTokens.isEmpty())
        assertFalse(repository.syncNeeded)
    }

    @Test
    fun `fallback이 null을 반환하면 동기화하지 않는다`() = runTest {
        val repository = FakeFcmTokenRepository(
            storedToken = null,
            syncNeeded = true,
        )
        val useCase = SyncFcmTokenUseCase(repository)

        val result = useCase { null }

        assertEquals(NeveraResult.Success(Unit), result)
        assertNull(repository.storedToken)
        assertTrue(repository.markedTokens.isEmpty())
        assertTrue(repository.registeredTokens.isEmpty())
        assertTrue(repository.syncNeeded)
    }

    @Test
    fun `서버 등록 실패 시 needsSync를 유지한다`() = runTest {
        val repository = FakeFcmTokenRepository(
            storedToken = "cached-token",
            syncNeeded = true,
            registerResult = NeveraResult.Failure(FcmTokenError.Common(com.anddd.nevera.domain.model.common.CommonError.Unknown))
        )
        val useCase = SyncFcmTokenUseCase(repository)

        val result = useCase { "unused-token" }

        assertEquals(NeveraResult.Failure(FcmTokenError.Common(com.anddd.nevera.domain.model.common.CommonError.Unknown)), result)
        assertEquals(listOf("cached-token"), repository.registeredTokens)
        assertTrue(repository.syncNeeded)
    }
}

private class FakeFcmTokenRepository(
    var storedToken: String?,
    var syncNeeded: Boolean,
    var registerResult: NeveraResult<Unit, FcmTokenError> = NeveraResult.Success(Unit),
) : FcmTokenRepository {

    val markedTokens = mutableListOf<String>()
    val registeredTokens = mutableListOf<String>()

    override suspend fun getFcmToken(): String? = storedToken

    override suspend fun saveFcmToken(token: String) {
        storedToken = token
    }

    override suspend fun markTokenForSync(token: String) {
        markedTokens += token
        storedToken = token
        syncNeeded = true
    }

    override suspend fun isSyncNeeded(): Boolean = syncNeeded

    override suspend fun setNeedsSync(value: Boolean) {
        syncNeeded = value
    }

    override suspend fun registerFcmToken(token: String): NeveraResult<Unit, FcmTokenError> {
        registeredTokens += token
        return registerResult
    }
}