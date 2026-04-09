package com.anddd.nevera.feature.main.home.model

sealed interface HomeSideEffect {
    data object NavigateToLogin : HomeSideEffect
    data class ShowError(val message: String): HomeSideEffect
}
