package com.anddd.nevera.feature.sample.main.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface SampleSideEffect : NeveraSideEffect {
    data class ShowToast(val message: String) : SampleSideEffect
}
