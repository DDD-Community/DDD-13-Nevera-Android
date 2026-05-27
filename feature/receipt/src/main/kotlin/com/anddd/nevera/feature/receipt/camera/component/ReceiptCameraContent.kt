package com.anddd.nevera.feature.receipt.camera.component

import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.receipt.main.component.PermissionDeniedDialog
import com.anddd.nevera.feature.receipt.main.model.ReceiptIntent

@Composable
internal fun ReceiptCameraContent(
    hasCameraPermission: Boolean,
    showPermissionDialog: Boolean,
    onIntent: (ReceiptIntent) -> Unit,
    onBindCamera: (LifecycleOwner, Preview.SurfaceProvider) -> Unit,
    onDismissPermissionDialog: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            if (hasCameraPermission) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).also { previewView ->
                            onBindCamera(lifecycleOwner, previewView.surfaceProvider)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                )
            }

            if (showPermissionDialog) {
                PermissionDeniedDialog(
                    onDismiss = onDismissPermissionDialog,
                    onOpenSettings = onOpenSettings,
                )
            }
        }

        CameraControls(
            hasCameraPermission = hasCameraPermission,
            onSwitchToGallery = { onIntent(ReceiptIntent.SwitchToGallery) },
            onTakePicture = { onIntent(ReceiptIntent.TakePicture) },
            onSwapCamera = { onIntent(ReceiptIntent.SwapCamera) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@ComposePreview(widthDp = 360, heightDp = 720)
@Composable
private fun ReceiptCameraContentPreview() {
    NeveraTheme {
        ReceiptCameraContent(
            hasCameraPermission = true,
            showPermissionDialog = false,
            onIntent = {},
            onBindCamera = { _, _ -> },
            onDismissPermissionDialog = {},
            onOpenSettings = {},
        )
    }
}

@ComposePreview(widthDp = 360, heightDp = 720, name = "Permission Denied")
@Composable
private fun ReceiptCameraContentPermissionDeniedPreview() {
    NeveraTheme {
        ReceiptCameraContent(
            hasCameraPermission = false,
            showPermissionDialog = true,
            onIntent = {},
            onBindCamera = { _, _ -> },
            onDismissPermissionDialog = {},
            onOpenSettings = {},
        )
    }
}