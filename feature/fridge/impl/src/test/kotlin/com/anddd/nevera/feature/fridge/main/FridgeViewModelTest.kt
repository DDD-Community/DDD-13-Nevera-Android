package com.anddd.nevera.feature.fridge.main

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.IngredientSortOrder
import com.anddd.nevera.domain.model.ingredient.ProcessIngredientError
import com.anddd.nevera.domain.model.ingredient.ProcessRatio
import com.anddd.nevera.domain.model.ingredient.ProcessType
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import com.anddd.nevera.domain.usecase.ingredient.GetFridgeIngredientsUseCase
import com.anddd.nevera.domain.usecase.ingredient.ObserveFridgeIngredientsUseCase
import com.anddd.nevera.domain.usecase.ingredient.ProcessIngredientUseCase
import com.anddd.nevera.domain.usecase.notification.MarkAllNotificationsAsReadUseCase
import com.anddd.nevera.domain.usecase.notification.ObserveUnreadNotificationUseCase
import com.anddd.nevera.feature.fridge.main.model.CategoryFilter
import com.anddd.nevera.feature.fridge.main.model.FridgeIngredientUiModel
import com.anddd.nevera.feature.fridge.main.model.FridgeIntent
import com.anddd.nevera.feature.fridge.main.model.FridgeSideEffect
import com.anddd.nevera.feature.fridge.main.model.FridgeUiState
import com.anddd.nevera.feature.fridge.main.model.StorageLocationFilter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.orbitmvi.orbit.test.test
import java.time.LocalDate

/**
 * 냉장고 화면의 Intent 처리 계약을 고정한다.
 *
 * `orbit-test`의 `test()`는 검증용 컨테이너를 새로 만들어 바꿔치기하므로 `init` 블록의
 * 초기 로딩과 흐름 구독은 관찰되지 않는다. 여기서는 `initialState`로 상태를 주입하고
 * 사용자 액션이 만드는 상태 전이와 SideEffect만 검증한다.
 *
 * 특히 처리 비율은 화면이 넘기는 0~1 사이 실수를 서버가 허용하는 네 단계로 접는데,
 * 경계값을 잘못 잡으면 사용자가 고른 비율과 다른 값이 서버로 나간다. 눈으로는
 * 알아채기 어려운 종류의 오류라 경계값마다 테스트를 둔다.
 */
class FridgeViewModelTest {

    private val getFridgeIngredients = mockk<GetFridgeIngredientsUseCase>()
    private val observeUnreadNotification = mockk<ObserveUnreadNotificationUseCase>()
    private val markAllNotificationsAsRead = mockk<MarkAllNotificationsAsReadUseCase>()
    private val observeFridgeIngredients = mockk<ObserveFridgeIngredientsUseCase>()
    private val processIngredient = mockk<ProcessIngredientUseCase>()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())

        every { observeUnreadNotification() } returns emptyFlow()
        every { observeFridgeIngredients() } returns emptyFlow()
        coEvery { getFridgeIngredients(any(), any(), any()) } returns NeveraResult.Success(emptyList())
        coEvery { markAllNotificationsAsRead() } returns Unit
        coEvery { processIngredient(any(), any(), any()) } returns
            NeveraResult.Failure(ProcessIngredientError.InventoryNotFound)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = FridgeViewModel(
        getFridgeIngredients = getFridgeIngredients,
        observeUnreadNotification = observeUnreadNotification,
        markAllNotificationsAsRead = markAllNotificationsAsRead,
        observeFridgeIngredients = observeFridgeIngredients,
        processIngredient = processIngredient,
    )

    private val item = FridgeIngredientUiModel(
        id = 7L,
        name = "당근",
        category = FoodCategory.Veg,
        quantity = 1,
        cost = 3_000,
        expiryDate = LocalDate.of(2026, 12, 31),
    )

    // ── 필터와 정렬 ──────────────────────────────────────────────────────────────

    @Test
    fun `보관위치 필터를 바꾸면 상태에 반영하고 목록을 다시 불러온다`() = runTest {
        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(
                FridgeIntent.SelectStorageFilter(StorageLocationFilter.Specific(StorageLocation.Freezer)),
            )

            expectState { copy(selectedStorageFilter = StorageLocationFilter.Specific(StorageLocation.Freezer)) }
            expectState { copy(isLoading = true) }
            expectState { copy(isLoading = false) }
        }

        coVerify {
            getFridgeIngredients(
                storageLocation = StorageLocation.Freezer,
                category = null,
                sortOrder = IngredientSortOrder.ExpiryDate,
            )
        }
    }

    @Test
    fun `카테고리 필터는 선택된 보관위치별로 따로 기억된다`() = runTest {
        val state = FridgeUiState(
            selectedStorageFilter = StorageLocationFilter.Specific(StorageLocation.Fridge),
        )

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(
                FridgeIntent.SelectCategoryFilter(CategoryFilter.Specific(FoodCategory.Fruit)),
            )

            expectState {
                copy(
                    categoryFilters = persistentMapOf(
                        StorageLocationFilter.Specific(StorageLocation.Fridge) to
                            CategoryFilter.Specific(FoodCategory.Fruit),
                    ),
                )
            }
            expectState { copy(isLoading = true) }
            expectState { copy(isLoading = false) }
        }

        coVerify {
            getFridgeIngredients(
                storageLocation = StorageLocation.Fridge,
                category = FoodCategory.Fruit,
                sortOrder = IngredientSortOrder.ExpiryDate,
            )
        }
    }

    @Test
    fun `정렬 기준을 바꾸면 상태에 반영하고 목록을 다시 불러온다`() = runTest {
        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.SelectSortOrder(IngredientSortOrder.Latest))

            expectState { copy(selectedSortOrder = IngredientSortOrder.Latest) }
            expectState { copy(isLoading = true) }
            expectState { copy(isLoading = false) }
        }

        coVerify {
            getFridgeIngredients(
                storageLocation = null,
                category = null,
                sortOrder = IngredientSortOrder.Latest,
            )
        }
    }

    @Test
    fun `목록 조회에 실패하면 안내 토스트를 띄우고 로딩을 끝낸다`() = runTest {
        coEvery { getFridgeIngredients(any(), any(), any()) } returns
            NeveraResult.Failure(CommonError.NetworkUnavailable)

        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.SelectSortOrder(IngredientSortOrder.Latest))

            expectState { copy(selectedSortOrder = IngredientSortOrder.Latest) }
            expectState { copy(isLoading = true) }
            expectSideEffect(FridgeSideEffect.ShowToast("데이터를 불러오지 못했습니다."))
            expectState { copy(isLoading = false) }
        }
    }

    // ── 처리 비율 경계값 ─────────────────────────────────────────────────────────

    @Test
    fun `비율 0_25 이하는 25퍼센트로 접힌다`() = runTest {
        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.RescueConfirm(item, 0.25f))
            expectSideEffect(FridgeSideEffect.ShowToast("식재료를 찾을 수 없어요."))
        }

        coVerify { processIngredient(item.id, ProcessType.Consumed, ProcessRatio.Quarter) }
    }

    @Test
    fun `비율 0_5 이하는 50퍼센트로 접힌다`() = runTest {
        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.RescueConfirm(item, 0.5f))
            expectSideEffect(FridgeSideEffect.ShowToast("식재료를 찾을 수 없어요."))
        }

        coVerify { processIngredient(item.id, ProcessType.Consumed, ProcessRatio.Half) }
    }

    @Test
    fun `비율 0_75 이하는 75퍼센트로 접힌다`() = runTest {
        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.RescueConfirm(item, 0.75f))
            expectSideEffect(FridgeSideEffect.ShowToast("식재료를 찾을 수 없어요."))
        }

        coVerify { processIngredient(item.id, ProcessType.Consumed, ProcessRatio.ThreeQuarters) }
    }

    @Test
    fun `비율 0_75 초과는 100퍼센트로 접힌다`() = runTest {
        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.RescueConfirm(item, 1.0f))
            expectSideEffect(FridgeSideEffect.ShowToast("식재료를 찾을 수 없어요."))
        }

        coVerify { processIngredient(item.id, ProcessType.Consumed, ProcessRatio.Full) }
    }

    @Test
    fun `폐기 확정은 구조가 아닌 폐기 유형으로 처리를 요청한다`() = runTest {
        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.DisposeConfirm(item, 0.5f))
            expectSideEffect(FridgeSideEffect.ShowToast("식재료를 찾을 수 없어요."))
        }

        coVerify { processIngredient(item.id, ProcessType.Wasted, ProcessRatio.Half) }
    }

    @Test
    fun `처리에 성공하면 토스트를 띄우지 않는다`() = runTest {
        coEvery { processIngredient(any(), any(), any()) } returns NeveraResult.Success(mockk())

        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.RescueConfirm(item, 0.5f))

            expectNoItems()
        }
    }

    @Test
    fun `처리 실패 사유마다 다른 안내 문구를 보여준다`() = runTest {
        coEvery { processIngredient(any(), any(), any()) } returns
            NeveraResult.Failure(ProcessIngredientError.AlreadyCompleted)

        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.RescueConfirm(item, 0.5f))

            expectSideEffect(FridgeSideEffect.ShowToast("이미 처리가 완료된 식재료예요."))
        }
    }

    @Test
    fun `분류되지 않은 처리 실패는 일반 안내 문구를 보여준다`() = runTest {
        coEvery { processIngredient(any(), any(), any()) } returns
            NeveraResult.Failure(ProcessIngredientError.Common(CommonError.NetworkUnavailable))

        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.RescueConfirm(item, 0.5f))

            expectSideEffect(FridgeSideEffect.ShowToast("처리 중 오류가 발생했어요."))
        }
    }

    // ── 그 밖의 사용자 액션 ──────────────────────────────────────────────────────

    @Test
    fun `알림 아이콘을 누르면 모두 읽음 처리한 뒤 알림 화면으로 이동한다`() = runTest {
        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.NotificationIconClicked)

            expectSideEffect(FridgeSideEffect.NavigateToNotification)
        }

        coVerify { markAllNotificationsAsRead() }
    }

    @Test
    fun `재료 추가를 누르면 촬영 방식 바텀시트를 띄운다`() = runTest {
        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.AddIngredientClick)

            expectSideEffect(FridgeSideEffect.ShowCaptureModeBottomSheet)
        }
    }

    @Test
    fun `구조 버튼을 누르면 해당 재료로 구조 바텀시트를 띄운다`() = runTest {
        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.RescueClick(item))

            expectSideEffect(FridgeSideEffect.ShowRescueBottomSheet(item))
        }
    }

    @Test
    fun `폐기 버튼을 누르면 해당 재료로 폐기 바텀시트를 띄운다`() = runTest {
        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.DisposeClick(item))

            expectSideEffect(FridgeSideEffect.ShowDisposeBottomSheet(item))
        }
    }

    @Test
    fun `더보기를 누르면 해당 재료 수정 화면으로 이동한다`() = runTest {
        createViewModel().test(this, initialState = FridgeUiState()) {
            containerHost.handleIntent(FridgeIntent.IngredientMoreClick(item))

            expectSideEffect(FridgeSideEffect.NavigateToEditIngredient(item.id))
        }
    }
}
