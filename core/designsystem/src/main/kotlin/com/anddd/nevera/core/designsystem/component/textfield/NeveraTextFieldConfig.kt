package com.anddd.nevera.core.designsystem.component.textfield

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

enum class NeveraTextFieldType {
    Box,
    Underline,
}

enum class NeveraTextFieldState {
    Normal,
    Positive,
    Negative,
}

@Stable
data class NeveraTextFieldConfig(
    val type: NeveraTextFieldType = NeveraTextFieldType.Box,
    val state: NeveraTextFieldState = NeveraTextFieldState.Normal,
    val heading: String? = null,
    val placeholder: String? = null,
    val description: String? = null,
    val negativeColor: Color = Color.Unspecified,
    val singleLine: Boolean = true,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
)
