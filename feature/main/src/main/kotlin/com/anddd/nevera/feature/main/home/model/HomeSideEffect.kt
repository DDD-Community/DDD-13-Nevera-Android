package com.anddd.nevera.feature.main.home.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface HomeSideEffect : NeveraSideEffect {
    data class ShowError(val message: String) : HomeSideEffect
    data object ShowCaptureModeDialog : HomeSideEffect
}
