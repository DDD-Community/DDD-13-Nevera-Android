package com.anddd.nevera.feature.receipt.main.model

import android.graphics.Bitmap
import android.net.Uri
import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface ReceiptMutation : NeveraMutation {
    data class ModeChanged(val mode: ReceiptMode) : ReceiptMutation
    data class GalleryLoaded(val images: List<Uri>) : ReceiptMutation
    data class CaptureSuccess(val bitmap: Bitmap) : ReceiptMutation
}