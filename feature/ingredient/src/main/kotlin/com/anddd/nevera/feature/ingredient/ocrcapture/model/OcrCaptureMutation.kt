package com.anddd.nevera.feature.ingredient.ocrcapture.model

import android.net.Uri
import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface OcrCaptureMutation : NeveraMutation {
    data class ModeChanged(val mode: OcrCaptureMode) : OcrCaptureMutation
    data class GalleryLoaded(val images: List<Uri>) : OcrCaptureMutation
    // file:// Uri (cacheDir 임시 파일) — 갤러리의 content:// Uri와 ResultScreen에서 동일하게 처리
    data class CaptureSuccess(val uri: Uri) : OcrCaptureMutation
}
