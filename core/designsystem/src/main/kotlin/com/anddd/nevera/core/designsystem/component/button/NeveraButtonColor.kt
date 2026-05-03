package com.anddd.nevera.core.designsystem.component.button

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.anddd.nevera.core.designsystem.ui.theme.color.NeveraColor

enum class NeveraButtonColor { Primary, Secondary, Danger, }

@Immutable
internal data class NeveraButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val pressedContainerColor: Color,
    val pressedContentColor: Color,
    val borderColor: Color = Color.Transparent,
    val pressedBorderColor: Color = Color.Transparent,
) {
    val disabledContainerColor: Color = containerColor.copy(alpha = 0.3f)
    val disabledContentColor: Color = contentColor.copy(alpha = 0.3f)
    val disabledBorderColor: Color = if (borderColor != Color.Transparent) borderColor.copy(alpha = 0.3f) else borderColor
}

internal fun NeveraColor.filledButtonColors(color: NeveraButtonColor): NeveraButtonColors =
    when (color) {
        NeveraButtonColor.Primary -> NeveraButtonColors(
            containerColor = primaryNormal,
            contentColor = textInverse,
            pressedContainerColor = primaryStrong,
            pressedContentColor = textInverse,
        )

        NeveraButtonColor.Secondary -> NeveraButtonColors(
            containerColor = secondaryNormal,
            contentColor = textInverse,
            pressedContainerColor = secondaryStrong,
            pressedContentColor = textInverse,
        )

        NeveraButtonColor.Danger -> NeveraButtonColors(
            containerColor = statusNegativeNormal,
            contentColor = textInverse,
            pressedContainerColor = statusNegativeStrong,
            pressedContentColor = textInverse,
        )
    }

internal fun NeveraColor.weakButtonColors(color: NeveraButtonColor): NeveraButtonColors =
    when (color) {
        NeveraButtonColor.Primary -> NeveraButtonColors(
            containerColor = surfaceBrandPrimary,
            contentColor = primaryNormal,
            pressedContainerColor = surfaceBrandSecondary,
            pressedContentColor = primaryStrong,
        )

        NeveraButtonColor.Secondary -> NeveraButtonColors(
            containerColor = surfaceSecondary,
            contentColor = textTertiary,
            pressedContainerColor = surfaceTertiary,
            pressedContentColor = textSecondary,
        )

        NeveraButtonColor.Danger -> NeveraButtonColors(
            containerColor = statusNegativeGhost,
            contentColor = statusNegativeNormal,
            pressedContainerColor = statusNegativeWeak,
            pressedContentColor = statusNegativeStrong,
        )
    }

internal fun NeveraColor.outlinedButtonColors(color: NeveraButtonColor): NeveraButtonColors =
    when (color) {
        NeveraButtonColor.Primary -> NeveraButtonColors(
            containerColor = surfacePrimary,
            contentColor = primaryNormal,
            pressedContainerColor = surfaceSecondary,
            pressedContentColor = primaryStrong,
            borderColor = primaryNormal,
            pressedBorderColor = primaryStrong,
        )

        NeveraButtonColor.Secondary -> NeveraButtonColors(
            containerColor = surfacePrimary,
            contentColor = textTertiary,
            pressedContainerColor = surfaceSecondary,
            pressedContentColor = textSecondary,
            borderColor = secondaryNormal,
            pressedBorderColor = secondaryStrong,
        )

        NeveraButtonColor.Danger -> NeveraButtonColors(
            containerColor = surfacePrimary,
            contentColor = statusNegativeNormal,
            pressedContainerColor = surfaceSecondary,
            pressedContentColor = statusNegativeStrong,
            borderColor = statusNegativeNormal,
            pressedBorderColor = statusNegativeStrong,
        )
    }

internal fun NeveraColor.ghostButtonColors(color: NeveraButtonColor): NeveraButtonColors =
    when (color) {
        NeveraButtonColor.Primary -> NeveraButtonColors(
            containerColor = Color.Transparent,
            contentColor = primaryNormal,
            pressedContainerColor = surfaceSecondary,
            pressedContentColor = primaryStrong,
        )

        NeveraButtonColor.Secondary -> NeveraButtonColors(
            containerColor = Color.Transparent,
            contentColor = textTertiary,
            pressedContainerColor = surfaceSecondary,
            pressedContentColor = textSecondary,
        )

        NeveraButtonColor.Danger -> NeveraButtonColors(
            containerColor = Color.Transparent,
            contentColor = statusNegativeNormal,
            pressedContainerColor = surfaceSecondary,
            pressedContentColor = statusNegativeStrong,
        )
    }
