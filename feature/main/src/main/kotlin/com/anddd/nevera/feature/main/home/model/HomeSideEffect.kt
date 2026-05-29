package com.anddd.nevera.feature.main.home.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface HomeSideEffect : NeveraSideEffect {
    data class ShowError(val message: String) : HomeSideEffect
    data object ShowCaptureModeBottomSheet : HomeSideEffect
    data object ShowWishCreatedToast : HomeSideEffect
    data object ShowWishUpdatedToast : HomeSideEffect
}
