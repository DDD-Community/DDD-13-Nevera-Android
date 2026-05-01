package com.anddd.nevera.feature.splash.main

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.splash.R

@Composable
internal fun NotificationPermissionRationaleDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.notification_permission_rationale_title)) },
        text = {
            Text(stringResource(R.string.notification_permission_rationale_message))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.notification_permission_rationale_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.notification_permission_rationale_dismiss))
            }
        },
    )
}

@Preview
@Composable
private fun NotificationPermissionRationaleDialogPreview() {
    NeveraTheme {
        NotificationPermissionRationaleDialog(
            onConfirm = {},
            onDismiss = {}
        )
    }
}