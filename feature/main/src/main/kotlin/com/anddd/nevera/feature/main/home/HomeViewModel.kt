package com.anddd.nevera.feature.main.home

import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.usecase.home.GetHomeSummaryUseCase
import com.anddd.nevera.domain.usecase.ingredient.GetDisposedIngredientsUseCase
import com.anddd.nevera.domain.usecase.ingredient.GetRescuedIngredientsUseCase
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
) : NeveraViewModel<HomeUiState, HomeSideEffect, HomeIntent, HomeMutation>(HomeUiState()) {

    private companion object {
        const val INGREDIENT_PAGINATION_LIMIT = 10
    }

    private val loadMoreMutex = Mutex()

    init {
        load()
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.RecentIngredientTabClick -> onRecentIngredientTabClick(intent.tab)
            HomeIntent.AddIngredientClick -> Unit // TODO: 식재료 추가 화면 이동
            is HomeIntent.LoadMoreIngredients -> loadMoreIngredients(intent.tab)
        }
    }

    private fun load() = intent {
        applyMutation(HomeMutation.Loading)

        val (summaryResult, rescuedResult, disposalResult) = coroutineScope {
            val summaryDeferred = async { getHomeSummary() }
            val rescuedDeferred = async { getRescuedIngredients(limit = INGREDIENT_PAGINATION_LIMIT) }
            val disposalDeferred = async { getDisposedIngredients(limit = INGREDIENT_PAGINATION_LIMIT) }
            Triple(summaryDeferred.await(), rescuedDeferred.await(), disposalDeferred.await())
        }

        summaryResult
            .onSuccess { summary ->
                applyMutation(HomeMutation.ShowProfile(HomeProfileUiModel(summary.nickname)))
                val wishMutation = summary.wish?.let { wish ->
                    HomeMutation.ShowWish(
                        HomeWishUiModel(
                            name = wish.name,
                            goalAmount = wish.goalAmount,
                            accumulatedAmount = wish.accumulatedAmount,
                            remainingAmount = wish.remainingAmount,
                            isAchieved = wish.isAchieved,
                        )
                    )
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
        }
    }
}
