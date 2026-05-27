package com.anddd.nevera.feature.ingredient.ocrcapture

import androidx.camera.core.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R
import com.anddd.nevera.feature.ingredient.ocrcapture.component.camera.internal.OcrCaptureCameraContent
import com.anddd.nevera.feature.ingredient.ocrcapture.component.gallery.internal.GalleryPartialAccessBanner
import com.anddd.nevera.feature.ingredient.ocrcapture.component.gallery.internal.OcrCaptureGalleryContent
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureIntent
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureMode
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureUiState

@Composable
internal fun OcrCaptureContent(
    uiState: OcrCaptureUiState,
    cameraPermissionState: PermissionState,
    galleryPermissionState: PermissionState,
    onIntent: (OcrCaptureIntent) -> Unit,
    onBindCamera: (LifecycleOwner, Preview.SurfaceProvider) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            NeveraAppBar(
                modifier = Modifier.background(Color.Black),
                title = stringResource(R.string.ocr_capture_scan_title),
                navigation = NeveraAppBarNavigation.Close(onClick = { onIntent(OcrCaptureIntent.Close) }),
                action = when (uiState.mode) {
                    OcrCaptureMode.Camera -> NeveraAppBarAction.None
                    OcrCaptureMode.Gallery -> NeveraAppBarAction.Icons.of(
                        NeveraAppBarAction.Icons.Item(
                            painter = NeveraIcons.OcrCaptureCameraWhite,
                            contentDescription = stringResource(R.string.ocr_capture_switch_to_camera),
                            onClick = { onIntent(OcrCaptureIntent.SwitchToCamera) },
                        )
                    )
                },
                showBackground = false,
            )
        },
    ) { innerPadding ->
        when (uiState.mode) {
            OcrCaptureMode.Camera -> OcrCaptureCameraContent(
                hasCameraPermission = cameraPermissionState.hasPermission,
                showPermissionDialog = cameraPermissionState.isDenied,
                onIntent = onIntent,
                onBindCamera = onBindCamera,
                onDismissPermissionDialog = cameraPermissionState.clearDenied,
                onOpenSettings = { onIntent(OcrCaptureIntent.OpenCameraSettings) },
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
            OcrCaptureMode.Gallery -> Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            ) {
                if (galleryPermissionState.isPartialAccess) {
                    GalleryPartialAccessBanner(onSelectMore = galleryPermissionState.requestPermission)
                }
                OcrCaptureGalleryContent(
                    images = uiState.galleryImages,
                    showPermissionDialog = galleryPermissionState.isDenied,
                    onIntent = onIntent,
                    onDismissPermissionDialog = galleryPermissionState.clearDenied,
                    onOpenSettings = { onIntent(OcrCaptureIntent.OpenGallerySettings) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@ComposePreview(name = "OcrCaptureContent - Camera Mode", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun OcrCaptureContentCameraPreview() {
    NeveraTheme {
        OcrCaptureContent(
            uiState = OcrCaptureUiState(mode = OcrCaptureMode.Camera),
            cameraPermissionState = PermissionState(
                hasPermission = true,
                isDenied = false,
                requestPermission = {},
                clearDenied = {},
            ),
            galleryPermissionState = PermissionState(
                hasPermission = false,
                isDenied = false,
                requestPermission = {},
                clearDenied = {},
            ),
            onIntent = {},
            onBindCamera = { _, _ -> },
        )
    }
}

@ComposePreview(name = "OcrCaptureContent - Gallery Mode", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun OcrCaptureContentGalleryPreview() {
    NeveraTheme {
        OcrCaptureContent(
            uiState = OcrCaptureUiState(mode = OcrCaptureMode.Gallery),
            cameraPermissionState = PermissionState(
                hasPermission = true,
                isDenied = false,
                requestPermission = {},
                clearDenied = {},
            ),
            galleryPermissionState = PermissionState(
                hasPermission = true,
                isDenied = false,
                requestPermission = {},
                clearDenied = {},
            ),
            onIntent = {},
            onBindCamera = { _, _ -> },
        )
    }
}

@ComposePreview(name = "OcrCaptureContent - Gallery Partial Access", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun OcrCaptureContentGalleryPartialAccessPreview() {
    NeveraTheme {
        OcrCaptureContent(
            uiState = OcrCaptureUiState(mode = OcrCaptureMode.Gallery),
            cameraPermissionState = PermissionState(
                hasPermission = true,
                isDenied = false,
                requestPermission = {},
                clearDenied = {},
            ),
            galleryPermissionState = PermissionState(
                hasPermission = true,
                isPartialAccess = true,
                isDenied = false,
                requestPermission = {},
                clearDenied = {},
            ),
            onIntent = {},
            onBindCamera = { _, _ -> },
        )
    }
}
