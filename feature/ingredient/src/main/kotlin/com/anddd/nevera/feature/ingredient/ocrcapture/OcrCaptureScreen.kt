package com.anddd.nevera.feature.ingredient.ocrcapture

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.feature.ingredient.R
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureIntent
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureMode
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun OcrCaptureScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: (Uri) -> Unit,
    viewModel: OcrCaptureViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value
    val cameraPermissionState = rememberCameraPermissionState()
    val galleryPermissionState = rememberGalleryPermissionState(
        onGranted = { viewModel.handleIntent(OcrCaptureIntent.LoadGalleryImages) }
    )

    LaunchedEffect(uiState.mode, galleryPermissionState.hasPermission) {
        when (uiState.mode) {
            OcrCaptureMode.Camera -> if (!cameraPermissionState.hasPermission) cameraPermissionState.requestPermission()
            OcrCaptureMode.Gallery -> if (!galleryPermissionState.hasPermission) {
                galleryPermissionState.requestPermission()
            } else {
                viewModel.handleIntent(OcrCaptureIntent.LoadGalleryImages)
            }
        }
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            OcrCaptureSideEffect.NavigateBack -> onNavigateBack()
            is OcrCaptureSideEffect.NavigateToResult -> onNavigateToResult(effect.uri)
            OcrCaptureSideEffect.OpenCameraSettings -> {
                cameraPermissionState.clearDenied()
                context.openAppSettings()
            }
            OcrCaptureSideEffect.OpenGallerySettings -> {
                galleryPermissionState.clearDenied()
                context.openAppSettings()
            }
            OcrCaptureSideEffect.ShowCaptureError ->
                Toast.makeText(context, context.getString(R.string.ocr_capture_error), Toast.LENGTH_SHORT).show()
        }
    }

    OcrCaptureContent(
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
