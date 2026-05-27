package com.anddd.nevera.feature.receipt.main.model

import android.net.Uri
import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface ReceiptIntent : NeveraIntent {
    data object Close : ReceiptIntent
    data object SwitchToGallery : ReceiptIntent
    data object SwitchToCamera : ReceiptIntent
    data object TakePicture : ReceiptIntent
    data object SwapCamera : ReceiptIntent
    data object LoadGalleryImages : ReceiptIntent
    data class SelectImage(val uri: Uri) : ReceiptIntent
    data object OpenCameraSettings : ReceiptIntent
    data object OpenGallerySettings : ReceiptIntent
}