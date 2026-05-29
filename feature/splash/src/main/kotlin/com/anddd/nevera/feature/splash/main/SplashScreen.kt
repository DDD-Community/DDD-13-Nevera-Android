package com.anddd.nevera.feature.splash.main

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.designsystem.component.dialog.NeveraConfirmDialog
import com.anddd.nevera.feature.splash.R
import com.anddd.nevera.feature.splash.main.component.SplashContent
import com.anddd.nevera.feature.splash.main.model.SplashIntent
import com.anddd.nevera.feature.splash.main.model.SplashSideEffect
import com.anddd.nevera.infra.permission.AppPermission
import com.anddd.nevera.infra.permission.PermissionRequester
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val startAutoLogin = {
        val startTime = SystemClock.elapsedRealtime()
        viewModel.handleIntent(SplashIntent.StartAutoLogin(startTime))
    }

    PermissionRequester(
        permission = AppPermission.Notification,
        onGranted = startAutoLogin,
        onDenied = startAutoLogin,
    ) { onConfirm, onDismiss ->
        NeveraConfirmDialog(
            title = stringResource(R.string.notification_permission_rationale_title),
            subtitle = stringResource(R.string.notification_permission_rationale_message),
            negative = stringResource(R.string.notification_permission_rationale_dismiss),
            positive = stringResource(R.string.notification_permission_rationale_confirm),
            onNegative = onDismiss,
            onPositive = onConfirm,
        )
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is SplashSideEffect.MoveToHome -> onNavigateToHome()
            is SplashSideEffect.MoveToLogin -> onNavigateToLogin()
        }
    }

    SplashContent()
}
