package com.anddd.nevera.core.designsystem.component.button

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

enum class NeveraButtonSize { XSmall, Small, Medium, Large, }

internal data class ButtonSizeSpec(
    val height: Dp,
    val horizontalPadding: Dp,
)

internal data class IconButtonSizeSpec(
    val size: Dp,
    val iconSize: Dp,
)

internal fun NeveraButtonSize.spec(): ButtonSizeSpec = when (this) {
    NeveraButtonSize.XSmall -> ButtonSizeSpec(height = 28.dp, horizontalPadding = 10.dp)

    NeveraButtonSize.Small -> ButtonSizeSpec(height = 34.dp, horizontalPadding = 14.dp)

    NeveraButtonSize.Medium -> ButtonSizeSpec(height = 40.dp, horizontalPadding = 18.dp)

    NeveraButtonSize.Large -> ButtonSizeSpec(height = 48.dp, horizontalPadding = 22.dp)
}

internal fun NeveraButtonSize.iconSpec(): IconButtonSizeSpec = when (this) {
    NeveraButtonSize.XSmall -> IconButtonSizeSpec(size = 28.dp, iconSize = 12.dp)

    NeveraButtonSize.Small -> IconButtonSizeSpec(size = 34.dp, iconSize = 16.dp)

    NeveraButtonSize.Medium -> IconButtonSizeSpec(size = 40.dp, iconSize = 20.dp)

    NeveraButtonSize.Large -> IconButtonSizeSpec(size = 48.dp, iconSize = 24.dp)

}

internal fun NeveraButtonSize.textStyle(): TextStyle = when (this) {
    NeveraButtonSize.XSmall -> NeveraTheme.typography.captionStrong

    NeveraButtonSize.Small -> NeveraTheme.typography.titleXSmall

    NeveraButtonSize.Medium -> NeveraTheme.typography.titleSmall

    NeveraButtonSize.Large -> NeveraTheme.typography.titleMedium
}
