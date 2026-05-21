package com.anddd.nevera.core.designsystem.component.navigationbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
fun <T> NeveraNavigationBar(
    items: List<NeveraNavigationBarItem<T>>,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(NeveraNavigationBarDefault.height)
            .background(NeveraNavigationBarDefault.backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            NavigationBarItem(
                item = item,
                onClick = { onItemClick(item.tag) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private enum class PreviewTab { HOME, FRIDGE, MY_PAGE }

@Preview(
    name = "NeveraNavigationBar",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraNavigationBarPreview() {
    NeveraTheme {
        NeveraNavigationBar(
            items = listOf(
                NeveraNavigationBarItem(
                    tag = PreviewTab.HOME,
                    selectedIcon = NeveraIcons.NavHomeFilled,
                    unselectedIcon = NeveraIcons.NavHome,
                    selected = true,
                ),
                NeveraNavigationBarItem(
                    tag = PreviewTab.FRIDGE,
                    selectedIcon = NeveraIcons.NavFridgeFilled,
                    unselectedIcon = NeveraIcons.NavFridge,
                    selected = false,
                ),
                NeveraNavigationBarItem(
                    tag = PreviewTab.MY_PAGE,
                    selectedIcon = NeveraIcons.NavMyFilled,
                    unselectedIcon = NeveraIcons.NavMy,
                    selected = false,
                ),
            ),
            onItemClick = {},
        )
    }
}
