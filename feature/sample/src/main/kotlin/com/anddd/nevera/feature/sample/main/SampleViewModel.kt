package com.anddd.nevera.feature.sample.main

import androidx.lifecycle.ViewModel
import com.anddd.nevera.feature.sample.main.model.SampleSideEffect
import com.anddd.nevera.feature.sample.main.model.SampleUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(

) : ViewModel(), ContainerHost<SampleUiState, SampleSideEffect> {

    override val container = container<SampleUiState, SampleSideEffect>(
        initialState = SampleUiState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                Timber.e(throwable)
            }
        })

    fun onButtonClick() = intent {
        reduce { state.copy(count = state.count + 1) }
        postSideEffect(SampleSideEffect.ShowToast("클릭: ${state.count}"))
    }
}
