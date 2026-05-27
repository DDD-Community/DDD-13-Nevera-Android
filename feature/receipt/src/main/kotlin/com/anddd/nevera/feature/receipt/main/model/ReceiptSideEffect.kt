package com.anddd.nevera.feature.receipt.main.model

import android.net.Uri
import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface ReceiptSideEffect : NeveraSideEffect {
    data object NavigateBack : ReceiptSideEffect
    data class NavigateToResult(val uri: Uri) : ReceiptSideEffect
    data object OpenCameraSettings : ReceiptSideEffect
    data object OpenGallerySettings : ReceiptSideEffect
    data object ShowCaptureError : ReceiptSideEffect
}