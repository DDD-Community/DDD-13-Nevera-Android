package com.anddd.nevera.feature.ingredient.ocrcapture.model

import com.anddd.nevera.core.mvi.NeveraState

data class OcrCaptureUiState(
    val hasCameraPermission: Boolean = false,
    val showPermissionDialog: Boolean = false,
) : NeveraState
