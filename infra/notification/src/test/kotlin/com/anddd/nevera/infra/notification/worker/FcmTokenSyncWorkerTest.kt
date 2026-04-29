package com.anddd.nevera.infra.notification.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.infra.notification.testutil.FakeFcmTokenRepository
import com.anddd.nevera.infra.notification.testutil.FakeTokenRepository
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.notification.FcmTokenError
import com.anddd.nevera.domain.repository.FcmTokenProvider
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.coroutines.cancellation.CancellationException

class FcmTokenSyncWorkerTest {

    private val context = mockk<Context>(relaxed = true)

    private fun createWorker(
        inputData: Data = Data.EMPTY,
        storedToken: String? = null,
        syncNeeded: Boolean = false,
        registerResult: NeveraResult<Unit, FcmTokenError> = NeveraResult.Success(Unit),
        fcmTokenProvider: FcmTokenProvider = FakeFcmTokenProvider(null),
        accessToken: String? = "access-token",
        runAttemptCount: Int = 0,
    ): Pair<FcmTokenSyncWorker, FakeFcmTokenRepository> {
        val fcmTokenRepository = FakeFcmTokenRepository(storedToken, syncNeeded, registerResult)
        val tokenRepository = FakeTokenRepository(accessToken)
        val workerParams = mockk<WorkerParameters>(relaxed = true) {
            every { this@mockk.inputData } returns inputData
            every { this@mockk.runAttemptCount } returns runAttemptCount
        }
        val worker = FcmTokenSyncWorker(
            context,
            workerParams,
            fcmTokenRepository,
            fcmTokenProvider,
            tokenRepository,
        )
        return worker to fcmTokenRepository
    }

    // ── Sync path (Splash / Login 호출, input token 없음) ──────────────────────

    @Test
    fun `로그인 상태이고 저장된 토큰이 있고 isSyncNeeded가 true이면 서버에 등록하고 Result_success를 반환한다`() = runTest {
        val (worker, repo) = createWorker(
            storedToken = "cached-token",
            syncNeeded = true,
        )

        val result = worker.doWork()

        assertEquals(Result.success(), result)
        assertEquals(listOf("cached-token"), repo.registeredTokens)
        assertTrue(repo.markedTokens.isEmpty())
        assertFalse(repo.syncNeeded)
    }

    @Test
    fun `로그인 상태이고 저장된 토큰이 있지만 isSyncNeeded가 false이면 서버 등록 없이 Result_success를 반환한다`() = runTest {
        val (worker, repo) = createWorker(
            storedToken = "cached-token",
            syncNeeded = false,
        )

        val result = worker.doWork()

        assertEquals(Result.success(), result)
        assertTrue(repo.registeredTokens.isEmpty())
        assertFalse(repo.syncNeeded)
    }

    @Test
    fun `로그인 상태이고 저장된 토큰이 없을 때 Firebase fetch에 성공하면 토큰을 저장하고 서버에 등록한다`() = runTest {
        val (worker, repo) = createWorker(
            storedToken = null,
            syncNeeded = false,
            fcmTokenProvider = FakeFcmTokenProvider("fetched-token"),
        )

        val result = worker.doWork()

        assertEquals(Result.success(), result)
        assertEquals(listOf("fetched-token"), repo.markedTokens)
        assertEquals(listOf("fetched-token"), repo.registeredTokens)
        assertFalse(repo.syncNeeded)
    }

    @Test
    fun `로그인 상태이고 저장된 토큰이 없을 때 Firebase fetch에 실패하면 아무 작업 없이 Result_success를 반환한다`() = runTest {
        val (worker, repo) = createWorker(
            storedToken = null,
            syncNeeded = false,
            fcmTokenProvider = FakeFcmTokenProvider(null),
        )

        val result = worker.doWork()

        assertEquals(Result.success(), result)
        assertTrue(repo.markedTokens.isEmpty())
        assertTrue(repo.registeredTokens.isEmpty())
    }

    @Test
    fun `비로그인 상태이고 저장된 토큰이 있고 isSyncNeeded가 true이면 서버 등록 없이 Result_success를 반환한다`() = runTest {
        val (worker, repo) = createWorker(
            storedToken = "cached-token",
            syncNeeded = true,
            accessToken = null,
        )

        val result = worker.doWork()

        assertEquals(Result.success(), result)
        assertTrue(repo.registeredTokens.isEmpty())
    }

    @Test
    fun `비로그인 상태이고 저장된 토큰도 없으면 아무 작업 없이 Result_success를 반환한다`() = runTest {
        val (worker, repo) = createWorker(
            storedToken = null,
            syncNeeded = false,
            fcmTokenProvider = FakeFcmTokenProvider(null),
            accessToken = null,
        )

        val result = worker.doWork()

        assertEquals(Result.success(), result)
        assertTrue(repo.markedTokens.isEmpty())
        assertTrue(repo.registeredTokens.isEmpty())
    }

    @Test
    fun `서버 등록에 실패하면 Result_retry를 반환하고 isSyncNeeded를 유지한다`() = runTest {
        val (worker, repo) = createWorker(
            storedToken = "cached-token",
            syncNeeded = true,
            registerResult = NeveraResult.Failure(FcmTokenError.Common(CommonError.Unknown)),
        )

        val result = worker.doWork()

        assertEquals(Result.retry(), result)
        assertEquals(listOf("cached-token"), repo.registeredTokens)
        assertTrue(repo.syncNeeded)
    }

    @Test
    fun `서버 등록 실패 횟수가 MAX_RETRIES에 도달하면 Result_failure를 반환한다`() = runTest {
        val (worker, repo) = createWorker(
            storedToken = "cached-token",
            syncNeeded = true,
            registerResult = NeveraResult.Failure(FcmTokenError.Common(CommonError.Unknown)),
            runAttemptCount = 5,
        )

        val result = worker.doWork()

        assertEquals(Result.failure(), result)
        assertEquals(listOf("cached-token"), repo.registeredTokens)
        assertTrue(repo.syncNeeded)
    }

    // ── Update path (NeveraMessagingService 호출, input token 있음) ────────────

    @Test
    fun `새 토큰이 저장된 토큰과 동일하고 isSyncNeeded가 false이면 서버 등록 없이 Result_success를 반환한다`() = runTest {
        val inputData = workDataOf("fcm_token" to "same-token")
        val (worker, repo) = createWorker(
            inputData = inputData,
            storedToken = "same-token",
            syncNeeded = false,
        )

        val result = worker.doWork()

        assertEquals(Result.success(), result)
        assertTrue(repo.markedTokens.isEmpty())
        assertTrue(repo.registeredTokens.isEmpty())
    }

    @Test
    fun `새 토큰이 저장된 토큰과 다르고 로그인 상태이면 토큰을 갱신하고 서버에 등록한다`() = runTest {
        val inputData = workDataOf("fcm_token" to "new-token")
        val (worker, repo) = createWorker(
            inputData = inputData,
            storedToken = "old-token",
            syncNeeded = false,
        )

        val result = worker.doWork()

        assertEquals(Result.success(), result)
        assertEquals(listOf("new-token"), repo.markedTokens)
        assertEquals(listOf("new-token"), repo.registeredTokens)
        assertFalse(repo.syncNeeded)
    }

    @Test
    fun `새 토큰이 저장된 토큰과 다르고 비로그인 상태이면 토큰만 저장하고 서버 등록은 하지 않는다`() = runTest {
        val inputData = workDataOf("fcm_token" to "new-token")
        val (worker, repo) = createWorker(
            inputData = inputData,
            storedToken = "old-token",
            syncNeeded = false,
            accessToken = null,
        )

        val result = worker.doWork()

        assertEquals(Result.success(), result)
        assertEquals(listOf("new-token"), repo.markedTokens)
        assertTrue(repo.registeredTokens.isEmpty())
        assertTrue(repo.syncNeeded)
    }

    @Test
    fun `새 토큰이 저장된 토큰과 동일하지만 isSyncNeeded가 true이면 markTokenForSync 없이 서버에 등록한다`() = runTest {
        val inputData = workDataOf("fcm_token" to "same-token")
        val (worker, repo) = createWorker(
            inputData = inputData,
            storedToken = "same-token",
            syncNeeded = true,
        )

        val result = worker.doWork()

        assertEquals(Result.success(), result)
        assertTrue(repo.markedTokens.isEmpty())
        assertEquals(listOf("same-token"), repo.registeredTokens)
        assertFalse(repo.syncNeeded)
    }

    @Test
    fun `새 토큰으로 서버 등록에 실패하면 Result_retry를 반환한다`() = runTest {
        val inputData = workDataOf("fcm_token" to "new-token")
        val (worker, repo) = createWorker(
            inputData = inputData,
            storedToken = "old-token",
            syncNeeded = false,
            registerResult = NeveraResult.Failure(FcmTokenError.Common(CommonError.Unknown)),
        )

        val result = worker.doWork()

        assertEquals(Result.retry(), result)
        assertEquals(listOf("new-token"), repo.registeredTokens)
        assertTrue(repo.syncNeeded)
    }

    // ── 공통 ──────────────────────────────────────────────────────────────────

    @Test
    fun `Firebase가 CancellationException을 던지면 예외가 그대로 전파된다`() = runTest {
        val (worker, _) = createWorker(
            storedToken = null,
            syncNeeded = false,
            fcmTokenProvider = ThrowingFcmTokenProvider(CancellationException("cancelled")),
        )

        var thrown: Throwable? = null
        try {
            worker.doWork()
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
