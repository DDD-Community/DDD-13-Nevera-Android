package com.anddd.nevera.feature.ingredient.ocrcapture.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonColor
import com.anddd.nevera.core.designsystem.component.dialog.NeveraConfirmDialog
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R

@Composable
internal fun PermissionDeniedDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    NeveraConfirmDialog(
        title = stringResource(R.string.ocr_capture_permission_dialog_title),
        subtitle = stringResource(R.string.ocr_capture_permission_dialog_subtitle),
        positive = stringResource(R.string.ocr_capture_permission_dialog_positive),
        negative = stringResource(R.string.ocr_capture_permission_dialog_negative),
        onPositive = onOpenSettings,
        onNegative = onDismiss,
        positiveButtonColor = NeveraButtonColor.Primary,
        negativeButtonColor = NeveraButtonColor.Secondary,
    )
}

@Preview(
    name = "PermissionDeniedDialog",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun PermissionDeniedDialogPreview() {
    NeveraTheme {
        PermissionDeniedDialog(
            onDismiss = {},
            onOpenSettings = {},
        )
    }
}
