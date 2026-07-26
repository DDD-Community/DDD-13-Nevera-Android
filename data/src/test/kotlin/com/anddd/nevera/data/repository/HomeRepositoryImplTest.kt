package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.core.network.model.ApiError
import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.testutil.FakeHomeRemoteDataSource
import com.anddd.nevera.data.testutil.homeSummaryResponse
import com.anddd.nevera.domain.model.common.CommonError
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * 홈 요약 캐시의 갱신 규칙을 고정한다.
 *
 * 이 저장소는 메모리에 요약 한 건을 들고 있다가 `observeHomeSummary()`로 흘려보낸다.
 * 값이 들어오기 전에는 아무것도 방출하지 않으므로, "실패했을 때 방출하지 않는다"를
 * 검증하려면 짧은 제한 시간을 두고 방출이 없음을 확인해야 한다.
 *
 * [ApiCallExecutor]는 인터페이스가 아니라 Gson 하나만 받는 결정적 변환기이므로
 * 대역을 만들지 않고 실제 구현을 쓴다. 덕분에 "HTTP 200이지만 본문에 에러가 담긴 응답"이
 * 실패로 처리되는 실제 동작까지 함께 검증된다.
 */
class HomeRepositoryImplTest {

    private val homeDataSource = FakeHomeRemoteDataSource()
    private val repository = HomeRepositoryImpl(homeDataSource, ApiCallExecutor(Gson()))

    private suspend fun observedSummaryOrNull() =
        withTimeoutOrNull(EMISSION_TIMEOUT_MILLIS) { repository.observeHomeSummary().first() }

    @Test
    fun `요약 조회에 성공하면 도메인 모델로 변환해 반환한다`() = runTest {
        homeDataSource.summaryResponse = ApiResponse(
            result = homeSummaryResponse(nickname = "네베라", totalConsumed = 70_000, totalWasted = 3_000),
            error = null,
        )

        val result = repository.loadSummary()

        val summary = (result as NeveraResult.Success).data
        assertEquals("네베라", summary.nickname)
        assertEquals(70_000, summary.rescuedAmount)
        assertEquals(3_000, summary.disposalAmount)
    }

    @Test
    fun `요약 조회에 성공하면 관찰자에게 그 값이 흘러간다`() = runTest {
        repository.loadSummary()

        assertEquals("네베라", observedSummaryOrNull()?.nickname)
    }

    @Test
    fun `요약을 한 번도 불러오지 않았으면 관찰자에게 아무것도 흘러가지 않는다`() = runTest {
        assertNull(observedSummaryOrNull())
    }

    @Test
    fun `서버가 에러 본문을 내려주면 실패로 처리한다`() = runTest {
        homeDataSource.summaryResponse = ApiResponse(
            result = null,
            error = ApiError(code = 500, message = "서버 내부 오류"),
        )

        val result = repository.loadSummary()

        assertEquals(NeveraResult.Failure(CommonError.ServerError("서버 내부 오류")), result)
    }

    @Test
    fun `요약 조회에 실패하면 관찰자에게 아무것도 흘러가지 않는다`() = runTest {
        homeDataSource.summaryResponse = ApiResponse(
            result = null,
            error = ApiError(code = 500, message = "서버 내부 오류"),
        )

        repository.loadSummary()

        assertNull(observedSummaryOrNull())
    }

    @Test
    fun `조회에 성공한 뒤 실패해도 이전 요약이 캐시에 남는다`() = runTest {
        homeDataSource.summaryResponse = ApiResponse(
            result = homeSummaryResponse(nickname = "이전-닉네임"),
            error = null,
        )
        repository.loadSummary()

        homeDataSource.summaryResponse = ApiResponse(
            result = null,
            error = ApiError(code = 500, message = "서버 내부 오류"),
        )
        repository.loadSummary()

        assertEquals("이전-닉네임", observedSummaryOrNull()?.nickname)
    }

    @Test
    fun `조회 중 코루틴이 취소되면 CancellationException을 삼키지 않고 전파한다`() = runTest {
        // 취소를 실패(NeveraResult.Failure)로 삼키면 구조적 동시성이 깨진다.
        homeDataSource.error = CancellationException("취소됨")

        val thrown = runCatching { repository.loadSummary() }.exceptionOrNull()

        assertTrue(thrown is CancellationException)
    }

    @Test
    fun `다시 불러오면 캐시가 새 값으로 바뀐다`() = runTest {
        homeDataSource.summaryResponse = ApiResponse(homeSummaryResponse(nickname = "이전-닉네임"), null)
        repository.loadSummary()

        homeDataSource.summaryResponse = ApiResponse(homeSummaryResponse(nickname = "새-닉네임"), null)
        repository.loadSummary()

        assertEquals("새-닉네임", observedSummaryOrNull()?.nickname)
        assertEquals(2, homeDataSource.getSummaryCount)
    }

    private companion object {
        const val EMISSION_TIMEOUT_MILLIS = 100L
    }
}
