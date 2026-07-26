package com.anddd.nevera.domain.usecase.auth

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.auth.LoginError
import com.anddd.nevera.domain.model.auth.LoginProvider
import com.anddd.nevera.domain.model.auth.LoginResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.testutil.FakeAuthRepository
import com.anddd.nevera.domain.testutil.FakeTokenRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EmailLoginUseCaseTest {

    private val authRepository = FakeAuthRepository()
    private val tokenRepository = FakeTokenRepository()
    private val useCase = EmailLoginUseCase(authRepository, tokenRepository)

    @Test
    fun `로그인에 성공하면 발급받은 토큰과 EMAIL 제공자를 함께 저장한다`() = runTest {
        authRepository.emailLoginResult = NeveraResult.Success(
            LoginResult(accessToken = "새-접근-토큰", refreshToken = "새-갱신-토큰"),
        )

        useCase(email = "user@example.com", password = "password1!")

        assertEquals(
            listOf(
                FakeTokenRepository.LoginInfo(
                    accessToken = "새-접근-토큰",
                    refreshToken = "새-갱신-토큰",
                    provider = LoginProvider.EMAIL,
                ),
            ),
            tokenRepository.loginInfoCalls,
        )
    }

    @Test
    fun `로그인에 성공하면 Success를 반환한다`() = runTest {
        authRepository.emailLoginResult = NeveraResult.Success(
            LoginResult(accessToken = "a", refreshToken = "r"),
        )

        val result = useCase(email = "user@example.com", password = "password1!")

        assertEquals(NeveraResult.Success(Unit), result)
    }

    @Test
    fun `로그인에 실패하면 토큰을 저장하지 않는다`() = runTest {
        authRepository.emailLoginResult = NeveraResult.Failure(LoginError.InvalidCredentials)

        useCase(email = "user@example.com", password = "틀린비밀번호1!")

        assertTrue(tokenRepository.loginInfoCalls.isEmpty())
        assertNull(tokenRepository.accessToken)
        assertNull(tokenRepository.refreshToken)
        assertNull(tokenRepository.provider)
    }

    @Test
    fun `로그인에 실패하면 저장소가 돌려준 에러를 그대로 전달한다`() = runTest {
        authRepository.emailLoginResult = NeveraResult.Failure(LoginError.InvalidCredentials)

        val result = useCase(email = "user@example.com", password = "틀린비밀번호1!")

        assertEquals(NeveraResult.Failure(LoginError.InvalidCredentials), result)
    }

    @Test
    fun `네트워크 오류로 실패해도 토큰을 저장하지 않는다`() = runTest {
        authRepository.emailLoginResult =
            NeveraResult.Failure(LoginError.Common(CommonError.NetworkUnavailable))

        val result = useCase(email = "user@example.com", password = "password1!")

        assertEquals(NeveraResult.Failure(LoginError.Common(CommonError.NetworkUnavailable)), result)
        assertTrue(tokenRepository.loginInfoCalls.isEmpty())
    }

    @Test
    fun `입력받은 이메일과 비밀번호를 그대로 저장소에 전달한다`() = runTest {
        useCase(email = "user@example.com", password = "password1!")

        assertEquals(listOf("user@example.com" to "password1!"), authRepository.emailLoginCalls)
    }
}
