package com.anddd.nevera.feature.receipt.main

import android.net.Uri
import androidx.camera.core.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.anddd.nevera.feature.receipt.main.component.ReceiptCameraContent
import com.anddd.nevera.feature.receipt.main.component.ReceiptGalleryContent
import com.anddd.nevera.feature.receipt.main.model.ReceiptIntent
import com.anddd.nevera.feature.receipt.main.model.ReceiptMode

@Composable
internal fun ReceiptContent(
    mode: ReceiptMode,
    galleryImages: List<Uri>,
    cameraPermissionState: PermissionState,
    galleryPermissionState: PermissionState,
    onClose: () -> Unit,
    onIntent: (ReceiptIntent) -> Unit,
    onBindCamera: (LifecycleOwner, Preview.SurfaceProvider) -> Unit,
    onCameraOpenSettings: () -> Unit,
    onGalleryOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        NeveraAppBar(
            modifier = Modifier.background(Color.Black),
            title = stringResource(R.string.receipt_scan_title),
            navigation = NeveraAppBarNavigation.Close(onClick = onClose),
            action = when (mode) {
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

        when (mode) {
            ReceiptMode.Camera -> ReceiptCameraContent(
                hasCameraPermission = cameraPermissionState.hasPermission,
                showPermissionDialog = cameraPermissionState.isDenied,
                onIntent = onIntent,
                onBindCamera = onBindCamera,
                onDismissPermissionDialog = cameraPermissionState.clearDenied,
                onOpenSettings = onCameraOpenSettings,
                modifier = Modifier.weight(1f),
            )
            ReceiptMode.Gallery -> ReceiptGalleryContent(
                images = galleryImages,
                hasGalleryPermission = galleryPermissionState.hasPermission,
                showPermissionDialog = galleryPermissionState.isDenied,
                onIntent = onIntent,
                onDismissPermissionDialog = galleryPermissionState.clearDenied,
                onOpenSettings = onGalleryOpenSettings,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@ComposePreview(name = "ReceiptContent - Camera Mode", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun ReceiptContentCameraPreview() {
    NeveraTheme {
        ReceiptContent(
            mode = ReceiptMode.Camera,
            galleryImages = emptyList(),
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
            onClose = {},
            onIntent = {},
            onBindCamera = { _, _ -> },
            onCameraOpenSettings = {},
            onGalleryOpenSettings = {},
        )
    }
}

@ComposePreview(name = "ReceiptContent - Gallery Mode", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun ReceiptContentGalleryPreview() {
    NeveraTheme {
        ReceiptContent(
            mode = ReceiptMode.Gallery,
            galleryImages = emptyList(),
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
            onClose = {},
            onIntent = {},
            onBindCamera = { _, _ -> },
            onCameraOpenSettings = {},
            onGalleryOpenSettings = {},
        )
    }
}