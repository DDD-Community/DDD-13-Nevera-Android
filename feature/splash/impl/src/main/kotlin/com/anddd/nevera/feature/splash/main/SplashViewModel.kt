package com.anddd.nevera.feature.splash.main

import android.os.SystemClock
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.scheduler.FcmSyncScheduler
import com.anddd.nevera.domain.usecase.auth.CheckAutoLoginUseCase
import com.anddd.nevera.feature.splash.main.model.SplashIntent
import com.anddd.nevera.feature.splash.main.model.SplashMutation
import com.anddd.nevera.feature.splash.main.model.SplashSideEffect
import com.anddd.nevera.feature.splash.main.model.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkAutoLoginUseCase: CheckAutoLoginUseCase,
    private val fcmSyncScheduler: FcmSyncScheduler,
) : NeveraViewModel<SplashUiState, SplashSideEffect, SplashIntent, SplashMutation>(SplashUiState) {

    override fun handleIntent(intent: SplashIntent) {
        when (intent) {
            is SplashIntent.StartAutoLogin -> startAutoLogin(intent.startTime)
        }
    }

    private fun startAutoLogin(startTime: Long) = intent {
        val accessToken = checkAutoLoginUseCase()
        val remaining = remainingDelay(startTime)
        if (remaining > 0) delay(remaining)

        if (accessToken != null) {
            fcmSyncScheduler.scheduleSyncFcmToken()
            postSideEffect(SplashSideEffect.MoveToHome)
        } else {
            postSideEffect(SplashSideEffect.MoveToLogin)
        }
    }

    override suspend fun Syntax<SplashUiState, SplashSideEffect>.applyMutation(
        mutation: SplashMutation
    ) = Unit

    private fun remainingDelay(startTime: Long): Long =
        (MIN_SPLASH_DURATION_MS - (SystemClock.elapsedRealtime() - startTime)).coerceAtLeast(0L)

    companion object {
        private const val MIN_SPLASH_DURATION_MS = 2000L
    }
}