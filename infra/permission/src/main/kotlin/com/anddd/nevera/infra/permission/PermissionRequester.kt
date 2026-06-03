package com.anddd.nevera.infra.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import timber.log.Timber

@Composable
fun PermissionRequester(
    permission: AppPermission,
    onGranted: () -> Unit,
    onDenied: () -> Unit,
    content: @Composable (onConfirm: () -> Unit, onDismiss: () -> Unit) -> Unit,
) {
    val context = LocalContext.current
    val activity = context.findActivity() ?: run {
        Timber.w("PermissionRequester requires Activity context")
        LaunchedEffect(Unit) { onDenied() }
        return
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            onGranted()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission.manifestPermission)) {
            // 첫 거부: content가 화면에 떠 있으므로 사용자가 재시도 가능
            Unit
        } else {
            context.openAppSettings()
            onDenied()
        }
    }

    content(
        { launcher.launch(permission.manifestPermission) },
        { onDenied() },
    )
}

private fun Context.openAppSettings() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }.let(::startActivity)
}
