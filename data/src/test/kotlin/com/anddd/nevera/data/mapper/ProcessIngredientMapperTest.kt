package com.anddd.nevera.data.mapper

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.data.model.fridge.ProcessIngredientResponse
import com.anddd.nevera.data.testutil.httpError
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.ingredient.ProcessIngredientError
import com.anddd.nevera.domain.model.ingredient.ProcessType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** 식재료 구조·폐기 처리의 요청·응답·에러 변환 규칙을 고정한다. */
class ProcessIngredientMapperTest {

    private fun response(
        processedStatus: String = "CONSUMED",
        completed: Boolean = false,
        remainingAmount: Int = 5_000,
    ) = ProcessIngredientResponse(
        inventoryId = 1L,
        processedStatus = processedStatus,
        processedRatio = 50,
        processedAmount = 5_000,
        consumedRatio = 50,
        wastedRatio = 0,
        remainingRatio = 50,
        remainingAmount = remainingAmount,
        inventoryStatus = "ACTIVE",
        completed = completed,
    )

    @Test
    fun `ProcessType이 서버 요청 문자열로 변환된다`() {
        assertEquals("CONSUMED", ProcessType.Consumed.toApiString())
        assertEquals("WASTED", ProcessType.Wasted.toApiString())
    }

    @Test
    fun `응답의 처리 유형 문자열이 도메인 ProcessType으로 변환된다`() {
        assertEquals(ProcessType.Consumed, response(processedStatus = "CONSUMED").toDomain().processedStatus)
        assertEquals(ProcessType.Wasted, response(processedStatus = "WASTED").toDomain().processedStatus)
    }

    @Test
    fun `알 수 없는 처리 유형 문자열은 Consumed로 변환된다`() {
        // 폐기가 아니라 구조로 떨어지는 것이 현재 동작이다. 손실이 아닌 방향의 기본값이라
        // 의도된 선택이지만, 바뀌면 사용자에게 보이는 결과가 달라지므로 고정해 둔다.
        assertEquals(ProcessType.Consumed, response(processedStatus = "알수없음").toDomain().processedStatus)
    }

    @Test
    fun `응답의 나머지 필드는 그대로 도메인 모델로 옮겨진다`() {
        val result = response(completed = true, remainingAmount = 0).toDomain()

        assertEquals(1L, result.inventoryId)
        assertEquals(50, result.processedRatio)
        assertEquals(5_000, result.processedAmount)
        assertEquals(50, result.consumedRatio)
        assertEquals(0, result.wastedRatio)
        assertEquals(50, result.remainingRatio)
        assertEquals(0, result.remainingAmount)
        assertEquals("ACTIVE", result.inventoryStatus)
        assertTrue(result.completed)
    }

    @Test
    fun `서버 코드 4001은 InventoryNotFound가 된다`() {
        assertEquals(ProcessIngredientError.InventoryNotFound, httpError(4001).toProcessIngredientError())
    }

    @Test
    fun `서버 코드 4002는 InventoryForbidden이 된다`() {
        assertEquals(ProcessIngredientError.InventoryForbidden, httpError(4002).toProcessIngredientError())
    }

    @Test
    fun `서버 코드 4003은 AlreadyCompleted가 된다`() {
        assertEquals(ProcessIngredientError.AlreadyCompleted, httpError(4003).toProcessIngredientError())
    }

    @Test
    fun `서버 코드 4004는 InvalidProcessStatus가 된다`() {
        assertEquals(ProcessIngredientError.InvalidProcessStatus, httpError(4004).toProcessIngredientError())
    }

    @Test
    fun `서버 코드 4005는 InvalidProcessRatio가 된다`() {
        assertEquals(ProcessIngredientError.InvalidProcessRatio, httpError(4005).toProcessIngredientError())
    }

    @Test
    fun `서버 코드 4006은 ProcessRatioExceeded가 된다`() {
        assertEquals(ProcessIngredientError.ProcessRatioExceeded, httpError(4006).toProcessIngredientError())
    }

    @Test
    fun `알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            ProcessIngredientError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toProcessIngredientError(),
        )
    }

    @Test
    fun `네트워크 연결 실패는 공통 에러로 감싼다`() {
        assertEquals(
            ProcessIngredientError.Common(CommonError.NetworkUnavailable),
            NetworkError.NetworkConnectionError().toProcessIngredientError(),
        )
    }
}
