package com.anddd.nevera.core.designsystem.component.textfield.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldState
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldType
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

internal object NeveraTextFieldColors {

    // Color.Unspecified는 "재정의 없음" 신호 — negativeColor가 지정된 경우에만 덮어쓴다.
    private fun resolveNegativeColor(negativeColor: Color, fallback: Color): Color =
        if (negativeColor != Color.Unspecified) negativeColor else fallback

    @Composable
    fun borderColor(
        type: NeveraTextFieldType,
        state: NeveraTextFieldState,
        isFocused: Boolean,
        isActive: Boolean,
        enabled: Boolean,
        negativeColor: Color,
    ): Color {
        val resolvedNegativeColor = resolveNegativeColor(negativeColor, NeveraTheme.colors.statusNegativeNormal)
        val defaultColor = when (type) {
            NeveraTextFieldType.Box -> Color.Transparent
            NeveraTextFieldType.Underline -> NeveraTheme.colors.borderNormal
        }
        return when {
            !enabled -> defaultColor
            state == NeveraTextFieldState.Negative && (isFocused || isActive) -> resolvedNegativeColor
            state == NeveraTextFieldState.Negative -> defaultColor
            isFocused -> NeveraTheme.colors.secondaryWeak
            else -> defaultColor
        }
    }

    @Composable
    fun containerColor(type: NeveraTextFieldType): Color = when (type) {
        NeveraTextFieldType.Box -> NeveraTheme.colors.surfaceSecondary
        NeveraTextFieldType.Underline -> Color.Transparent
    }

    @Composable
    fun headingColor(): Color = NeveraTheme.colors.textCaption

    @Composable
    fun inputTextColor(enabled: Boolean): Color = when {
        !enabled -> NeveraTheme.colors.textDisabled
        else -> NeveraTheme.colors.textSecondary
    }

    @Composable
    fun placeholderColor(type: NeveraTextFieldType, enabled: Boolean): Color = when {
        enabled && type == NeveraTextFieldType.Box -> NeveraTheme.colors.textCaption
        else -> NeveraTheme.colors.textDisabled
    }

    @Composable
    fun descriptionColor(
        state: NeveraTextFieldState,
        enabled: Boolean,
        negativeColor: Color,
    ): Color {
        val resolvedNegativeColor = resolveNegativeColor(negativeColor, NeveraTheme.colors.statusNegativeStrong)
        return when {
            !enabled -> NeveraTheme.colors.textDisabled
            state == NeveraTextFieldState.Negative -> resolvedNegativeColor
            else -> NeveraTheme.colors.textQuaternary
        }
    }

    @Composable
    fun stateIconColor(
        state: NeveraTextFieldState,
        negativeColor: Color,
    ): Color {
        val resolvedNegativeColor = resolveNegativeColor(negativeColor, NeveraTheme.colors.statusNegativeNormal)
        return when (state) {
            NeveraTextFieldState.Positive -> NeveraTheme.colors.statusPositiveNormal
            NeveraTextFieldState.Negative -> resolvedNegativeColor
            NeveraTextFieldState.Normal -> Color.Unspecified // 아이콘 미표시
        }
    }

    @Composable
    fun eyeIconColor(enabled: Boolean): Color = when {
        !enabled -> NeveraTheme.colors.iconDisabled
        else -> NeveraTheme.colors.iconPrimary
    }
}
