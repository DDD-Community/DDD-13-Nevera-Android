package com.anddd.nevera.feature.splash.main

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.anddd.nevera.core.designsystem.component.dialog.NeveraConfirmDialog
import com.anddd.nevera.feature.splash.R
import timber.log.Timber

@Composable
internal fun NotificationPermissionRequester(
    onPermissionFlowCompleted: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var showRationaleDialog by rememberSaveable { mutableStateOf(false) }

    if (activity == null) {
        Timber.i("NotificationPermissionRequester는 Activity context가 필요합니다")
        LaunchedEffect(Unit) {
            onPermissionFlowCompleted()
        }
        return
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { onPermissionFlowCompleted() }

    if (showRationaleDialog) {
        NeveraConfirmDialog(
            title = stringResource(R.string.notification_permission_rationale_title),
            subtitle = stringResource(R.string.notification_permission_rationale_message),
            negative = stringResource(R.string.notification_permission_rationale_dismiss),
            positive = stringResource(R.string.notification_permission_rationale_confirm),
            onNegative = {
                showRationaleDialog = false
                onPermissionFlowCompleted()
            },
            onPositive = {
                showRationaleDialog = false
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        )
    }

    LaunchedEffect(Unit) {
        val shouldRequestNotificationPermission =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED

        if (shouldRequestNotificationPermission) {
            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.POST_NOTIFICATIONS,
            )

            if (shouldShowRationale) {
                showRationaleDialog = true
            } else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            onPermissionFlowCompleted()
        }
    }
}
