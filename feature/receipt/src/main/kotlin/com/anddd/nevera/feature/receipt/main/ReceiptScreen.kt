package com.anddd.nevera.feature.receipt.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.feature.receipt.R
import com.anddd.nevera.feature.receipt.main.model.ReceiptIntent
import com.anddd.nevera.feature.receipt.main.model.ReceiptMode
import com.anddd.nevera.feature.receipt.main.model.ReceiptSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ReceiptScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: (Uri) -> Unit,
    viewModel: ReceiptViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value
    val cameraPermissionState = rememberCameraPermissionState()
    val galleryPermissionState = rememberGalleryPermissionState(
        onGranted = { viewModel.handleIntent(ReceiptIntent.LoadGalleryImages) }
    )

    LaunchedEffect(uiState.mode, galleryPermissionState.hasPermission) {
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
            is ReceiptSideEffect.NavigateToResult -> onNavigateToResult(effect.uri)
            ReceiptSideEffect.OpenCameraSettings -> {
                cameraPermissionState.clearDenied()
                context.openAppSettings()
            }
            ReceiptSideEffect.OpenGallerySettings -> {
                galleryPermissionState.clearDenied()
                context.openAppSettings()
            }
            ReceiptSideEffect.ShowCaptureError ->
                Toast.makeText(context, context.getString(R.string.receipt_capture_error), Toast.LENGTH_SHORT).show()
        }
    }

    ReceiptContent(
        uiState = uiState,
        cameraPermissionState = cameraPermissionState,
        galleryPermissionState = galleryPermissionState,
        onIntent = viewModel::handleIntent,
        onBindCamera = viewModel::bindCamera,
    )
}

private fun Context.openAppSettings() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }.let(::startActivity)
}