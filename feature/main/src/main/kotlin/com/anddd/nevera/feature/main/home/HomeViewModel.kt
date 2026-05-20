package com.anddd.nevera.feature.main.home

import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.feature.main.home.model.HomeIntent
import com.anddd.nevera.feature.main.home.model.HomeMutation
import com.anddd.nevera.feature.main.home.model.HomeSideEffect
import com.anddd.nevera.feature.main.home.model.HomeUiState
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
        applyMutation(HomeMutation.LoadComplete)
    }

    override suspend fun Syntax<HomeUiState, HomeSideEffect>.applyMutation(mutation: HomeMutation) {
        when (mutation) {
            HomeMutation.Loading -> reduce { state.copy(isLoading = true) }
            HomeMutation.LoadComplete -> reduce { state.copy(isLoading = false) }
        }
    }
}
