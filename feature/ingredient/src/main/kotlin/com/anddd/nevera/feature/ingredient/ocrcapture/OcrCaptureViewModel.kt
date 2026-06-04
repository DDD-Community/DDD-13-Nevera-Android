package com.anddd.nevera.feature.ingredient.ocrcapture

import android.net.Uri
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.feature.ingredient.ocrcapture.component.camera.CameraManager
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureIntent
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureMutation
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureSideEffect
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.coroutines.cancellation.CancellationException
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class OcrCaptureViewModel @Inject constructor(
    private val cameraManager: CameraManager,
) : NeveraViewModel<OcrCaptureUiState, OcrCaptureSideEffect, OcrCaptureIntent, OcrCaptureMutation>(
    OcrCaptureUiState
) {

    fun bindCamera(lifecycleOwner: LifecycleOwner, surfaceProvider: Preview.SurfaceProvider) {
        intent {
            runCatching { cameraManager.bindCamera(lifecycleOwner, surfaceProvider) }
                .onFailure { e ->
                    if (e is CancellationException) throw e
                    postSideEffect(OcrCaptureSideEffect.ShowCaptureError)
                }
        }
    }

    override fun handleIntent(action: OcrCaptureIntent) {
        when (action) {
            OcrCaptureIntent.Close -> onClose()
            OcrCaptureIntent.OpenGallery -> onOpenGallery()
            OcrCaptureIntent.TakePicture -> onTakePicture()
            OcrCaptureIntent.SwapCamera -> onSwapCamera()
            is OcrCaptureIntent.SelectImage -> onSelectImage(action.uri)
            OcrCaptureIntent.OpenCameraSettings -> onOpenCameraSettings()
        }
    }

    private fun onClose() = intent {
        postSideEffect(OcrCaptureSideEffect.NavigateBack)
    }

    private fun onOpenCameraSettings() = intent {
        postSideEffect(OcrCaptureSideEffect.OpenCameraSettings)
    }

    private fun onOpenGallery() = intent {
        postSideEffect(OcrCaptureSideEffect.LaunchPhotoPicker)
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
        }
    }

    override fun onCleared() {
        super.onCleared()
        cameraManager.release()
    }
}
