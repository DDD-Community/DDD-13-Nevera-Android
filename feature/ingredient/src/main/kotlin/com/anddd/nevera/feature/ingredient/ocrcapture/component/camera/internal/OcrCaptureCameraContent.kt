package com.anddd.nevera.feature.ingredient.ocrcapture.component.camera.internal

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
import com.anddd.nevera.feature.ingredient.ocrcapture.component.PermissionDeniedDialog
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureIntent

@Composable
internal fun OcrCaptureCameraContent(
    hasCameraPermission: Boolean,
    showPermissionDialog: Boolean,
    onIntent: (OcrCaptureIntent) -> Unit,
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
            onSwitchToGallery = { onIntent(OcrCaptureIntent.SwitchToGallery) },
            onTakePicture = { onIntent(OcrCaptureIntent.TakePicture) },
            onSwapCamera = { onIntent(OcrCaptureIntent.SwapCamera) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@ComposePreview(widthDp = 360, heightDp = 720)
@Composable
private fun OcrCaptureCameraContentPreview() {
    NeveraTheme {
        OcrCaptureCameraContent(
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
private fun OcrCaptureCameraContentPermissionDeniedPreview() {
    NeveraTheme {
        OcrCaptureCameraContent(
            hasCameraPermission = false,
            showPermissionDialog = true,
            onIntent = {},
            onBindCamera = { _, _ -> },
            onDismissPermissionDialog = {},
            onOpenSettings = {},
        )
    }
}
