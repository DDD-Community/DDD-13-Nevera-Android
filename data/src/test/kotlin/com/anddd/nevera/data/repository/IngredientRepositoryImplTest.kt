package com.anddd.nevera.data.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.core.network.auth.ApiCallExecutor
import com.anddd.nevera.core.network.model.ApiError
import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.model.fridge.FridgeIngredientsResponse
import com.anddd.nevera.data.testutil.FakeFridgeRemoteDataSource
import com.anddd.nevera.data.testutil.FakeIngredientRemoteDataSource
import com.anddd.nevera.data.testutil.FakeOcrDataSource
import com.anddd.nevera.data.testutil.FakeOcrProgressDataSource
import com.anddd.nevera.data.testutil.fridgeIngredientResponse
import com.anddd.nevera.data.testutil.ingredientResponse
import com.anddd.nevera.data.testutil.processIngredientResponse
import com.anddd.nevera.domain.model.ingredient.EditIngredientInput
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.IngredientSortOrder
import com.anddd.nevera.domain.model.ingredient.ProcessRatio
import com.anddd.nevera.domain.model.ingredient.ProcessType
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * 냉장고 재료 메모리 캐시의 조작 규칙을 고정한다.
 *
 * 이 저장소는 목록을 메모리에 들고 있다가 `observeFridgeIngredients()`로 여러 화면에
 * 흘려보낸다. 그래서 캐시 조작이 틀리면 증상이 조작을 일으킨 화면이 아니라 그 목록을
 * 구독하는 다른 화면에서 나타나고, 손으로 재현하기가 특히 어렵다.
 */
class IngredientRepositoryImplTest {

    private val ingredientDataSource = FakeIngredientRemoteDataSource()
    private val fridgeDataSource = FakeFridgeRemoteDataSource()
    private val repository = IngredientRepositoryImpl(
        ocrDataSource = FakeOcrDataSource(),
        ocrProgressDataSource = FakeOcrProgressDataSource(),
        ingredientRemoteDataSource = ingredientDataSource,
        fridgeRemoteDataSource = fridgeDataSource,
        apiCall = ApiCallExecutor(Gson()),
    )

    private val editInput = EditIngredientInput(
        name = "당근",
        category = FoodCategory.Veg,
        location = StorageLocation.Fridge,
        quantity = 1,
        expiryDate = LocalDate.of(2026, 12, 31),
        cost = 3_000,
    )

    /** 냉장 보관 채소 두 건(식별자 1, 2)으로 캐시를 채운다. */
    private suspend fun fillCache() {
        fridgeDataSource.fridgeIngredientsResponse = ApiResponse(
            result = FridgeIngredientsResponse(
                content = listOf(
                    fridgeIngredientResponse(id = 1L, name = "당근", category = "VEG", location = "FRIDGE", cost = 3_000),
                    fridgeIngredientResponse(id = 2L, name = "양파", category = "VEG", location = "FRIDGE", cost = 2_000),
                ),
                last = true,
                number = 0,
            ),
            error = null,
        )
        repository.getFridgeIngredients(
            storageLocation = StorageLocation.Fridge,
            category = FoodCategory.Veg,
            sortOrder = IngredientSortOrder.Latest,
            page = 0,
            size = 20,
        )
    }

    private suspend fun cachedIngredients() = repository.observeFridgeIngredients().first()

    @Test
    fun `목록 조회에 성공하면 캐시가 채워지고 관찰자에게 흘러간다`() = runTest {
        fillCache()

        assertEquals(listOf(1L, 2L), cachedIngredients().map { it.id })
    }

    @Test
    fun `수정 결과의 보관위치와 카테고리가 그대로면 캐시 항목을 교체한다`() = runTest {
        fillCache()
        ingredientDataSource.editResponse = ApiResponse(
            result = fridgeIngredientResponse(
                id = 1L,
                name = "햇당근",
                category = "VEG",
                location = "FRIDGE",
                cost = 9_000,
            ),
            error = null,
        )

        repository.editIngredient(id = 1L, input = editInput)

        val cached = cachedIngredients()
        assertEquals(listOf(1L, 2L), cached.map { it.id })
        assertEquals("햇당근", cached.first { it.id == 1L }.name)
        assertEquals(9_000, cached.first { it.id == 1L }.cost)
    }

    @Test
    fun `수정으로 보관위치가 바뀌면 캐시에서 제거한다`() = runTest {
        fillCache()
        ingredientDataSource.editResponse = ApiResponse(
            result = fridgeIngredientResponse(id = 1L, category = "VEG", location = "FREEZER"),
            error = null,
        )

        repository.editIngredient(id = 1L, input = editInput)

        // 현재 화면은 냉장 보관만 보여주므로 냉동으로 옮겨진 항목은 목록에서 빠져야 한다.
        assertEquals(listOf(2L), cachedIngredients().map { it.id })
    }

    @Test
    fun `수정으로 카테고리가 바뀌면 캐시에서 제거한다`() = runTest {
        fillCache()
        ingredientDataSource.editResponse = ApiResponse(
            result = fridgeIngredientResponse(id = 1L, category = "FRUIT", location = "FRIDGE"),
            error = null,
        )

        repository.editIngredient(id = 1L, input = editInput)

        assertEquals(listOf(2L), cachedIngredients().map { it.id })
    }

    @Test
    fun `캐시에 없는 항목을 수정해도 캐시는 그대로다`() = runTest {
        fillCache()
        ingredientDataSource.editResponse = ApiResponse(
            result = fridgeIngredientResponse(id = 99L, category = "VEG", location = "FRIDGE"),
            error = null,
        )

        repository.editIngredient(id = 99L, input = editInput)

        assertEquals(listOf(1L, 2L), cachedIngredients().map { it.id })
    }

    @Test
    fun `수정에 실패하면 캐시를 건드리지 않는다`() = runTest {
        fillCache()
        ingredientDataSource.editResponse = ApiResponse(
            result = null,
            error = ApiError(code = 4001, message = "존재하지 않는 재고"),
        )

        val result = repository.editIngredient(id = 1L, input = editInput)

        assertTrue(result is NeveraResult.Failure)
        assertEquals(listOf(1L, 2L), cachedIngredients().map { it.id })
        assertEquals(3_000, cachedIngredients().first { it.id == 1L }.cost)
    }

    @Test
    fun `처리가 완료되면 캐시에서 제거한다`() = runTest {
        fillCache()
        fridgeDataSource.processResponse = ApiResponse(
            result = processIngredientResponse(inventoryId = 1L, completed = true, remainingAmount = 0),
            error = null,
        )

        repository.processIngredient(
            inventoryId = 1L,
            processType = ProcessType.Consumed,
            ratio = ProcessRatio.Full,
        )

        assertEquals(listOf(2L), cachedIngredients().map { it.id })
    }

    @Test
    fun `부분 처리면 캐시 항목의 금액을 잔여 금액으로 갱신한다`() = runTest {
        fillCache()
        fridgeDataSource.processResponse = ApiResponse(
            result = processIngredientResponse(inventoryId = 1L, completed = false, remainingAmount = 1_500),
            error = null,
        )

        repository.processIngredient(
            inventoryId = 1L,
            processType = ProcessType.Consumed,
            ratio = ProcessRatio.Half,
        )

        val cached = cachedIngredients()
        assertEquals(listOf(1L, 2L), cached.map { it.id })
        assertEquals(1_500, cached.first { it.id == 1L }.cost)
        assertEquals(2_000, cached.first { it.id == 2L }.cost)
    }

    @Test
    fun `처리에 실패하면 캐시를 건드리지 않는다`() = runTest {
        fillCache()
        fridgeDataSource.processResponse = ApiResponse(
            result = null,
            error = ApiError(code = 4003, message = "이미 처리 완료"),
        )

        val result = repository.processIngredient(
            inventoryId = 1L,
            processType = ProcessType.Consumed,
            ratio = ProcessRatio.Full,
        )

        assertTrue(result is NeveraResult.Failure)
        assertEquals(listOf(1L, 2L), cachedIngredients().map { it.id })
        assertEquals(3_000, cachedIngredients().first { it.id == 1L }.cost)
    }

    @Test
    fun `처리 목록을 불러오면 구조 캐시와 폐기 캐시가 모두 채워진다`() = runTest {
        ingredientDataSource.rescuedResponse =
            ApiResponse(listOf(ingredientResponse(id = 10L, name = "구조된 당근")), null)
        ingredientDataSource.disposedResponse =
            ApiResponse(listOf(ingredientResponse(id = 20L, name = "폐기된 우유")), null)

        repository.loadProcessedIngredients()

        assertEquals(listOf(10L), repository.observeRescuedIngredients().first().map { it.id })
        assertEquals(listOf(20L), repository.observeDisposedIngredients().first().map { it.id })
    }

    @Test
    fun `폐기 목록 조회가 실패해도 구조 캐시는 정상적으로 채워진다`() = runTest {
        ingredientDataSource.rescuedResponse =
            ApiResponse(listOf(ingredientResponse(id = 10L)), null)
        ingredientDataSource.disposedResponse =
            ApiResponse(null, ApiError(code = 500, message = "서버 내부 오류"))

        repository.loadProcessedIngredients()

        assertEquals(listOf(10L), repository.observeRescuedIngredients().first().map { it.id })
        assertNull(
            withTimeoutOrNull(EMISSION_TIMEOUT_MILLIS) { repository.observeDisposedIngredients().first() },
        )
    }

    @Test
    fun `수정 중 코루틴이 취소되면 CancellationException을 삼키지 않고 전파한다`() = runTest {
        // 취소를 실패(NeveraResult.Failure)로 삼키면 구조적 동시성이 깨진다.
        ingredientDataSource.editError = CancellationException("취소됨")

        val thrown = runCatching { repository.editIngredient(id = 1L, input = editInput) }.exceptionOrNull()

        assertTrue(thrown is CancellationException)
    }

    private companion object {
        const val EMISSION_TIMEOUT_MILLIS = 100L
    }
}
