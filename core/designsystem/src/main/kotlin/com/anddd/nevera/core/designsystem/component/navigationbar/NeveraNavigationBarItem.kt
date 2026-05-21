package com.anddd.nevera.core.designsystem.component.navigationbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

data class NeveraNavigationBarItem<T>(
    val tag: T,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    val selected: Boolean,
)

@Composable
internal fun <T> NavigationBarItem(
    item: NeveraNavigationBarItem<T>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(NeveraNavigationBarDefault.iconHeight)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = if (item.selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(NeveraNavigationBarDefault.iconSize),
        )
    }
}
