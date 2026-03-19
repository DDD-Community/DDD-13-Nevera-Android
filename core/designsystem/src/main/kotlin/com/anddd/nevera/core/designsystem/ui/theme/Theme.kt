package com.anddd.nevera.core.designsystem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import com.anddd.nevera.core.designsystem.ui.theme.color.DarkNeveraColors
import com.anddd.nevera.core.designsystem.ui.theme.color.LightNeveraColors
import com.anddd.nevera.core.designsystem.ui.theme.color.LocalNeveraColors
import com.anddd.nevera.core.designsystem.ui.theme.color.NeveraColor
import com.anddd.nevera.core.designsystem.ui.theme.shape.NeveraRadius
import com.anddd.nevera.core.designsystem.ui.theme.spacing.NeveraSpacing
import com.anddd.nevera.core.designsystem.ui.theme.typography.NeveraTypography

@Composable
fun NeveraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val neveraColors = when {
        darkTheme -> DarkNeveraColors
        else -> LightNeveraColors
    }

    CompositionLocalProvider(
        LocalNeveraColors provides neveraColors
    ) {
        MaterialTheme(
            content = content
        )
    }
}

object NeveraTheme {
    val colors: NeveraColor
        @Composable
        @ReadOnlyComposable
        get() = LocalNeveraColors.current

    val spacing: NeveraSpacing
        get() = NeveraSpacing

    val radius: NeveraRadius
        get() = NeveraRadius

    val typography: NeveraTypography
        get() = NeveraTypography
}