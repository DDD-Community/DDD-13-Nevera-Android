package com.anddd.nevera.feature.splash.main.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface SplashIntent : NeveraIntent {
    data class StartAutoLogin(val startTime: Long) : SplashIntent
}