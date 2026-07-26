package com.anddd.nevera.domain.usecase.ingredient

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.ingredient.ProcessIngredientError
import com.anddd.nevera.domain.model.ingredient.ProcessRatio
import com.anddd.nevera.domain.model.ingredient.ProcessType
import com.anddd.nevera.domain.testutil.FakeHomeRepository
import com.anddd.nevera.domain.testutil.FakeIngredientRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProcessIngredientUseCaseTest {

    private val ingredientRepository = FakeIngredientRepository()
    private val homeRepository = FakeHomeRepository()
    private val useCase = ProcessIngredientUseCase(ingredientRepository, homeRepository)

    @Test
    fun `처리에 성공하면 홈 요약과 처리 목록을 각각 한 번씩 다시 불러온다`() = runTest {
        ingredientRepository.processResult =
            NeveraResult.Success(FakeIngredientRepository.defaultProcessResult())

        useCase(inventoryId = 1L, processType = ProcessType.Consumed, ratio = ProcessRatio.Half)

        assertEquals(1, homeRepository.loadSummaryCount)
        assertEquals(1, ingredientRepository.loadProcessedIngredientsCount)
    }

    @Test
    fun `처리에 실패하면 홈 요약을 다시 불러오지 않는다`() = runTest {
        ingredientRepository.processResult =
            NeveraResult.Failure(ProcessIngredientError.AlreadyCompleted)

        useCase(inventoryId = 1L, processType = ProcessType.Consumed, ratio = ProcessRatio.Half)

        assertEquals(0, homeRepository.loadSummaryCount)
    }

    @Test
    fun `처리에 실패하면 처리 목록을 다시 불러오지 않는다`() = runTest {
        ingredientRepository.processResult =
            NeveraResult.Failure(ProcessIngredientError.AlreadyCompleted)

        useCase(inventoryId = 1L, processType = ProcessType.Consumed, ratio = ProcessRatio.Half)

        assertEquals(0, ingredientRepository.loadProcessedIngredientsCount)
    }

    @Test
    fun `처리에 성공하면 저장소가 돌려준 결과를 그대로 반환한다`() = runTest {
        val processResult = FakeIngredientRepository.defaultProcessResult(
            inventoryId = 42L,
            completed = true,
            remainingAmount = 0,
        )
        ingredientRepository.processResult = NeveraResult.Success(processResult)

        val result = useCase(inventoryId = 42L, processType = ProcessType.Wasted, ratio = ProcessRatio.Full)

        assertEquals(NeveraResult.Success(processResult), result)
    }

    @Test
    fun `처리에 실패하면 저장소가 돌려준 에러를 그대로 반환한다`() = runTest {
        ingredientRepository.processResult =
            NeveraResult.Failure(ProcessIngredientError.InventoryNotFound)

        val result = useCase(inventoryId = 1L, processType = ProcessType.Consumed, ratio = ProcessRatio.Half)

        assertEquals(NeveraResult.Failure(ProcessIngredientError.InventoryNotFound), result)
    }

    @Test
    fun `입력받은 식재료 식별자와 처리 유형과 비율을 그대로 저장소에 전달한다`() = runTest {
        val inventoryId = 7L
        val processType = ProcessType.Wasted
        val ratio = ProcessRatio.ThreeQuarters

        useCase(inventoryId = inventoryId, processType = processType, ratio = ratio)

        assertEquals(
            listOf(
                FakeIngredientRepository.ProcessCall(
                    inventoryId = inventoryId,
                    processType = processType,
                    ratio = ratio,
                ),
            ),
            ingredientRepository.processCalls,
        )
    }
}
