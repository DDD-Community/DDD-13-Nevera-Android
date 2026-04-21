package com.anddd.nevera.feature.splash.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.domain.model.notification.logFcmSyncFailure
import com.anddd.nevera.domain.usecase.auth.CheckAutoLoginUseCase
import com.anddd.nevera.domain.usecase.notification.SyncFcmTokenUseCase
import com.anddd.nevera.feature.splash.BuildConfig
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
    private val syncFcmTokenUseCase: SyncFcmTokenUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState

    init {
        checkAutoLogin(startTime = System.currentTimeMillis())
    }

    private fun checkAutoLogin(startTime: Long) {
        viewModelScope.launch {
            val accessToken = checkAutoLoginUseCase()
            val remaining = remainingDelay(startTime)
            if (remaining > 0) delay(remaining)

            _uiState.value = if (accessToken != null) {
                syncFcmToken()  // 로그인 상태일 때만 동기화 시도 (실패해도 네비게이션 진행)
                SplashUiState.NavigateToHome(accessToken)
            } else {
                SplashUiState.NavigateToLogin
            }
        }
    }

    private suspend fun syncFcmToken() {
        runCatching {
            syncFcmTokenUseCase()
        }.onSuccess { result ->
            result.logFcmSyncFailure(TAG, BuildConfig.DEBUG, Log::w)
        }.onFailure { throwable ->
            if (BuildConfig.DEBUG) {
                Log.e(TAG, throwable.message, throwable)
            }
        }
    }

    private fun remainingDelay(startTime: Long): Long =
        MIN_SPLASH_DURATION_MS - (System.currentTimeMillis() - startTime)

    companion object {
        private const val MIN_SPLASH_DURATION_MS = 2000L
        private const val TAG = "SplashViewModel"
    }
}
