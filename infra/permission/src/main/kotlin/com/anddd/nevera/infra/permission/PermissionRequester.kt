package com.anddd.nevera.infra.permission

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import timber.log.Timber

@Composable
fun PermissionRequester(
    permission: AppPermission,
    checker: PermissionChecker = DefaultPermissionChecker,
    onGranted: () -> Unit,
    onDenied: () -> Unit,
    rationaleContent: @Composable (onConfirm: () -> Unit, onDismiss: () -> Unit) -> Unit,
) {
    val context = LocalContext.current
    val activity = context.findActivity() ?: run {
        Timber.w("PermissionRequester requires Activity context")
        LaunchedEffect(Unit) { onDenied() }
        return
    }

    var showRationale by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted -> if (isGranted) onGranted() else onDenied() }

    // 화면 진입 시 1회만 실행되는 초기 권한 요청 플로우.
    // 외부에서 권한 상태가 변경되는 경우(앱 설정 복귀 등)는 이 Composable의 대상이 아님.
    LaunchedEffect(Unit) {
        when (checker.checkState(activity, permission)) {
            PermissionState.Granted -> onGranted()
            PermissionState.DeniedWithRationale -> showRationale = true
            PermissionState.Denied -> launcher.launch(permission.manifestPermission)
        }
    }

    if (showRationale) {
        val onConfirm = {
            showRationale = false
            launcher.launch(permission.manifestPermission)
        }
        val onDismiss = {
            showRationale = false
            onDenied()
        }
        rationaleContent(onConfirm, onDismiss)
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}