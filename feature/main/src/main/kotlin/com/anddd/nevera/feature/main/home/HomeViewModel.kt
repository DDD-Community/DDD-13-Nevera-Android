package com.anddd.nevera.feature.main.home

import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.feature.main.home.model.HomeIntent
import com.anddd.nevera.feature.main.home.model.HomeMutation
import com.anddd.nevera.feature.main.home.model.HomeSideEffect
import com.anddd.nevera.feature.main.home.model.HomeUiState
import com.anddd.nevera.feature.main.home.model.WishUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
) : NeveraViewModel<HomeUiState, HomeSideEffect, HomeIntent, HomeMutation>(HomeUiState()) {

    init {
        load()
    }

    override fun handleIntent(action: HomeIntent) {}

    private fun load() = intent {
        applyMutation(HomeMutation.Loading)
        delay(500L)
        // TODO: UseCase로 초기 데이터 로드
        applyMutation(
            HomeMutation.LoadComplete(
                wishUiModel = WishUiModel(
                    nickname = "김푸드",
                    wish = "제주도 여행",
                    savedMoney = 12000,
                    goalMoney = 480000,
                )
            )
        )
    }

    override suspend fun Syntax<HomeUiState, HomeSideEffect>.applyMutation(mutation: HomeMutation) {
        when (mutation) {
            HomeMutation.Loading -> reduce { state.copy(isLoading = true) }
            is HomeMutation.LoadComplete -> reduce {
                state.copy(isLoading = false, wishUiModel = mutation.wishUiModel)
            }
        }
    }
}
