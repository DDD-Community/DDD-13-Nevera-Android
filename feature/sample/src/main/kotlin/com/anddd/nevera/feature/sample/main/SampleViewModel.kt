package com.anddd.nevera.feature.sample.main

import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.feature.sample.main.model.SampleIntent
import com.anddd.nevera.feature.sample.main.model.SampleSideEffect
import com.anddd.nevera.feature.sample.main.model.SampleUiState
import com.anddd.nevera.feature.sample.main.model.SimpleMutation
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(

) : NeveraViewModel<SampleUiState, SampleSideEffect, SampleIntent, SimpleMutation>(SampleUiState()) {

    override fun handleIntent(intent: SampleIntent) {
        when (intent) {
            SampleIntent.ClickButton -> onClickButton()
        }
    }

    private fun onClickButton() = intent {
        applyMutation(SimpleMutation.IncrementCount)
        postSideEffect(SampleSideEffect.ShowToast("클릭: ${state.count}"))
    }

    override suspend fun Syntax<SampleUiState, SampleSideEffect>.applyMutation(mutation: SimpleMutation) {
        when (mutation) {
            SimpleMutation.IncrementCount -> reduce { state.copy(count = state.count + 1) }
        }
    }
}
