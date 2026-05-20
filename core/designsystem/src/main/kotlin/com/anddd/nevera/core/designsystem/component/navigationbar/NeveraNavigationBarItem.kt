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

data class NeveraNavigationBarItem(
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    val selected: Boolean,
    val onClick: () -> Unit,
)

@Composable
internal fun NavigationBarItem(
    item: NeveraNavigationBarItem,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(NeveraNavigationBarDefault.iconHeight)
            .clickable(onClick = item.onClick),
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
