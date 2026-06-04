package com.anddd.nevera.feature.ingredient.ocrcapture

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.feature.ingredient.R
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureIntent
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureSideEffect
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun OcrCaptureScreen(
    openGallery: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToResult: (Uri) -> Unit,
    viewModel: OcrCaptureViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    DarkNavigationBarEffect()

    val cameraPermissionState = rememberCameraPermissionState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) viewModel.handleIntent(OcrCaptureIntent.SelectImage(uri))
    }

    var galleryAutoLaunched by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.hasPermission) {
            cameraPermissionState.requestPermission()
        }
    }

    LaunchedEffect(cameraPermissionState.hasPermission, cameraPermissionState.isDenied) {
        if (openGallery && !galleryAutoLaunched) {
            if (cameraPermissionState.hasPermission || cameraPermissionState.isDenied) {
                galleryAutoLaunched = true
                viewModel.handleIntent(OcrCaptureIntent.OpenGallery)
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
            OcrCaptureSideEffect.LaunchPhotoPicker ->
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            OcrCaptureSideEffect.ShowCaptureError ->
                Toast.makeText(context, context.getString(R.string.ocr_capture_error), Toast.LENGTH_SHORT).show()
        }
    }

    OcrCaptureContent(
        cameraPermissionState = cameraPermissionState,
        onIntent = viewModel::handleIntent,
        onBindCamera = viewModel::bindCamera,
    )
}

// enableEdgeToEdge() 기본값이 라이트 모드에서 흰색 scrim(#E6FFFFFF)을 네비게이션바에 적용하므로,
// 다크 배경 화면 진입 시 투명 다크로 전환하고 이탈 시 기본값으로 복원한다.
@Composable
private fun DarkNavigationBarEffect() {
    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(Unit) {
            val activity = view.context.findActivity() as? ComponentActivity
                ?: return@DisposableEffect onDispose { }
            activity.enableEdgeToEdge(
                navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            )
            onDispose {
                activity.enableEdgeToEdge()
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun Context.openAppSettings() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }.let(::startActivity)
}
