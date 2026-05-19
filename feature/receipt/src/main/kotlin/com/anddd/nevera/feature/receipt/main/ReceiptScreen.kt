package com.anddd.nevera.feature.receipt.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.anddd.nevera.feature.receipt.main.model.ReceiptIntent
import com.anddd.nevera.feature.receipt.main.model.ReceiptMode
import com.anddd.nevera.feature.receipt.main.model.ReceiptSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ReceiptScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReceiptViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value
    val cameraPermissionState = rememberCameraPermissionState()
    val galleryPermissionState = rememberGalleryPermissionState()

    LaunchedEffect(uiState.mode, cameraPermissionState.hasPermission, galleryPermissionState.hasPermission) {
        when (uiState.mode) {
            ReceiptMode.Camera -> if (!cameraPermissionState.hasPermission) cameraPermissionState.requestPermission()
            ReceiptMode.Gallery -> if (!galleryPermissionState.hasPermission) {
                galleryPermissionState.requestPermission()
            } else {
                viewModel.handleIntent(ReceiptIntent.LoadGalleryImages)
            }
        }
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            ReceiptSideEffect.NavigateBack -> onNavigateBack()
            is ReceiptSideEffect.NavigateToResult -> onNavigateBack()
            ReceiptSideEffect.OpenAppSettings -> context.openAppSettings()
            ReceiptSideEffect.ShowCaptureError ->
                Toast.makeText(context, "촬영에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    ReceiptContent(
        mode = uiState.mode,
        galleryImages = uiState.galleryImages,
        cameraPermissionState = cameraPermissionState,
        galleryPermissionState = galleryPermissionState,
        onClose = onNavigateBack,
        onIntent = viewModel::handleIntent,
        onBindCamera = viewModel::bindCamera,
        onCameraOpenSettings = {
            cameraPermissionState.clearDenied()
            context.openAppSettings()
        },
        onGalleryOpenSettings = {
            galleryPermissionState.clearDenied()
            context.openAppSettings()
        },
    )
}

private fun Context.openAppSettings() {
    startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
    )
}