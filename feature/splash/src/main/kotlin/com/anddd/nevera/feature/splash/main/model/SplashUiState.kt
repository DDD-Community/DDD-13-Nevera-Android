package com.anddd.nevera.feature.splash.main.model

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data object NavigateToLogin : SplashUiState
    data class NavigateToHome(val accessToken: String) : SplashUiState
}
