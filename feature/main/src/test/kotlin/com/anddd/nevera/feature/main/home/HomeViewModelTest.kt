package com.anddd.nevera.feature.main.home

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.home.HomeSummary
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.Ingredient
import com.anddd.nevera.domain.model.user.OnboardingStatus
import com.anddd.nevera.domain.model.user.Profile
import com.anddd.nevera.domain.model.user.UpdateNicknameError
import com.anddd.nevera.domain.model.wish.CreateWishError
import com.anddd.nevera.domain.model.wish.UpdateWishError
import com.anddd.nevera.domain.model.wish.Wish
import com.anddd.nevera.domain.usecase.home.GetHomeSummaryUseCase
import com.anddd.nevera.domain.usecase.home.ObserveHomeSummaryUseCase
import com.anddd.nevera.domain.usecase.ingredient.GetDisposedIngredientsUseCase
import com.anddd.nevera.domain.usecase.ingredient.GetRescuedIngredientsUseCase
import com.anddd.nevera.domain.usecase.ingredient.ObserveDisposedIngredientsUseCase
import com.anddd.nevera.domain.usecase.ingredient.ObserveRescuedIngredientsUseCase
import com.anddd.nevera.domain.usecase.notification.ObserveUnreadNotificationUseCase
import com.anddd.nevera.domain.usecase.user.GetOnboardingStatusUseCase
import com.anddd.nevera.domain.usecase.user.UpdateNicknameUseCase
import com.anddd.nevera.domain.usecase.wish.CreateWishUseCase
import com.anddd.nevera.domain.usecase.wish.UpdateWishUseCase
import com.anddd.nevera.feature.main.home.model.HomeIntent
import com.anddd.nevera.feature.main.home.model.HomeSideEffect
import com.anddd.nevera.feature.main.home.model.HomeUiState
import com.anddd.nevera.feature.main.home.model.IngredientFilterTab
import com.anddd.nevera.feature.main.home.model.IngredientUiModel
import com.anddd.nevera.feature.main.home.model.PaginatedListState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
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

/**
 * 홈 화면의 Intent 처리 계약을 고정한다.
 *
 * `orbit-test`의 `test()`는 검증용 컨테이너를 새로 만들어 바꿔치기하므로, ViewModel의
 * `init` 블록에서 시작되는 초기 로딩과 흐름 구독은 테스트가 관찰하는 컨테이너에서
 * 재현되지 않는다. 그래서 여기서는 `initialState`로 원하는 상태를 직접 주입하고,
 * 그 상태에서 사용자 액션이 어떤 상태 전이와 SideEffect를 만드는지만 검증한다.
 * 초기 로딩 로직 자체는 domain·data 레이어의 테스트가 담당한다.
 */
class HomeViewModelTest {

    private val getHomeSummary = mockk<GetHomeSummaryUseCase>()
    private val observeHomeSummary = mockk<ObserveHomeSummaryUseCase>()
    private val getRescuedIngredients = mockk<GetRescuedIngredientsUseCase>()
    private val getDisposedIngredients = mockk<GetDisposedIngredientsUseCase>()
    private val observeRescuedIngredients = mockk<ObserveRescuedIngredientsUseCase>()
    private val observeDisposedIngredients = mockk<ObserveDisposedIngredientsUseCase>()
    private val updateNickname = mockk<UpdateNicknameUseCase>()
    private val getOnboardingStatus = mockk<GetOnboardingStatusUseCase>()
    private val createWish = mockk<CreateWishUseCase>()
    private val updateWish = mockk<UpdateWishUseCase>()
    private val observeUnreadNotification = mockk<ObserveUnreadNotificationUseCase>()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())

        // init 블록이 곧바로 호출하는 것들. 테스트가 관찰하지는 않지만 준비는 되어 있어야 한다.
        every { observeHomeSummary() } returns emptyFlow()
        every { observeRescuedIngredients() } returns emptyFlow()
        every { observeDisposedIngredients() } returns emptyFlow()
        every { observeUnreadNotification() } returns emptyFlow()
        coEvery { getHomeSummary() } returns NeveraResult.Success(
            HomeSummary(nickname = "네베라", wish = null, rescuedAmount = 0, disposalAmount = 0),
        )
        coEvery { getRescuedIngredients(any(), any()) } returns NeveraResult.Success(emptyList())
        coEvery { getDisposedIngredients(any(), any()) } returns NeveraResult.Success(emptyList())
        coEvery { getOnboardingStatus() } returns NeveraResult.Success(OnboardingStatus(true))
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = HomeViewModel(
        getHomeSummary = getHomeSummary,
        observeHomeSummary = observeHomeSummary,
        getRescuedIngredients = getRescuedIngredients,
        getDisposedIngredients = getDisposedIngredients,
        observeRescuedIngredients = observeRescuedIngredients,
        observeDisposedIngredients = observeDisposedIngredients,
        updateNickname = updateNickname,
        getOnboardingStatus = getOnboardingStatus,
        createWish = createWish,
        updateWish = updateWish,
        observeUnreadNotification = observeUnreadNotification,
    )

    private fun ingredient(id: Long) = Ingredient(
        id = id,
        name = "재료$id",
        category = FoodCategory.Veg,
        categoryName = "채소",
        quantity = 1,
        cost = 1_000,
    )

    private fun uiIngredient(id: Long) = IngredientUiModel(
        id = id,
        name = "재료$id",
        category = FoodCategory.Veg,
        categoryName = "채소",
        quantity = 1,
        cost = 1_000,
    )

    // ── 탭 전환과 단순 SideEffect ────────────────────────────────────────────────

    @Test
    fun `최근 재료 탭을 누르면 선택된 탭이 바뀐다`() = runTest {
        createViewModel().test(this, initialState = HomeUiState()) {
            containerHost.handleIntent(HomeIntent.RecentIngredientTabClick(IngredientFilterTab.Rescue))

            expectState { copy(ingredientFilterTab = IngredientFilterTab.Rescue) }
        }
    }

    @Test
    fun `재료 추가를 누르면 촬영 방식 바텀시트를 띄운다`() = runTest {
        createViewModel().test(this, initialState = HomeUiState()) {
            containerHost.handleIntent(HomeIntent.AddIngredientClick)

            expectSideEffect(HomeSideEffect.ShowCaptureModeBottomSheet)
        }
    }

    @Test
    fun `알림 아이콘을 누르면 알림 화면으로 이동한다`() = runTest {
        createViewModel().test(this, initialState = HomeUiState()) {
            containerHost.handleIntent(HomeIntent.NotificationIconClicked)

            expectSideEffect(HomeSideEffect.NavigateToNotification)
        }
    }

    @Test
    fun `위시 만들기를 누르면 위시 생성 바텀시트를 띄운다`() = runTest {
        createViewModel().test(this, initialState = HomeUiState()) {
            containerHost.handleIntent(HomeIntent.CreateWishClick)

            expectSideEffect(HomeSideEffect.ShowCreateWishBottomSheet)
        }
    }

    @Test
    fun `위시 수정을 누르면 위시 수정 바텀시트를 띄운다`() = runTest {
        createViewModel().test(this, initialState = HomeUiState()) {
            containerHost.handleIntent(HomeIntent.WishEditClick)

            expectSideEffect(HomeSideEffect.ShowUpdateWishBottomSheet)
        }
    }

    // ── 목록 더 불러오기 ─────────────────────────────────────────────────────────

    @Test
    fun `더 불러올 것이 없으면 추가 요청을 보내지 않는다`() = runTest {
        val state = HomeUiState(
            rescuedIngredients = PaginatedListState(
                items = persistentListOf(uiIngredient(1)),
                hasMore = false,
                currentOffset = 1,
            ),
        )

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(HomeIntent.LoadMoreIngredients(IngredientFilterTab.Rescue))

            expectNoItems()
        }

        // 가드가 추가 로드 호출(offset=1)을 막았는지 확인한다.
        // (init의 최초 load는 offset=0이므로 구분된다.)
        coVerify(exactly = 0) { getRescuedIngredients(offset = 1, limit = 10) }
    }

    @Test
    fun `이미 불러오는 중이면 추가 요청을 보내지 않는다`() = runTest {
        val state = HomeUiState(
            rescuedIngredients = PaginatedListState(
                items = persistentListOf(uiIngredient(1)),
                isLoadingMore = true,
                hasMore = true,
                currentOffset = 1,
            ),
        )

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(HomeIntent.LoadMoreIngredients(IngredientFilterTab.Rescue))

            expectNoItems()
        }

        // 가드가 추가 로드 호출(offset=1)을 막았는지 확인한다.
        // (init의 최초 load는 offset=0이므로 구분된다.)
        coVerify(exactly = 0) { getRescuedIngredients(offset = 1, limit = 10) }
    }

    @Test
    fun `구조 목록을 더 불러오면 기존 목록 뒤에 이어 붙인다`() = runTest {
        coEvery { getRescuedIngredients(offset = 1, limit = 10) } returns
            NeveraResult.Success(listOf(ingredient(2), ingredient(3)))

        val state = HomeUiState(
            rescuedIngredients = PaginatedListState(
                items = persistentListOf(uiIngredient(1)),
                hasMore = true,
                currentOffset = 1,
            ),
        )

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(HomeIntent.LoadMoreIngredients(IngredientFilterTab.Rescue))

            expectState { copy(rescuedIngredients = rescuedIngredients.copy(isLoadingMore = true)) }
            expectState {
                copy(
                    rescuedIngredients = PaginatedListState(
                        items = persistentListOf(uiIngredient(1), uiIngredient(2), uiIngredient(3)),
                        isLoadingMore = false,
                        hasMore = false,
                        currentOffset = 3,
                    ),
                )
            }
        }
    }

    @Test
    fun `응답 개수가 한 페이지를 채우면 더 불러올 것이 있다고 표시한다`() = runTest {
        val fullPage = (2L..11L).map { ingredient(it) }
        coEvery { getRescuedIngredients(offset = 1, limit = 10) } returns NeveraResult.Success(fullPage)

        val state = HomeUiState(
            rescuedIngredients = PaginatedListState(
                items = persistentListOf(uiIngredient(1)),
                hasMore = true,
                currentOffset = 1,
            ),
        )

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(HomeIntent.LoadMoreIngredients(IngredientFilterTab.Rescue))

            expectState { copy(rescuedIngredients = rescuedIngredients.copy(isLoadingMore = true)) }
            expectState {
                copy(
                    rescuedIngredients = rescuedIngredients.copy(
                        items = persistentListOf(uiIngredient(1))
                            .addAll(fullPage.map { uiIngredient(it.id) }),
                        isLoadingMore = false,
                        hasMore = true,
                        currentOffset = 11,
                    ),
                )
            }
        }
    }

    @Test
    fun `추가 요청이 실패하면 목록은 그대로 두고 로딩 표시를 해제한다`() = runTest {
        coEvery { getRescuedIngredients(offset = 1, limit = 10) } returns
            NeveraResult.Failure(CommonError.NetworkUnavailable)

        val state = HomeUiState(
            rescuedIngredients = PaginatedListState(
                items = persistentListOf(uiIngredient(1)),
                hasMore = true,
                currentOffset = 1,
            ),
        )

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(HomeIntent.LoadMoreIngredients(IngredientFilterTab.Rescue))

            // 로딩을 켰다가 실패 시 다시 꺼야 다음 추가 로드 요청이 막히지 않는다.
            // (copy가 isLoadingMore만 바꾸므로 items가 그대로임도 함께 검증된다.)
            expectState { copy(rescuedIngredients = rescuedIngredients.copy(isLoadingMore = true)) }
            expectState { copy(rescuedIngredients = rescuedIngredients.copy(isLoadingMore = false)) }
        }
    }

    @Test
    fun `폐기 탭에서 더 불러오면 폐기 목록만 늘어난다`() = runTest {
        coEvery { getDisposedIngredients(offset = 1, limit = 10) } returns
            NeveraResult.Success(listOf(ingredient(2)))

        val state = HomeUiState(
            rescuedIngredients = PaginatedListState(items = persistentListOf(uiIngredient(9)), hasMore = true),
            disposalIngredients = PaginatedListState(
                items = persistentListOf(uiIngredient(1)),
                hasMore = true,
                currentOffset = 1,
            ),
        )

        createViewModel().test(this, initialState = state) {
            containerHost.handleIntent(HomeIntent.LoadMoreIngredients(IngredientFilterTab.Disposal))

            expectState { copy(disposalIngredients = disposalIngredients.copy(isLoadingMore = true)) }
            expectState {
                copy(
                    disposalIngredients = PaginatedListState(
                        items = persistentListOf(uiIngredient(1), uiIngredient(2)),
                        isLoadingMore = false,
                        hasMore = false,
                        currentOffset = 2,
                    ),
                )
            }
        }
    }

    // ── 닉네임과 위시 ────────────────────────────────────────────────────────────

    @Test
    fun `닉네임 변경에 성공하면 상태를 갱신하고 인사 바텀시트를 띄운다`() = runTest {
        coEvery { updateNickname("새이름") } returns NeveraResult.Success(
            Profile(profileImageUrl = "", nickname = "새이름", email = "a@b.com", hasWish = false),
        )

        createViewModel().test(this, initialState = HomeUiState()) {
            containerHost.handleIntent(HomeIntent.UpdateNicknameClick("새이름"))

            expectState { copy(profile = profile.copy(nickname = "새이름")) }
            expectSideEffect(HomeSideEffect.ShowGreetingBottomSheet)
        }
    }

    @Test
    fun `닉네임 변경에 실패하면 인사 바텀시트를 띄우지 않는다`() = runTest {
        coEvery { updateNickname("새이름") } returns
            NeveraResult.Failure(UpdateNicknameError.InvalidNickname)

        createViewModel().test(this, initialState = HomeUiState()) {
            containerHost.handleIntent(HomeIntent.UpdateNicknameClick("새이름"))

            expectNoItems()
        }
    }

    @Test
    fun `위시 생성에 성공하면 생성 완료 토스트를 띄운다`() = runTest {
        coEvery { createWish("에어팟", 300_000L) } returns
            NeveraResult.Success(Wish(id = 1L, name = "에어팟", amount = 300_000L))

        createViewModel().test(this, initialState = HomeUiState()) {
            containerHost.handleIntent(HomeIntent.CreateWishConfirmed("에어팟", 300_000L))

            expectSideEffect(HomeSideEffect.ShowWishCreatedToast)
        }
    }

    @Test
    fun `위시 생성에 실패하면 토스트를 띄우지 않는다`() = runTest {
        coEvery { createWish("에어팟", 300_000L) } returns
            NeveraResult.Failure(CreateWishError.InvalidInput)

        createViewModel().test(this, initialState = HomeUiState()) {
            containerHost.handleIntent(HomeIntent.CreateWishConfirmed("에어팟", 300_000L))

            expectNoItems()
        }
    }

    @Test
    fun `위시 수정에 성공하면 수정 완료 토스트를 띄운다`() = runTest {
        coEvery { updateWish(1L, "갤럭시버즈", 200_000L) } returns
            NeveraResult.Success(Wish(id = 1L, name = "갤럭시버즈", amount = 200_000L))

        createViewModel().test(this, initialState = HomeUiState()) {
            containerHost.handleIntent(HomeIntent.UpdateWishConfirmed(1L, "갤럭시버즈", 200_000L))

            expectSideEffect(HomeSideEffect.ShowWishUpdatedToast)
        }
    }

    @Test
    fun `위시 수정에 실패하면 토스트를 띄우지 않는다`() = runTest {
        coEvery { updateWish(1L, "갤럭시버즈", 200_000L) } returns
            NeveraResult.Failure(UpdateWishError.WishNotFound)

        createViewModel().test(this, initialState = HomeUiState()) {
            containerHost.handleIntent(HomeIntent.UpdateWishConfirmed(1L, "갤럭시버즈", 200_000L))

            expectNoItems()
        }
    }
}
