package com.anddd.nevera.feature.fridge.main.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface FridgeSideEffect : NeveraSideEffect {
    data class ShowToast(val message: String) : FridgeSideEffect
}
