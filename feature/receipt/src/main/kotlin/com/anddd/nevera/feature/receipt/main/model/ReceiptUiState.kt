package com.anddd.nevera.feature.receipt.main.model

import android.net.Uri
import com.anddd.nevera.core.mvi.NeveraState

data class ReceiptUiState(
    val mode: ReceiptMode = ReceiptMode.Camera,
    val galleryImages: List<Uri> = emptyList(),
) : NeveraState