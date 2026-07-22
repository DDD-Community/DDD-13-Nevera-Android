package com.anddd.nevera.domain.usecase.auth

import com.anddd.nevera.domain.testutil.FakeTokenRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CheckAutoLoginUseCaseTest {

    private val tokenRepository = FakeTokenRepository()
    private val useCase = CheckAutoLoginUseCase(tokenRepository)

    @Test
    fun `저장된 접근 토큰이 없으면 null을 반환한다`() = runTest {
        tokenRepository.accessToken = null

        assertNull(useCase())
    }

    @Test
    fun `저장된 접근 토큰이 빈 문자열이면 null을 반환한다`() = runTest {
        tokenRepository.accessToken = ""

        assertNull(useCase())
    }

    @Test
    fun `저장된 접근 토큰이 있으면 그 토큰을 반환한다`() = runTest {
        tokenRepository.accessToken = "저장된-접근-토큰"

        assertEquals("저장된-접근-토큰", useCase())
    }
}
