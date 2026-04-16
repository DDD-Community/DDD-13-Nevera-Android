package com.anddd.nevera.core.designsystem.ui.theme.color

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
class NeveraColor(

    // Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textQuaternary: Color,
    val textCaption: Color,
    val textDisabled: Color,
    val textInverse: Color,

    // Icon
    val iconPrimary: Color,
    val iconSecondary: Color,
    val iconTertiary: Color,
    val iconQuaternary: Color,
    val iconCaption: Color,
    val iconDisabled: Color,
    val iconInverse: Color,

    // Primary
    val primaryWeak: Color,
    val primaryNormal: Color,
    val primaryStrong: Color,
    val primaryHeavy: Color,

    // Secondary
    val secondaryWeak: Color,
    val secondaryNormal: Color,
    val secondaryStrong: Color,
    val secondaryHeavy: Color,

    // Accent
    val accentOrange: Color,
    val accentRed: Color,
    val accentYellow: Color,
    val accentLime: Color,
    val accentGreen: Color,
    val accentCyan: Color,
    val accentBlue: Color,
    val accentPurple: Color,

    // Background
    val backgroundPrimary: Color,
    val backgroundSecondary: Color,
    val backgroundTertiary: Color,
    val backgroundInverse: Color,

    // Surface
    val surfacePrimary: Color,
    val surfaceSecondary: Color,
    val surfaceTertiary: Color,
    val surfaceQuaternary: Color,
    val surfaceInverse: Color,
    val surfaceBrandPrimary: Color,

    // Border
    val borderNormal: Color,
    val borderStrong: Color,

    // Divider
    val dividerNormal: Color,
    val dividerStrong: Color,

    // Status - Information
    val statusInformationGhost: Color,
    val statusInformationWeak: Color,
    val statusInformationNormal: Color,
    val statusInformationStrong: Color,

    // Status - Positive
    val statusPositiveGhost: Color,
    val statusPositiveWeak: Color,
    val statusPositiveNormal: Color,
    val statusPositiveStrong: Color,

    // Status - Negative
    val statusNegativeGhost: Color,
    val statusNegativeWeak: Color,
    val statusNegativeNormal: Color,
    val statusNegativeStrong: Color,

    // Status - Warning
    val statusWarningGhost: Color,
    val statusWarningWeak: Color,
    val statusWarningNormal: Color,
    val statusWarningStrong: Color,

    // Notification
    val notificationRed: Color,
)

internal val LightNeveraColors = NeveraColor(
    // Text
    textPrimary = ColorPalette.gray95,
    textSecondary = ColorPalette.gray80,
    textTertiary = ColorPalette.gray60,
    textQuaternary = ColorPalette.gray50,
    textCaption = ColorPalette.gray40,
    textDisabled = ColorPalette.gray30,
    textInverse = ColorPalette.white,

    // Icon
    iconPrimary = ColorPalette.gray80,
    iconSecondary = ColorPalette.gray70,
    iconTertiary = ColorPalette.gray60,
    iconQuaternary = ColorPalette.gray40,
    iconCaption = ColorPalette.gray30,
    iconDisabled = ColorPalette.gray20,
    iconInverse = ColorPalette.white,

    // Primary
    primaryWeak = ColorPalette.orange40,
    primaryNormal = ColorPalette.orange50,
    primaryStrong = ColorPalette.orange60,
    primaryHeavy = ColorPalette.orange70,

    // Secondary
    secondaryWeak = ColorPalette.gray70,
    secondaryNormal = ColorPalette.gray80,
    secondaryStrong = ColorPalette.gray90,
    secondaryHeavy = ColorPalette.gray95,

    // Accent
    accentOrange = ColorPalette.orange50,
    accentRed = ColorPalette.red50,
    accentYellow = ColorPalette.yellow50,
    accentLime = ColorPalette.lime50,
    accentGreen = ColorPalette.green50,
    accentCyan = ColorPalette.cyan50,
    accentBlue = ColorPalette.blue50,
    accentPurple = ColorPalette.purple50,

    // Background
    backgroundPrimary = ColorPalette.white,
    backgroundSecondary = ColorPalette.gray5,
    backgroundTertiary = ColorPalette.gray10,
    backgroundInverse = ColorPalette.gray95,

    // Surface
    surfacePrimary = ColorPalette.white,
    surfaceSecondary = ColorPalette.gray5,
    surfaceTertiary = ColorPalette.gray10,
    surfaceQuaternary = ColorPalette.gray20,
    surfaceInverse = ColorPalette.gray95,
    surfaceBrandPrimary = ColorPalette.orange5,

    // Border
    borderNormal = ColorPalette.gray10,
    borderStrong = ColorPalette.gray20,

    // Divider
    dividerNormal = ColorPalette.gray5,
    dividerStrong = ColorPalette.gray10,

    // Status - Information
    statusInformationGhost = ColorPalette.cyan5,
    statusInformationWeak = ColorPalette.cyan10,
    statusInformationNormal = ColorPalette.cyan50,
    statusInformationStrong = ColorPalette.cyan60,

    // Status - Positive
    statusPositiveGhost = ColorPalette.green5,
    statusPositiveWeak = ColorPalette.green10,
    statusPositiveNormal = ColorPalette.green50,
    statusPositiveStrong = ColorPalette.green60,

    // Status - Negative
    statusNegativeGhost = ColorPalette.red5,
    statusNegativeWeak = ColorPalette.red10,
    statusNegativeNormal = ColorPalette.red50,
    statusNegativeStrong = ColorPalette.red60,

    // Status - Warning
    statusWarningGhost = ColorPalette.yellow5,
    statusWarningWeak = ColorPalette.yellow10,
    statusWarningNormal = ColorPalette.yellow50,
    statusWarningStrong = ColorPalette.yellow60,

    // Notification
    notificationRed = ColorPalette.red50,
)

// TODO: Dark mode colors - pending design team's color palette
internal val DarkNeveraColors = NeveraColor(
    // Text
    textPrimary = ColorPalette.gray95,
    textSecondary = ColorPalette.gray80,
    textTertiary = ColorPalette.gray60,
    textQuaternary = ColorPalette.gray50,
    textCaption = ColorPalette.gray40,
    textDisabled = ColorPalette.gray30,
    textInverse = ColorPalette.white,

    // Icon
    iconPrimary = ColorPalette.gray80,
    iconSecondary = ColorPalette.gray70,
    iconTertiary = ColorPalette.gray60,
    iconQuaternary = ColorPalette.gray40,
    iconCaption = ColorPalette.gray30,
    iconDisabled = ColorPalette.gray20,
    iconInverse = ColorPalette.white,

    // Primary
    primaryWeak = ColorPalette.orange40,
    primaryNormal = ColorPalette.orange50,
    primaryStrong = ColorPalette.orange60,
    primaryHeavy = ColorPalette.orange70,

    // Secondary
    secondaryWeak = ColorPalette.gray70,
    secondaryNormal = ColorPalette.gray80,
    secondaryStrong = ColorPalette.gray90,
    secondaryHeavy = ColorPalette.gray95,

    // Accent
    accentOrange = ColorPalette.orange50,
    accentRed = ColorPalette.red50,
    accentYellow = ColorPalette.yellow50,
    accentLime = ColorPalette.lime50,
    accentGreen = ColorPalette.green50,
    accentCyan = ColorPalette.cyan50,
    accentBlue = ColorPalette.blue50,
    accentPurple = ColorPalette.purple50,

    // Background
    backgroundPrimary = ColorPalette.white,
    backgroundSecondary = ColorPalette.gray5,
    backgroundTertiary = ColorPalette.gray10,
    backgroundInverse = ColorPalette.gray95,

    // Surface
    surfacePrimary = ColorPalette.white,
    surfaceSecondary = ColorPalette.gray5,
    surfaceTertiary = ColorPalette.gray10,
    surfaceQuaternary = ColorPalette.gray20,
    surfaceInverse = ColorPalette.gray95,
    surfaceBrandPrimary = ColorPalette.orange5,

    // Border
    borderNormal = ColorPalette.gray10,
    borderStrong = ColorPalette.gray20,

    // Divider
    dividerNormal = ColorPalette.gray5,
    dividerStrong = ColorPalette.gray10,

    // Status - Information
    statusInformationGhost = ColorPalette.cyan5,
    statusInformationWeak = ColorPalette.cyan10,
    statusInformationNormal = ColorPalette.cyan50,
    statusInformationStrong = ColorPalette.cyan60,

    // Status - Positive
    statusPositiveGhost = ColorPalette.green5,
    statusPositiveWeak = ColorPalette.green10,
    statusPositiveNormal = ColorPalette.green50,
    statusPositiveStrong = ColorPalette.green60,

    // Status - Negative
    statusNegativeGhost = ColorPalette.red5,
    statusNegativeWeak = ColorPalette.red10,
    statusNegativeNormal = ColorPalette.red50,
    statusNegativeStrong = ColorPalette.red60,

    // Status - Warning
    statusWarningGhost = ColorPalette.yellow5,
    statusWarningWeak = ColorPalette.yellow10,
    statusWarningNormal = ColorPalette.yellow50,
    statusWarningStrong = ColorPalette.yellow60,

    // Notification
    notificationRed = ColorPalette.red50,
)

internal val LocalNeveraColors = staticCompositionLocalOf { LightNeveraColors }
