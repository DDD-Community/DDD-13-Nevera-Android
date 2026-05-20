package com.anddd.nevera.feature.receipt.main

import android.net.Uri
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.feature.receipt.camera.CameraManager
import com.anddd.nevera.feature.receipt.gallery.GalleryManager
import com.anddd.nevera.feature.receipt.main.model.ReceiptIntent
import com.anddd.nevera.feature.receipt.main.model.ReceiptMode
import com.anddd.nevera.feature.receipt.main.model.ReceiptMutation
import com.anddd.nevera.feature.receipt.main.model.ReceiptSideEffect
import com.anddd.nevera.feature.receipt.main.model.ReceiptUiState
import com.anddd.nevera.feature.receipt.main.navigation.GALLERY_MODE_VALUE
import com.anddd.nevera.feature.receipt.main.navigation.RECEIPT_INITIAL_MODE_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cameraManager: CameraManager,
    private val galleryManager: GalleryManager,
) : NeveraViewModel<ReceiptUiState, ReceiptSideEffect, ReceiptIntent, ReceiptMutation>(
    ReceiptUiState(
        mode = if (savedStateHandle.get<String>(RECEIPT_INITIAL_MODE_ARG) == GALLERY_MODE_VALUE)
            ReceiptMode.Gallery else ReceiptMode.Camera
    )
) {

    fun bindCamera(lifecycleOwner: LifecycleOwner, surfaceProvider: Preview.SurfaceProvider) {
        intent {
            runCatching { cameraManager.bindCamera(lifecycleOwner, surfaceProvider) }
                .onFailure { e ->
                    if (e is CancellationException) throw e
                    postSideEffect(ReceiptSideEffect.ShowCaptureError)
                }
        }
    }

    override fun handleIntent(action: ReceiptIntent) {
        when (action) {
            ReceiptIntent.Close -> onClose()
            ReceiptIntent.SwitchToGallery -> onSwitchToGallery()
            ReceiptIntent.SwitchToCamera -> onSwitchToCamera()
            ReceiptIntent.TakePicture -> onTakePicture()
            ReceiptIntent.SwapCamera -> onSwapCamera()
            ReceiptIntent.LoadGalleryImages -> onLoadGalleryImages()
            is ReceiptIntent.SelectImage -> onSelectImage(action.uri)
            ReceiptIntent.OpenCameraSettings -> onOpenCameraSettings()
            ReceiptIntent.OpenGallerySettings -> onOpenGallerySettings()
        }
    }

    private fun onClose() = intent {
        postSideEffect(ReceiptSideEffect.NavigateBack)
    }

    private fun onOpenCameraSettings() = intent {
        postSideEffect(ReceiptSideEffect.OpenCameraSettings)
    }

    private fun onOpenGallerySettings() = intent {
        postSideEffect(ReceiptSideEffect.OpenGallerySettings)
    }

    private fun onSwitchToGallery() = intent {
        applyMutation(ReceiptMutation.ModeChanged(ReceiptMode.Gallery))
    }

    private fun onSwitchToCamera() = intent {
        applyMutation(ReceiptMutation.ModeChanged(ReceiptMode.Camera))
    }

    private fun onSwapCamera() = intent { cameraManager.swapCamera() }

    private fun onSelectImage(uri: Uri) = intent {
        postSideEffect(ReceiptSideEffect.NavigateToResult(uri))
    }

    private fun onTakePicture() = intent {
        runCatching { cameraManager.takePicture() }
            .onSuccess { applyMutation(ReceiptMutation.CaptureSuccess(it)) }
            .onFailure { e ->
                if (e is CancellationException) throw e
                postSideEffect(ReceiptSideEffect.ShowCaptureError)
            }
    }

    private fun onLoadGalleryImages() = intent {
        val images = galleryManager.loadImages()
        applyMutation(ReceiptMutation.GalleryLoaded(images))
    }

    override suspend fun Syntax<ReceiptUiState, ReceiptSideEffect>.applyMutation(
        mutation: ReceiptMutation,
    ) {
        when (mutation) {
            is ReceiptMutation.ModeChanged ->
                reduce { state.copy(mode = mutation.mode) }
            is ReceiptMutation.GalleryLoaded ->
                reduce { state.copy(galleryImages = mutation.images) }
            is ReceiptMutation.CaptureSuccess ->
                postSideEffect(ReceiptSideEffect.NavigateToResult(mutation.uri))
        }
    }

    override fun onCleared() {
        super.onCleared()
        cameraManager.release()
    }
}