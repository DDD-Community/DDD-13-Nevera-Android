package com.anddd.nevera.feature.receipt.main.model

import android.net.Uri
import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface ReceiptMutation : NeveraMutation {
    data class ModeChanged(val mode: ReceiptMode) : ReceiptMutation
    data class GalleryLoaded(val images: List<Uri>) : ReceiptMutation
    // file:// Uri (cacheDir 임시 파일) — 갤러리의 content:// Uri와 ResultScreen에서 동일하게 처리
    data class CaptureSuccess(val uri: Uri) : ReceiptMutation
}