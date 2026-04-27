package com.anddd.nevera.core.designsystem.component.appbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
internal fun AppBarNavigationSlot(navigation: AppBarNavigation) {
    when (navigation) {
        is AppBarNavigation.Back -> AppBarIconButton(
            painter = NeveraIcons.ArrowBack,
            onClick = navigation.onClick,
            contentDescription = "뒤로가기",
        )

        is AppBarNavigation.Close -> AppBarIconButton(
            painter = NeveraIcons.Close,
            onClick = navigation.onClick,
            contentDescription = "닫기",
        )

        is AppBarNavigation.Menu -> AppBarIconButton(
            painter = NeveraIcons.Menu,
            onClick = navigation.onClick,
            contentDescription = "메뉴",
        )

        AppBarNavigation.None -> Unit
    }
}

@Composable
internal fun AppBarActionSlot(action: AppBarAction) {
    when (action) {
        is AppBarAction.Icons -> Row(horizontalArrangement = Arrangement.spacedBy(AppBarDefault.actionSpacing)) {
            action.items.forEach { item ->
                AppBarIconButton(
                    painter = item.painter,
                    onClick = item.onClick,
                    contentDescription = item.contentDescription,
                )
            }
        }

        is AppBarAction.Text -> {
            val textColor = when (action.tone) {
                AppBarAction.Text.Tone.Primary -> NeveraTheme.colors.primaryNormal
                AppBarAction.Text.Tone.Tertiary -> NeveraTheme.colors.textTertiary
            }

            TextButton(onClick = action.onClick) {
                Text(
                    text = action.label,
                    style = NeveraTheme.typography.titleXSmall,
                    color = textColor,
                )
            }
        }

        AppBarAction.None -> Unit
    }
}

@Composable
private fun AppBarIconButton(
    painter: Painter,
    onClick: () -> Unit,
    contentDescription: String? = null,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(AppBarDefault.iconButtonSize),
        ) {
            Icon(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier.size(AppBarDefault.iconSize),
                tint = NeveraTheme.colors.iconPrimary
            )
        }
    }
}
