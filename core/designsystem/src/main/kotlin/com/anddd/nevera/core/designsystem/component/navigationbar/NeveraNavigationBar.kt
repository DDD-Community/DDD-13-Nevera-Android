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
fun NeveraNavigationBar(
    items: List<NeveraNavigationBarItem>,
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
                modifier = Modifier.weight(1f),
            )
        }
    }
}

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
                    selectedIcon = NeveraIcons.NavHomeFilled,
                    unselectedIcon = NeveraIcons.NavHome,
                    selected = true,
                    onClick = {},
                ),
                NeveraNavigationBarItem(
                    selectedIcon = NeveraIcons.NavFridgeFilled,
                    unselectedIcon = NeveraIcons.NavFridge,
                    selected = false,
                    onClick = {},
                ),
                NeveraNavigationBarItem(
                    selectedIcon = NeveraIcons.NavMyFilled,
                    unselectedIcon = NeveraIcons.NavMy,
                    selected = false,
                    onClick = {},
                ),
            ),
        )
    }
}
