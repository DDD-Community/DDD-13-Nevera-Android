package com.anddd.nevera.feature.ingredient.ocrcapture

import android.net.Uri
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.feature.ingredient.ocrcapture.component.camera.CameraManager
import com.anddd.nevera.feature.ingredient.ocrcapture.component.gallery.GalleryManager
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureIntent
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureMode
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureMutation
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureSideEffect
import com.anddd.nevera.feature.ingredient.ocrcapture.model.OcrCaptureUiState
import com.anddd.nevera.feature.ingredient.ocrcapture.navigation.GALLERY_MODE_VALUE
import com.anddd.nevera.feature.ingredient.ocrcapture.navigation.OCR_CAPTURE_INITIAL_MODE_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.coroutines.cancellation.CancellationException
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class OcrCaptureViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cameraManager: CameraManager,
    private val galleryManager: GalleryManager,
) : NeveraViewModel<OcrCaptureUiState, OcrCaptureSideEffect, OcrCaptureIntent, OcrCaptureMutation>(
    OcrCaptureUiState(
        mode = if (savedStateHandle.get<String>(OCR_CAPTURE_INITIAL_MODE_ARG) == GALLERY_MODE_VALUE)
            OcrCaptureMode.Gallery else OcrCaptureMode.Camera
    )
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
            OcrCaptureIntent.SwitchToGallery -> onSwitchToGallery()
            OcrCaptureIntent.SwitchToCamera -> onSwitchToCamera()
            OcrCaptureIntent.TakePicture -> onTakePicture()
            OcrCaptureIntent.SwapCamera -> onSwapCamera()
            OcrCaptureIntent.LoadGalleryImages -> onLoadGalleryImages()
            is OcrCaptureIntent.SelectImage -> onSelectImage(action.uri)
            OcrCaptureIntent.OpenCameraSettings -> onOpenCameraSettings()
            OcrCaptureIntent.OpenGallerySettings -> onOpenGallerySettings()
        }
    }

    private fun onClose() = intent {
        postSideEffect(OcrCaptureSideEffect.NavigateBack)
    }

    private fun onOpenCameraSettings() = intent {
        postSideEffect(OcrCaptureSideEffect.OpenCameraSettings)
    }

    private fun onOpenGallerySettings() = intent {
        postSideEffect(OcrCaptureSideEffect.OpenGallerySettings)
    }

    private fun onSwitchToGallery() = intent {
        applyMutation(OcrCaptureMutation.ModeChanged(OcrCaptureMode.Gallery))
    }

    private fun onSwitchToCamera() = intent {
        applyMutation(OcrCaptureMutation.ModeChanged(OcrCaptureMode.Camera))
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

    private fun onLoadGalleryImages() = intent {
        val images = galleryManager.loadImages()
        applyMutation(OcrCaptureMutation.GalleryLoaded(images))
    }

    override suspend fun Syntax<OcrCaptureUiState, OcrCaptureSideEffect>.applyMutation(
        mutation: OcrCaptureMutation,
    ) {
        when (mutation) {
            is OcrCaptureMutation.ModeChanged ->
                reduce { state.copy(mode = mutation.mode) }
            is OcrCaptureMutation.GalleryLoaded ->
                reduce { state.copy(galleryImages = mutation.images) }
            is OcrCaptureMutation.CaptureSuccess ->
                postSideEffect(OcrCaptureSideEffect.NavigateToResult(mutation.uri))
        }
    }

    override fun onCleared() {
        super.onCleared()
        cameraManager.release()
    }
}
