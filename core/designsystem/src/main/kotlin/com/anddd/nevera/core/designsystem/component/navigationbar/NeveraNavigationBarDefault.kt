package com.anddd.nevera.core.designsystem.component.navigationbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

internal object NeveraNavigationBarDefault {
    val height = 60.dp
    val iconSize = NeveraTheme.iconSize.medium
    val iconHeight = 40.dp
    val backgroundColor
        @Composable get() = NeveraTheme.colors.backgroundPrimary
}
