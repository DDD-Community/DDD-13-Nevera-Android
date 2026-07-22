package com.anddd.nevera.data.testutil

import com.anddd.nevera.core.network.model.ApiResponse
import com.anddd.nevera.data.datasource.FridgeRemoteDataSource
import com.anddd.nevera.data.datasource.HomeRemoteDataSource
import com.anddd.nevera.data.datasource.IngredientRemoteDataSource
import com.anddd.nevera.data.datasource.OcrDataSource
import com.anddd.nevera.data.datasource.OcrProgressDataSource
import com.anddd.nevera.data.datasource.OcrProgressResponse
import com.anddd.nevera.data.model.fridge.FridgeIngredientResponse
import com.anddd.nevera.data.model.fridge.FridgeIngredientsResponse
import com.anddd.nevera.data.model.fridge.ProcessIngredientResponse
import com.anddd.nevera.data.model.home.HomeSummaryResponse
import com.anddd.nevera.data.model.ingredient.EditIngredientRequest
import com.anddd.nevera.data.model.ingredient.IngredientResponse
import com.anddd.nevera.data.model.ingredient.OcrIngredientDto
import com.anddd.nevera.data.model.ingredient.OcrJobResponse
import com.anddd.nevera.data.model.ingredient.RegisterIngredientRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * 저장소 구현체 테스트에서 쓰는 DataSource 대역들.
 *
 * 서버 호출을 흉내 내는 것이 목적이므로 미리 정해 둔 [ApiResponse]를 그대로 돌려준다.
 * 서버가 200과 함께 에러 본문을 내려주는 상황은 `result = null, error = ApiError(...)`로 표현한다.
 */
internal class FakeHomeRemoteDataSource(
    var summaryResponse: ApiResponse<HomeSummaryResponse> = ApiResponse(
        result = homeSummaryResponse(),
        error = null,
    ),
) : HomeRemoteDataSource {

    var getSummaryCount: Int = 0
        private set

    override suspend fun getSummary(): ApiResponse<HomeSummaryResponse> {
        getSummaryCount++
        return summaryResponse
    }
}

internal class FakeIngredientRemoteDataSource(
    var editResponse: ApiResponse<FridgeIngredientResponse> = ApiResponse(
        result = fridgeIngredientResponse(),
        error = null,
    ),
    var rescuedResponse: ApiResponse<List<IngredientResponse>> = ApiResponse(emptyList(), null),
    var disposedResponse: ApiResponse<List<IngredientResponse>> = ApiResponse(emptyList(), null),
) : IngredientRemoteDataSource {

    val editRequests = mutableListOf<Pair<Long, EditIngredientRequest>>()

    override suspend fun editIngredient(
        id: Long,
        request: EditIngredientRequest,
    ): ApiResponse<FridgeIngredientResponse> {
        editRequests += id to request
        return editResponse
    }

    override suspend fun getRescuedIngredients(
        offset: Int,
        limit: Int,
    ): ApiResponse<List<IngredientResponse>> = rescuedResponse

    override suspend fun getDisposedIngredients(
        offset: Int,
        limit: Int,
    ): ApiResponse<List<IngredientResponse>> = disposedResponse

    override suspend fun registerIngredients(
        items: List<RegisterIngredientRequest>,
    ): ApiResponse<Boolean> = ApiResponse(result = true, error = null)
}

internal class FakeFridgeRemoteDataSource(
    var fridgeIngredientsResponse: ApiResponse<FridgeIngredientsResponse> = ApiResponse(
        result = FridgeIngredientsResponse(content = emptyList(), last = true, number = 0),
        error = null,
    ),
    var processResponse: ApiResponse<ProcessIngredientResponse> = ApiResponse(
        result = processIngredientResponse(),
        error = null,
    ),
) : FridgeRemoteDataSource {

    override suspend fun getFridgeIngredients(
        storageLocation: String?,
        category: String?,
        sortType: String,
        page: Int,
        size: Int,
    ): ApiResponse<FridgeIngredientsResponse> = fridgeIngredientsResponse

    override suspend fun getFridgeIngredientById(id: Long): ApiResponse<FridgeIngredientResponse> =
        ApiResponse(result = fridgeIngredientResponse(id = id), error = null)

    override suspend fun processIngredient(
        inventoryId: Long,
        status: String,
        ratio: Int,
    ): ApiResponse<ProcessIngredientResponse> = processResponse
}

internal class FakeOcrDataSource : OcrDataSource {
    override suspend fun createOcrJob(): ApiResponse<OcrJobResponse> =
        throw UnsupportedOperationException("FakeOcrDataSource.createOcrJob 은 이 테스트에서 준비되지 않았다")

    override suspend fun extractIngredients(
        jobId: String,
        imageUri: String,
    ): ApiResponse<List<OcrIngredientDto>> =
        throw UnsupportedOperationException("FakeOcrDataSource.extractIngredients 은 이 테스트에서 준비되지 않았다")
}

internal class FakeOcrProgressDataSource : OcrProgressDataSource {
    override fun observeOcrProgress(jobId: String): Flow<OcrProgressResponse> = emptyFlow()
}

// ── 응답 본문 생성 도우미 ───────────────────────────────────────────────────────

internal fun homeSummaryResponse(
    nickname: String = "네베라",
    totalConsumed: Int = 50_000,
    totalWasted: Int = 12_000,
) = HomeSummaryResponse(
    nickname = nickname,
    wishId = null,
    wishName = null,
    wishAmount = null,
    accumulated = null,
    remaining = null,
    achieved = null,
    totalConsumed = totalConsumed,
    totalWasted = totalWasted,
)

internal fun fridgeIngredientResponse(
    id: Long = 1L,
    name: String = "당근",
    category: String = "VEG",
    location: String = "FRIDGE",
    quantity: Int = 1,
    cost: Int = 3_000,
) = FridgeIngredientResponse(
    id = id,
    name = name,
    category = category,
    location = location,
    quantity = quantity,
    expirationDate = "2026-12-31T00:00:00+09:00",
    cost = cost,
    createdAt = "2026-07-22T09:30:00+09:00",
)

internal fun ingredientResponse(
    id: Long = 1L,
    name: String = "당근",
    category: String = "VEG",
) = IngredientResponse(
    id = id,
    name = name,
    category = category,
    categoryDisplayName = "채소",
    quantity = 1,
    cost = 3_000,
)

internal fun processIngredientResponse(
    inventoryId: Long = 1L,
    completed: Boolean = false,
    remainingAmount: Int = 1_500,
) = ProcessIngredientResponse(
    inventoryId = inventoryId,
    processedStatus = "CONSUMED",
    processedRatio = 50,
    processedAmount = 1_500,
    consumedRatio = 50,
    wastedRatio = 0,
    remainingRatio = 50,
    remainingAmount = remainingAmount,
    inventoryStatus = "ACTIVE",
    completed = completed,
)
