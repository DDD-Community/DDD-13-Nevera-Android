package com.anddd.nevera.feature.ingredient.ocrcapture

import android.net.Uri
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.feature.ingredient.ocrcapture.component.camera.CameraManager
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureIntent
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureMutation
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureSideEffect
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureUiState
import com.anddd.nevera.feature.ingredient.ocrcapture.navigation.OcrCaptureRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.coroutines.cancellation.CancellationException
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class OcrCaptureViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cameraManager: CameraManager,
) : NeveraViewModel<OcrCaptureUiState, OcrCaptureSideEffect, OcrCaptureIntent, OcrCaptureMutation>(
    OcrCaptureUiState()
) {
    private val openGallery = savedStateHandle.toRoute<OcrCaptureRoute>().openGallery
    private var galleryAutoLaunched = false

    fun bindCamera(lifecycleOwner: LifecycleOwner, surfaceProvider: Preview.SurfaceProvider) {
        intent {
            runCatching { cameraManager.bindCamera(lifecycleOwner, surfaceProvider) }
                .onFailure { e ->
                    if (e is CancellationException) throw e
                    postSideEffect(OcrCaptureSideEffect.ShowCaptureError)
                }
        }
    }

    override fun handleIntent(intent: OcrCaptureIntent) {
        when (intent) {
            OcrCaptureIntent.Close -> onClose()
            OcrCaptureIntent.OpenGallery -> onOpenGallery()
            OcrCaptureIntent.EnsureGalleryIfNeeded -> onEnsureGalleryIfNeeded()
            OcrCaptureIntent.TakePicture -> onTakePicture()
            OcrCaptureIntent.SwapCamera -> onSwapCamera()
            is OcrCaptureIntent.SelectImage -> onSelectImage(intent.uri)
            OcrCaptureIntent.OpenCameraSettings -> onOpenCameraSettings()
            is OcrCaptureIntent.CameraPermissionUpdated -> onCameraPermissionUpdated(intent.hasPermission, intent.isDenied)
        }
    }

    private fun onClose() = intent {
        postSideEffect(OcrCaptureSideEffect.NavigateBack)
    }

    private fun onOpenCameraSettings() = intent {
        postSideEffect(OcrCaptureSideEffect.OpenCameraSettings)
    }

    private fun onCameraPermissionUpdated(hasPermission: Boolean, isDenied: Boolean) = intent {
        applyMutation(OcrCaptureMutation.UpdateCameraPermission(hasPermission, isDenied))
    }

    private fun onOpenGallery() = intent {
        postSideEffect(OcrCaptureSideEffect.LaunchPhotoPicker)
    }

    private fun onEnsureGalleryIfNeeded() = intent {
        if (openGallery && !galleryAutoLaunched) {
            galleryAutoLaunched = true
            postSideEffect(OcrCaptureSideEffect.LaunchPhotoPicker)
        }
    }

    private fun onSwapCamera() = intent { cameraManager.swapCamera() }

    private fun onSelectImage(uri: Uri) = intent {
        postSideEffect(OcrCaptureSideEffect.NavigateToResult(uri))
    }

    private fun onTakePicture() = intent {
        runCatching { cameraManager.takePicture() }
            .onSuccess { applyMutation(OcrCaptureMutation.CaptureSuccess(it)) }
            .onFailure { e ->
                if (e is CancellationException) throw e
                postSideEffect(OcrCaptureSideEffect.ShowCaptureError)
            }
    }

    override suspend fun Syntax<OcrCaptureUiState, OcrCaptureSideEffect>.applyMutation(
        mutation: OcrCaptureMutation,
    ) {
        when (mutation) {
            is OcrCaptureMutation.CaptureSuccess ->
                postSideEffect(OcrCaptureSideEffect.NavigateToResult(mutation.uri))
            is OcrCaptureMutation.UpdateCameraPermission -> reduce {
                state.copy(hasCameraPermission = mutation.hasPermission, showPermissionDialog = mutation.isDenied)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cameraManager.release()
    }
}
