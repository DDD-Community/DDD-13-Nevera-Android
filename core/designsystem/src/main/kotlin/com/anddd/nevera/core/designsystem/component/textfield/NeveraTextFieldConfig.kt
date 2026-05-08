package com.anddd.nevera.core.designsystem.component.textfield

import androidx.compose.foundation.text.KeyboardActions
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

// KeyboardActions는 람다를 포함하므로 data class의 auto-generated equals()가 항상 불일치를 반환한다.
// @Stable + 커스텀 equals/hashCode로 keyboardActions를 제외해 Compose 스마트 리컴포지션을 활성화한다.
@Stable
class NeveraTextFieldConfig(
    val type: NeveraTextFieldType = NeveraTextFieldType.Box,
    val state: NeveraTextFieldState = NeveraTextFieldState.Normal,
    val heading: String? = null,
    val placeholder: String? = null,
    val description: String? = null,
    val negativeColor: Color = Color.Unspecified,
    val singleLine: Boolean = true,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NeveraTextFieldConfig) return false
        return type == other.type &&
            state == other.state &&
            heading == other.heading &&
            placeholder == other.placeholder &&
            description == other.description &&
            negativeColor == other.negativeColor &&
            singleLine == other.singleLine &&
            keyboardOptions == other.keyboardOptions
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + (heading?.hashCode() ?: 0)
        result = 31 * result + (placeholder?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + negativeColor.hashCode()
        result = 31 * result + singleLine.hashCode()
        result = 31 * result + keyboardOptions.hashCode()
        return result
    }
}