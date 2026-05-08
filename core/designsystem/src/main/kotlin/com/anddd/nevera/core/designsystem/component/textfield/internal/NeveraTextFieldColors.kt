package com.anddd.nevera.core.designsystem.component.textfield.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldState
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldType
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

internal object NeveraTextFieldColors {

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

    /**
     * TODO :: Box에서 backgroundSecondary와 alpha/white24 두개를 적용해서 색상을 표현한 상태.
     * 색상 스포일러로 확인 시, #F7F7F8 색상으로 확인
     */
    @Composable
    fun containerColor(type: NeveraTextFieldType): Color = when (type) {
        NeveraTextFieldType.Box -> NeveraTheme.colors.backgroundSecondary
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
            NeveraTextFieldState.Normal -> Color.Unspecified
        }
    }

    @Composable
    fun eyeIconColor(enabled: Boolean): Color = when {
        !enabled -> NeveraTheme.colors.iconDisabled
        else -> NeveraTheme.colors.iconPrimary
    }
}
