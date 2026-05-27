package com.anddd.nevera.feature.receipt.main

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
import com.anddd.nevera.feature.receipt.R
import com.anddd.nevera.feature.receipt.camera.component.ReceiptCameraContent
import com.anddd.nevera.feature.receipt.gallery.component.GalleryPartialAccessBanner
import com.anddd.nevera.feature.receipt.gallery.component.ReceiptGalleryContent
import com.anddd.nevera.feature.receipt.main.model.ReceiptIntent
import com.anddd.nevera.feature.receipt.main.model.ReceiptMode
import com.anddd.nevera.feature.receipt.main.model.ReceiptUiState

@Composable
internal fun ReceiptContent(
    uiState: ReceiptUiState,
    cameraPermissionState: PermissionState,
    galleryPermissionState: PermissionState,
    onIntent: (ReceiptIntent) -> Unit,
    onBindCamera: (LifecycleOwner, Preview.SurfaceProvider) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            NeveraAppBar(
                modifier = Modifier.background(Color.Black),
                title = stringResource(R.string.receipt_scan_title),
                navigation = NeveraAppBarNavigation.Close(onClick = { onIntent(ReceiptIntent.Close) }),
                action = when (uiState.mode) {
                    ReceiptMode.Camera -> NeveraAppBarAction.None
                    ReceiptMode.Gallery -> NeveraAppBarAction.Icons.of(
                        NeveraAppBarAction.Icons.Item(
                            painter = NeveraIcons.ReceiptCameraWhite,
                            contentDescription = stringResource(R.string.receipt_switch_to_camera),
                            onClick = { onIntent(ReceiptIntent.SwitchToCamera) },
                        )
                    )
                },
                showBackground = false,
            )
        },
    ) { innerPadding ->
        when (uiState.mode) {
            ReceiptMode.Camera -> ReceiptCameraContent(
                hasCameraPermission = cameraPermissionState.hasPermission,
                showPermissionDialog = cameraPermissionState.isDenied,
                onIntent = onIntent,
                onBindCamera = onBindCamera,
                onDismissPermissionDialog = cameraPermissionState.clearDenied,
                onOpenSettings = { onIntent(ReceiptIntent.OpenCameraSettings) },
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
            ReceiptMode.Gallery -> Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            ) {
                if (galleryPermissionState.isPartialAccess) {
                    GalleryPartialAccessBanner(onSelectMore = galleryPermissionState.requestPermission)
                }
                ReceiptGalleryContent(
                    images = uiState.galleryImages,
                    showPermissionDialog = galleryPermissionState.isDenied,
                    onIntent = onIntent,
                    onDismissPermissionDialog = galleryPermissionState.clearDenied,
                    onOpenSettings = { onIntent(ReceiptIntent.OpenGallerySettings) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@ComposePreview(name = "ReceiptContent - Camera Mode", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun ReceiptContentCameraPreview() {
    NeveraTheme {
        ReceiptContent(
            uiState = ReceiptUiState(mode = ReceiptMode.Camera),
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

@ComposePreview(name = "ReceiptContent - Gallery Mode", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun ReceiptContentGalleryPreview() {
    NeveraTheme {
        ReceiptContent(
            uiState = ReceiptUiState(mode = ReceiptMode.Gallery),
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

@ComposePreview(name = "ReceiptContent - Gallery Partial Access", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun ReceiptContentGalleryPartialAccessPreview() {
    NeveraTheme {
        ReceiptContent(
            uiState = ReceiptUiState(mode = ReceiptMode.Gallery),
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
