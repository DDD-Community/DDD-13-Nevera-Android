package com.anddd.nevera.feature.ingredient.ocrcapture

import androidx.camera.core.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.lifecycle.LifecycleOwner
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBar
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarAction
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarNavigation
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R
import com.anddd.nevera.feature.ingredient.ocrcapture.component.camera.internal.OcrCaptureCameraView
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureIntent
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureUiState

@Composable
internal fun OcrCaptureContent(
    uiState: OcrCaptureUiState,
    onIntent: (OcrCaptureIntent) -> Unit,
    onBindCamera: (LifecycleOwner, Preview.SurfaceProvider) -> Unit,
    onDismissPermissionDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Black,
        topBar = {
            NeveraAppBar(
                modifier = Modifier.background(Color.Black),
                title = stringResource(R.string.ocr_capture_scan_title),
                navigation = NeveraAppBarNavigation.Close(onClick = { onIntent(OcrCaptureIntent.Close) }),
                action = NeveraAppBarAction.None,
                showBackground = false,
            )
        },
    ) { innerPadding ->
        OcrCaptureCameraView(
            hasCameraPermission = uiState.hasCameraPermission,
            showPermissionDialog = uiState.showPermissionDialog,
            onIntent = onIntent,
            onBindCamera = onBindCamera,
            onDismissPermissionDialog = onDismissPermissionDialog,
            onOpenSettings = { onIntent(OcrCaptureIntent.OpenCameraSettings) },
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        )
    }
}

@ComposePreview(name = "OcrCaptureContent - Camera Mode", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun OcrCaptureContentCameraPreview() {
    NeveraTheme {
        OcrCaptureContent(
            uiState = OcrCaptureUiState(hasCameraPermission = true, showPermissionDialog = false),
            onIntent = {},
            onBindCamera = { _, _ -> },
            onDismissPermissionDialog = {},
        )
    }
}

@ComposePreview(name = "OcrCaptureContent - Camera Permission Denied", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun OcrCaptureContentCameraPermissionDeniedPreview() {
    NeveraTheme {
        OcrCaptureContent(
            uiState = OcrCaptureUiState(hasCameraPermission = false, showPermissionDialog = true),
            onIntent = {},
            onBindCamera = { _, _ -> },
            onDismissPermissionDialog = {},
        )
    }
}
