package com.anddd.nevera.feature.main.home

import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.model.home.HomeSummary
import com.anddd.nevera.domain.usecase.home.GetHomeSummaryUseCase
import com.anddd.nevera.domain.usecase.ingredient.GetDisposedIngredientsUseCase
import com.anddd.nevera.domain.usecase.ingredient.GetRescuedIngredientsUseCase
import com.anddd.nevera.domain.usecase.notification.MarkAllNotificationsAsReadUseCase
import com.anddd.nevera.domain.usecase.notification.ObserveUnreadNotificationUseCase
import com.anddd.nevera.domain.usecase.user.GetOnboardingStatusUseCase
import com.anddd.nevera.domain.usecase.user.UpdateNicknameUseCase
import com.anddd.nevera.domain.usecase.wish.CreateWishUseCase
import com.anddd.nevera.domain.usecase.wish.UpdateWishUseCase
import com.anddd.nevera.feature.main.home.model.HomeIntent
import com.anddd.nevera.feature.main.home.model.HomeMutation
import com.anddd.nevera.feature.main.home.model.HomeProfileUiModel
import com.anddd.nevera.feature.main.home.model.HomeSavingsUiModel
import com.anddd.nevera.feature.main.home.model.HomeSideEffect
import com.anddd.nevera.feature.main.home.model.HomeUiState
import com.anddd.nevera.feature.main.home.model.HomeWishUiModel
import com.anddd.nevera.feature.main.home.model.IngredientFilterTab
import com.anddd.nevera.feature.main.home.model.PaginatedListState
import com.anddd.nevera.feature.main.home.model.appendPage
import com.anddd.nevera.feature.main.home.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeSummary: GetHomeSummaryUseCase,
    private val getRescuedIngredients: GetRescuedIngredientsUseCase,
    private val getDisposedIngredients: GetDisposedIngredientsUseCase,
    private val updateNickname: UpdateNicknameUseCase,
    private val getOnboardingStatus: GetOnboardingStatusUseCase,
    private val createWish: CreateWishUseCase,
    private val updateWish: UpdateWishUseCase,
    private val observeUnreadNotification: ObserveUnreadNotificationUseCase,
    private val markAllNotificationsAsRead: MarkAllNotificationsAsReadUseCase,
) : NeveraViewModel<HomeUiState, HomeSideEffect, HomeIntent, HomeMutation>(HomeUiState()) {

    private companion object {
        const val INGREDIENT_PAGINATION_LIMIT = 10
    }

    private val loadMoreMutex = Mutex()

    init {
        observeBadge()
        load()
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.RecentIngredientTabClick -> onRecentIngredientTabClick(intent.tab)

            HomeIntent.AddIngredientClick -> onAddIngredientClick()

            is HomeIntent.LoadMoreIngredients -> loadMoreIngredients(intent.tab)

            is HomeIntent.UpdateNicknameClick -> onConfirmNickname(intent.nickname)

            HomeIntent.CreateWishClick -> onGreetingCreateWishClick()

            is HomeIntent.CreateWishConfirmed -> onCreateWishConfirmed(intent.name, intent.goalAmount)

            HomeIntent.WishEditClick -> onWishEditClick()

            is HomeIntent.UpdateWishConfirmed -> onUpdateWishConfirmed(intent.id, intent.name, intent.goalAmount)

            HomeIntent.NotificationIconClicked -> onNotificationIconClick()
        }
    }

    private fun observeBadge() = intent {
        observeUnreadNotification().collect { hasUnread ->
            applyMutation(HomeMutation.BadgeUpdated(hasUnread))
        }
    }

    private fun onNotificationIconClick() = intent {
        markAllNotificationsAsRead()
        postSideEffect(HomeSideEffect.NavigateToNotification)
    }

    private fun load() = intent {
        applyMutation(HomeMutation.Loading)

        val (tripleResult, onboardingResult) = coroutineScope {
            val summaryDeferred = async { getHomeSummary() }
            val rescuedDeferred = async { getRescuedIngredients(limit = INGREDIENT_PAGINATION_LIMIT) }
            val disposalDeferred = async { getDisposedIngredients(limit = INGREDIENT_PAGINATION_LIMIT) }
            val onboardingDeferred = async { getOnboardingStatus() }
            Pair(
                Triple(summaryDeferred.await(), rescuedDeferred.await(), disposalDeferred.await()),
                onboardingDeferred.await(),
            )
        }
        val (summaryResult, rescuedResult, disposalResult) = tripleResult

        onboardingResult
            .onSuccess { status ->
                if (!status.isCompleteOnboarding) {
                    postSideEffect(HomeSideEffect.ShowSetNicknameBottomSheet)
                }
            }
            .onFailure {
                // TODO 네트워크 에러 처리
            }

        summaryResult
            .onSuccess { summary -> applyHomeSummary(summary) }
            .onFailure {
                // TODO 네트워크 에러 처리
            }

        rescuedResult
            .onSuccess { ingredients ->
                applyMutation(
                    HomeMutation.ShowRescuedIngredients(
                        ingredients = ingredients.toUiModel(),
                        hasMore = ingredients.size == INGREDIENT_PAGINATION_LIMIT,
                    )
                )
            }
            .onFailure {
                // TODO 네트워크 에러 처리
            }

        disposalResult
            .onSuccess { ingredients ->
                applyMutation(
                    HomeMutation.ShowDisposalIngredients(
                        ingredients = ingredients.toUiModel(),
                        hasMore = ingredients.size == INGREDIENT_PAGINATION_LIMIT,
                    )
                )
            }
            .onFailure {
                // TODO 네트워크 에러 처리
            }

        applyMutation(HomeMutation.LoadComplete)
    }

    private fun onAddIngredientClick() = intent {
        postSideEffect(HomeSideEffect.ShowCaptureModeBottomSheet)
    }

    private fun loadMoreIngredients(tab: IngredientFilterTab) = intent {
        if (!loadMoreMutex.tryLock()) return@intent
        try {
            when (tab) {
                IngredientFilterTab.Rescue -> {
                    val paginatedState = state.rescuedIngredients
                    if (paginatedState.isLoadingMore || !paginatedState.hasMore) return@intent

                    applyMutation(HomeMutation.LoadingMoreRescuedIngredients)

                    getRescuedIngredients(offset = paginatedState.currentOffset, limit = INGREDIENT_PAGINATION_LIMIT)
                        .onSuccess { ingredients ->
                            applyMutation(
                                HomeMutation.AppendRescuedIngredients(
                                    ingredients = ingredients.toUiModel(),
                                    hasMore = ingredients.size == INGREDIENT_PAGINATION_LIMIT,
                                )
                            )
                        }
                        .onFailure {
                            // TODO 네트워크 에러 처리
                        }
                }

                IngredientFilterTab.Disposal -> {
                    val paginatedState = state.disposalIngredients
                    if (paginatedState.isLoadingMore || !paginatedState.hasMore) return@intent

                    applyMutation(HomeMutation.LoadingMoreDisposalIngredients)

                    getDisposedIngredients(offset = paginatedState.currentOffset, limit = INGREDIENT_PAGINATION_LIMIT)
                        .onSuccess { ingredients ->
                            applyMutation(
                                HomeMutation.AppendDisposalIngredients(
                                    ingredients = ingredients.toUiModel(),
                                    hasMore = ingredients.size == INGREDIENT_PAGINATION_LIMIT,
                                )
                            )
                        }
                        .onFailure {
                            // TODO 네트워크 에러 처리
                        }
                }
            }
        } finally {
            loadMoreMutex.unlock()
        }
    }

    private fun onRecentIngredientTabClick(tab: IngredientFilterTab) = intent {
        applyMutation(HomeMutation.SetRecentIngredientFilterTab(tab))
    }

    private fun onConfirmNickname(nickname: String) = intent {
        updateNickname(nickname)
            .onSuccess { profile ->
                applyMutation(HomeMutation.UpdateNickname(profile.nickname))
                postSideEffect(HomeSideEffect.ShowGreetingBottomSheet)
            }
            .onFailure {
                // TODO: 닉네임 업데이트 에러 처리
            }
    }

    private fun onGreetingCreateWishClick() = intent {
        postSideEffect(HomeSideEffect.ShowCreateWishBottomSheet)
    }

    private fun onCreateWishConfirmed(name: String, goalAmount: Long) = intent {
        createWish(name, goalAmount)
            .onSuccess {
                getHomeSummary().onSuccess { summary -> applyHomeSummary(summary) }
                postSideEffect(HomeSideEffect.ShowWishCreatedToast)
            }
            .onFailure {
                // TODO: 에러 처리
            }
    }

    private fun onWishEditClick() = intent {
        postSideEffect(HomeSideEffect.ShowUpdateWishBottomSheet)
    }

    private fun onUpdateWishConfirmed(id: Long, name: String, goalAmount: Long) = intent {
        updateWish(id, name, goalAmount)
            .onSuccess {
                getHomeSummary().onSuccess { summary -> applyHomeSummary(summary) }
                postSideEffect(HomeSideEffect.ShowWishUpdatedToast)
            }
            .onFailure {
                // TODO: 에러 처리
            }
    }

    override suspend fun Syntax<HomeUiState, HomeSideEffect>.applyMutation(mutation: HomeMutation) {
        when (mutation) {
            HomeMutation.Loading -> reduce { state.copy(isLoading = true) }

            HomeMutation.LoadComplete -> reduce { state.copy(isLoading = false) }

            is HomeMutation.SetRecentIngredientFilterTab -> reduce {
                state.copy(ingredientFilterTab = mutation.tab)
            }

            is HomeMutation.ShowProfile -> reduce { state.copy(profile = mutation.profile) }

            is HomeMutation.ShowWish -> reduce { state.copy(wish = mutation.wish) }

            HomeMutation.ShowEmptyWish -> reduce { state.copy(wish = null) }

            is HomeMutation.ShowSavings -> reduce { state.copy(savings = mutation.savings) }

            is HomeMutation.ShowRescuedIngredients -> reduce {
                state.copy(
                    rescuedIngredients = PaginatedListState(
                        items = mutation.ingredients,
                        hasMore = mutation.hasMore,
                        currentOffset = mutation.ingredients.size,
                    )
                )
            }

            HomeMutation.LoadingMoreRescuedIngredients -> reduce {
                state.copy(rescuedIngredients = state.rescuedIngredients.copy(isLoadingMore = true))
            }

            is HomeMutation.AppendRescuedIngredients -> reduce {
                state.copy(
                    rescuedIngredients = state.rescuedIngredients.appendPage(
                        newItems = mutation.ingredients,
                        hasMore = mutation.hasMore,
                    )
                )
            }

            is HomeMutation.ShowDisposalIngredients -> reduce {
                state.copy(
                    disposalIngredients = PaginatedListState(
                        items = mutation.ingredients,
                        hasMore = mutation.hasMore,
                        currentOffset = mutation.ingredients.size,
                    )
                )
            }

            HomeMutation.LoadingMoreDisposalIngredients -> reduce {
                state.copy(disposalIngredients = state.disposalIngredients.copy(isLoadingMore = true))
            }

            is HomeMutation.AppendDisposalIngredients -> reduce {
                state.copy(
                    disposalIngredients = state.disposalIngredients.appendPage(
                        newItems = mutation.ingredients,
                        hasMore = mutation.hasMore,
                    )
                )
            }

            is HomeMutation.UpdateNickname -> reduce {
                state.copy(profile = state.profile.copy(nickname = mutation.nickname))
            }

            is HomeMutation.BadgeUpdated -> reduce {
                state.copy(hasUnreadNotification = mutation.hasUnread)
            }
        }
    }

    private suspend fun Syntax<HomeUiState, HomeSideEffect>.applyHomeSummary(summary: HomeSummary) {
        applyMutation(HomeMutation.ShowProfile(HomeProfileUiModel(summary.nickname)))
        val wishMutation = summary.wish?.let { wish ->
            HomeMutation.ShowWish(wish.toUiModel())
        } ?: HomeMutation.ShowEmptyWish
        applyMutation(wishMutation)
        applyMutation(
            HomeMutation.ShowSavings(
                HomeSavingsUiModel(
                    rescuedAmount = summary.rescuedAmount,
                    disposalAmount = summary.disposalAmount,
                )
            )
        )
    }
}
