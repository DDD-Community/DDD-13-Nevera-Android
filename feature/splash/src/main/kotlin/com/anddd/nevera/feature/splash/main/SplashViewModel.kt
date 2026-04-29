package com.anddd.nevera.feature.splash.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.domain.scheduler.FcmSyncScheduler
import com.anddd.nevera.domain.usecase.auth.CheckAutoLoginUseCase
import com.anddd.nevera.feature.splash.main.model.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkAutoLoginUseCase: CheckAutoLoginUseCase,
    private val fcmSyncScheduler: FcmSyncScheduler,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState

    fun startAutoLogin(startTime: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            val accessToken = checkAutoLoginUseCase()
            val remaining = remainingDelay(startTime)
            if (remaining > 0) delay(remaining)

            _uiState.value = if (accessToken != null) {
                fcmSyncScheduler.scheduleSyncFcmToken()
                SplashUiState.NavigateToHome(accessToken)
            } else {
                SplashUiState.NavigateToLogin
            }
        }
    }

    private fun remainingDelay(startTime: Long): Long =
        MIN_SPLASH_DURATION_MS - (System.currentTimeMillis() - startTime)

    companion object {
        private const val MIN_SPLASH_DURATION_MS = 2000L
    }
}
