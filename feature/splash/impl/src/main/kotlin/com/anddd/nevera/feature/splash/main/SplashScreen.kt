package com.anddd.nevera.feature.splash.main

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.feature.splash.main.component.SplashContent
import com.anddd.nevera.feature.splash.main.model.SplashIntent
import com.anddd.nevera.feature.splash.main.model.SplashSideEffect
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        val startTime = SystemClock.elapsedRealtime()
        viewModel.handleIntent(SplashIntent.StartAutoLogin(startTime))
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is SplashSideEffect.MoveToHome -> onNavigateToHome()
            is SplashSideEffect.MoveToLogin -> onNavigateToLogin()
        }
    }

    SplashContent()
}
