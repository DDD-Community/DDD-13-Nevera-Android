package com.anddd.nevera.core.designsystem.ui.theme.color

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
class NeveraColor(
    // Background
    val backgroundPrimary: Color,
    val backgroundSecondary: Color,

    // Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
    val textInverse: Color,

    // Brand
    val brandPrimary: Color,
    val onBrandPrimary: Color,
    val brandPrimarySubtle: Color,
    val onBrandPrimarySubtle: Color,

    // Disabled
    val disabledContainer: Color,
    val disabledContent: Color,

    // Border / Divider
    val borderDefault: Color,
    val divider: Color,
)

internal val LightNeveraColors = NeveraColor(
    backgroundPrimary = ColorPalette.white,
    backgroundSecondary = ColorPalette.gray50,

    textPrimary = ColorPalette.gray800,
    textSecondary = ColorPalette.gray600,
    textTertiary = ColorPalette.gray500,
    textDisabled = ColorPalette.gray400,
    textInverse = ColorPalette.white,

    brandPrimary = ColorPalette.primary400,
    onBrandPrimary = ColorPalette.onPrimary400,
    brandPrimarySubtle = ColorPalette.primary50,
    onBrandPrimarySubtle = ColorPalette.onPrimary500,

    disabledContainer = ColorPalette.gray300,
    disabledContent = ColorPalette.gray500,

    borderDefault = ColorPalette.gray300,
    divider = ColorPalette.gray200,
)

// TODO: Dark mode colors - pending design team's color palette
internal val DarkNeveraColors = NeveraColor(
    backgroundPrimary = ColorPalette.white,
    backgroundSecondary = ColorPalette.gray50,

    textPrimary = ColorPalette.gray800,
    textSecondary = ColorPalette.gray600,
    textTertiary = ColorPalette.gray500,
    textDisabled = ColorPalette.gray400,
    textInverse = ColorPalette.white,

    brandPrimary = ColorPalette.primary400,
    onBrandPrimary = ColorPalette.onPrimary400,
    brandPrimarySubtle = ColorPalette.primary50,
    onBrandPrimarySubtle = ColorPalette.onPrimary500,

    disabledContainer = ColorPalette.gray300,
    disabledContent = ColorPalette.gray500,

    borderDefault = ColorPalette.gray300,
    divider = ColorPalette.gray200,
)

internal val LocalNeveraColors = staticCompositionLocalOf { LightNeveraColors }
