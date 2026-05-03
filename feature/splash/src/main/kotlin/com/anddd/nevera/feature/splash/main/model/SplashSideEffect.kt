package com.anddd.nevera.feature.splash.main.model

sealed interface SplashSideEffect {
    data class MoveToHome(val accessToken: String) : SplashSideEffect
    data object MoveToLogin : SplashSideEffect
}