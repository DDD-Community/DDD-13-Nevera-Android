package com.anddd.nevera.domain.usecase

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.notification.FcmTokenError
import com.anddd.nevera.domain.testutil.FakeFcmTokenProvider
import com.anddd.nevera.domain.testutil.FakeFcmTokenRepository
import com.anddd.nevera.domain.testutil.FakeTokenRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * 로그인한 사용자의 기기 푸시 토큰을 서버와 동기화하는 UseCase의 계약을 고정한다.
 *
 * 이 UseCase는 "언제 서버에 등록하고 언제 건너뛰는가"가 네 가지 조건
 * (인자로 받은 토큰의 유무, 저장된 토큰과의 일치 여부, 동기화 필요 표시, 로그인 여부)의
 * 조합으로 결정되므로, 조합별로 등록 호출이 일어나는지를 검증한다.
 */
class SyncDeviceTokenUseCaseTest {

    private val fcmTokenRepository = FakeFcmTokenRepository()
    private val fcmTokenProvider = FakeFcmTokenProvider()
    private val tokenRepository = FakeTokenRepository(accessToken = "로그인된-접근-토큰")
    private val useCase = SyncDeviceTokenUseCase(fcmTokenRepository, fcmTokenProvider, tokenRepository)

    @Test
    fun `저장된 토큰과 다른 새 토큰이 들어오면 저장한 뒤 서버에 등록한다`() = runTest {
        fcmTokenRepository.storedToken = "이전-토큰"

        val result = useCase("새-토큰")

        assertEquals(listOf("새-토큰"), fcmTokenRepository.savedTokens)
        assertEquals(listOf("새-토큰"), fcmTokenRepository.registeredTokens)
        assertEquals(NeveraResult.Success(Unit), result)
    }

    @Test
    fun `저장된 토큰과 같고 동기화가 필요 없으면 서버에 등록하지 않는다`() = runTest {
        fcmTokenRepository.storedToken = "같은-토큰"
        fcmTokenRepository.syncNeeded = false

        val result = useCase("같은-토큰")

        assertTrue(fcmTokenRepository.registeredTokens.isEmpty())
        assertTrue(fcmTokenRepository.savedTokens.isEmpty())
        assertEquals(NeveraResult.Success(Unit), result)
    }

    @Test
    fun `저장된 토큰과 같아도 동기화가 필요하면 서버에 등록한다`() = runTest {
        fcmTokenRepository.storedToken = "같은-토큰"
        fcmTokenRepository.syncNeeded = true

        useCase("같은-토큰")

        assertEquals(listOf("같은-토큰"), fcmTokenRepository.registeredTokens)
    }

    @Test
    fun `인자가 null이면 저장된 토큰으로 동기화한다`() = runTest {
        fcmTokenRepository.storedToken = "저장된-토큰"
        fcmTokenRepository.syncNeeded = true

        useCase(null)

        assertEquals(listOf("저장된-토큰"), fcmTokenRepository.registeredTokens)
        assertEquals(0, fcmTokenProvider.getTokenCount)
    }

    @Test
    fun `인자와 저장된 토큰이 모두 없으면 제공자에서 토큰을 받아 저장하고 등록한다`() = runTest {
        fcmTokenRepository.storedToken = null
        fcmTokenProvider.token = "제공자-토큰"

        useCase(null)

        assertEquals(1, fcmTokenProvider.getTokenCount)
        assertEquals(listOf("제공자-토큰"), fcmTokenRepository.savedTokens)
        assertEquals(listOf("제공자-토큰"), fcmTokenRepository.registeredTokens)
    }

    @Test
    fun `제공자도 토큰을 주지 못하면 아무것도 등록하지 않고 성공을 반환한다`() = runTest {
        fcmTokenRepository.storedToken = null
        fcmTokenProvider.token = null

        val result = useCase(null)

        assertTrue(fcmTokenRepository.registeredTokens.isEmpty())
        assertEquals(NeveraResult.Success(Unit), result)
    }

    @Test
    fun `제공자가 예외를 던져도 실패로 전파하지 않고 성공을 반환한다`() = runTest {
        fcmTokenRepository.storedToken = null
        fcmTokenProvider.error = IllegalStateException("Firebase 초기화 실패")

        val result = useCase(null)

        assertTrue(fcmTokenRepository.registeredTokens.isEmpty())
        assertEquals(NeveraResult.Success(Unit), result)
    }

    @Test
    fun `제공자가 취소 예외를 던지면 삼키지 않고 재전파하며 아무것도 등록하지 않는다`() = runTest {
        // 일반 예외는 삼켜 성공으로 넘기지만, 취소는 상위 코루틴을 위해 반드시 재전파해야 한다.
        fcmTokenRepository.storedToken = null
        fcmTokenProvider.error = CancellationException("취소됨")

        val thrown = runCatching { useCase(null) }.exceptionOrNull()

        assertTrue(thrown is CancellationException)
        assertTrue(fcmTokenRepository.registeredTokens.isEmpty())
    }

    @Test
    fun `로그인 상태가 아니면 서버에 등록하지 않는다`() = runTest {
        tokenRepository.accessToken = null
        fcmTokenRepository.storedToken = "이전-토큰"

        val result = useCase("새-토큰")

        assertTrue(fcmTokenRepository.registeredTokens.isEmpty())
        assertEquals(NeveraResult.Success(Unit), result)
    }

    @Test
    fun `로그인 상태가 아니어도 새 토큰은 로컬에 저장한다`() = runTest {
        tokenRepository.accessToken = null
        fcmTokenRepository.storedToken = "이전-토큰"

        useCase("새-토큰")

        assertEquals(listOf("새-토큰"), fcmTokenRepository.savedTokens)
    }

    @Test
    fun `등록에 성공하면 동기화 필요 표시를 해제한다`() = runTest {
        fcmTokenRepository.storedToken = "같은-토큰"
        fcmTokenRepository.syncNeeded = true
        fcmTokenRepository.registerResult = NeveraResult.Success(Unit)

        useCase("같은-토큰")

        assertEquals(1, fcmTokenRepository.clearSyncNeededCount)
        assertFalse(fcmTokenRepository.syncNeeded)
    }

    @Test
    fun `등록에 실패하면 동기화 필요 표시를 유지한다`() = runTest {
        fcmTokenRepository.storedToken = "같은-토큰"
        fcmTokenRepository.syncNeeded = true
        fcmTokenRepository.registerResult =
            NeveraResult.Failure(FcmTokenError.Common(CommonError.NetworkUnavailable))

        useCase("같은-토큰")

        assertEquals(0, fcmTokenRepository.clearSyncNeededCount)
        assertTrue(fcmTokenRepository.syncNeeded)
    }

    @Test
    fun `등록이 MemberNotFound로 실패하면 Unknown 공통 에러로 바꿔 반환한다`() = runTest {
        fcmTokenRepository.storedToken = "같은-토큰"
        fcmTokenRepository.syncNeeded = true
        fcmTokenRepository.registerResult = NeveraResult.Failure(FcmTokenError.MemberNotFound)

        val result = useCase("같은-토큰")

        assertEquals(NeveraResult.Failure(CommonError.Unknown), result)
    }

    @Test
    fun `등록이 공통 에러로 실패하면 그 공통 에러를 그대로 반환한다`() = runTest {
        fcmTokenRepository.storedToken = "같은-토큰"
        fcmTokenRepository.syncNeeded = true
        fcmTokenRepository.registerResult =
            NeveraResult.Failure(FcmTokenError.Common(CommonError.Timeout))

        val result = useCase("같은-토큰")

        assertEquals(NeveraResult.Failure(CommonError.Timeout), result)
    }
}
