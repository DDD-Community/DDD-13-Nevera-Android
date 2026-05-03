package com.anddd.nevera.feature.splash.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.domain.scheduler.FcmSyncScheduler
import com.anddd.nevera.domain.usecase.auth.CheckAutoLoginUseCase
import com.anddd.nevera.feature.splash.main.model.SplashSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkAutoLoginUseCase: CheckAutoLoginUseCase,
    private val fcmSyncScheduler: FcmSyncScheduler,
) : ViewModel() {

    private val _sideEffect = Channel<SplashSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun startAutoLogin(startTime: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            val accessToken = checkAutoLoginUseCase()
            val remaining = remainingDelay(startTime)
            if (remaining > 0) delay(remaining)

            if (accessToken != null) {
                fcmSyncScheduler.scheduleSyncFcmToken()
                _sideEffect.send(SplashSideEffect.MoveToHome(accessToken))
            } else {
                _sideEffect.send(SplashSideEffect.MoveToLogin)
            }
        }
    }

    private fun remainingDelay(startTime: Long): Long =
        MIN_SPLASH_DURATION_MS - (System.currentTimeMillis() - startTime)

    companion object {
        private const val MIN_SPLASH_DURATION_MS = 2000L
    }
}
