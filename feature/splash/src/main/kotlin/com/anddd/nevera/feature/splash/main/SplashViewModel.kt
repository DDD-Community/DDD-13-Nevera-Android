package com.anddd.nevera.feature.splash.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.domain.usecase.CheckAutoLoginUseCase
import com.anddd.nevera.feature.splash.main.model.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkAutoLoginUseCase: CheckAutoLoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState

    init {
        checkAutoLogin(startTime = System.currentTimeMillis())
    }

    private fun checkAutoLogin(startTime: Long) {
        viewModelScope.launch {
            val userId = checkAutoLoginUseCase()
            val remaining = remainingDelay(startTime)
            if (remaining > 0) delay(remaining)
            _uiState.value = when {
                userId != null -> SplashUiState.NavigateToHome(userId)
                else -> SplashUiState.NavigateToLogin
            }
        }
    }

    private fun remainingDelay(startTime: Long): Long =
        MIN_SPLASH_DURATION_MS - (System.currentTimeMillis() - startTime)

    companion object {
        private const val MIN_SPLASH_DURATION_MS = 2000L
    }
}