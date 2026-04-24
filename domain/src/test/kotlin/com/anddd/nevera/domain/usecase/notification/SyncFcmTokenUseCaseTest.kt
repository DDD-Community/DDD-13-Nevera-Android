package com.anddd.nevera.domain.usecase.notification

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.notification.FcmTokenError
import com.anddd.nevera.domain.repository.FcmTokenProvider
import com.anddd.nevera.domain.testutil.FakeFcmTokenRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.coroutines.cancellation.CancellationException

class SyncFcmTokenUseCaseTest {

    @Test
    fun `needsSync가 false이고 저장된 토큰이 없으면 fallback 토큰을 저장하고 동기화한다`() = runTest {
        val repository = FakeFcmTokenRepository(storedToken = null, syncNeeded = false)
        val useCase = SyncFcmTokenUseCase(repository, FakeFcmTokenProvider("fallback-token"))

        val result = useCase()

        assertEquals(NeveraResult.Success(Unit), result)
        assertEquals("fallback-token", repository.storedToken)
        assertEquals(listOf("fallback-token"), repository.markedTokens)
        assertEquals(listOf("fallback-token"), repository.registeredTokens)
        assertFalse(repository.syncNeeded)
    }

    @Test
    fun `needsSync가 true이고 저장된 토큰이 없으면 fallback 토큰으로 복구 후 동기화한다`() = runTest {
        val repository = FakeFcmTokenRepository(storedToken = null, syncNeeded = true)
        val useCase = SyncFcmTokenUseCase(repository, FakeFcmTokenProvider("restored-token"))

        val result = useCase()

        assertEquals(NeveraResult.Success(Unit), result)
        assertEquals("restored-token", repository.storedToken)
        assertEquals(listOf("restored-token"), repository.markedTokens)
        assertEquals(listOf("restored-token"), repository.registeredTokens)
        assertFalse(repository.syncNeeded)
    }

    @Test
    fun `needsSync가 true이고 저장된 토큰이 있으면 저장된 토큰으로 동기화한다`() = runTest {
        val repository = FakeFcmTokenRepository(storedToken = "cached-token", syncNeeded = true)
        val useCase = SyncFcmTokenUseCase(repository, FakeFcmTokenProvider("unused-token"))

        val result = useCase()

        assertEquals(NeveraResult.Success(Unit), result)
        assertEquals("cached-token", repository.storedToken)
        assertTrue(repository.markedTokens.isEmpty())
        assertEquals(listOf("cached-token"), repository.registeredTokens)
        assertFalse(repository.syncNeeded)
    }

    @Test
    fun `needsSync가 false이고 저장된 토큰이 있으면 아무 작업도 하지 않는다`() = runTest {
        val repository = FakeFcmTokenRepository(storedToken = "cached-token", syncNeeded = false)
        val useCase = SyncFcmTokenUseCase(repository, FakeFcmTokenProvider("unused-token"))

        val result = useCase()

        assertEquals(NeveraResult.Success(Unit), result)
        assertEquals("cached-token", repository.storedToken)
        assertTrue(repository.markedTokens.isEmpty())
        assertTrue(repository.registeredTokens.isEmpty())
        assertFalse(repository.syncNeeded)
    }

    @Test
    fun `fallback이 null을 반환하면 동기화하지 않는다`() = runTest {
        val repository = FakeFcmTokenRepository(storedToken = null, syncNeeded = true)
        val useCase = SyncFcmTokenUseCase(repository, FakeFcmTokenProvider(null))

        val result = useCase()

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
            registerResult = NeveraResult.Failure(FcmTokenError.Common(CommonError.Unknown))
        )
        val useCase = SyncFcmTokenUseCase(repository, FakeFcmTokenProvider("unused-token"))

        val result = useCase()

        assertEquals(NeveraResult.Failure(FcmTokenError.Common(CommonError.Unknown)), result)
        assertEquals(listOf("cached-token"), repository.registeredTokens)
        assertTrue(repository.syncNeeded)
    }

    @Test
    fun `provider가 예외를 던지면 동기화하지 않고 Success를 반환한다`() = runTest {
        val repository = FakeFcmTokenRepository(storedToken = null, syncNeeded = false)
        val useCase = SyncFcmTokenUseCase(repository, ThrowingFcmTokenProvider(RuntimeException("firebase error")))

        val result = useCase()

        assertEquals(NeveraResult.Success(Unit), result)
        assertNull(repository.storedToken)
        assertTrue(repository.markedTokens.isEmpty())
        assertTrue(repository.registeredTokens.isEmpty())
    }

    @Test
    fun `provider가 CancellationException을 던지면 그대로 전파한다`() = runTest {
        val repository = FakeFcmTokenRepository(storedToken = null, syncNeeded = false)
        val useCase = SyncFcmTokenUseCase(repository, ThrowingFcmTokenProvider(CancellationException("cancelled")))

        var thrown: Throwable? = null
        try {
            useCase()
        } catch (ce: CancellationException) {
            thrown = ce
        }

        assertEquals("cancelled", thrown?.message)
    }
}

private class FakeFcmTokenProvider(private val token: String?) : FcmTokenProvider {
    override suspend fun getToken(): String? = token
}

private class ThrowingFcmTokenProvider(private val exception: Throwable) : FcmTokenProvider {
    override suspend fun getToken(): String? = throw exception
}