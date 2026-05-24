package com.anddd.nevera.feature.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.main.R
import com.anddd.nevera.feature.main.home.model.IngredientFilterTab
import com.anddd.nevera.feature.main.home.model.IngredientFilterTabUiModel
import com.anddd.nevera.feature.main.home.model.toUiModel

private val TabContainerPadding = 4.dp
private val TabHeight = 44.dp
private val EmptyIconSize = 64.dp

fun LazyListScope.recentIngredientSection(
    selectedTab: IngredientFilterTab,
    onTabSelected: (IngredientFilterTab) -> Unit,
    onHelpClick: () -> Unit,
) {
    item(key = "ingredient_header") {
        RecentIngredientSectionHeader(
            onHelpClick = onHelpClick,
            modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding20),
        )
    }
    item(key = "ingredient_header_spacer") {
        Spacer(Modifier.height(NeveraTheme.spacing.gap16))
    }
    item(key = "ingredient_tab") {
        IngredientFilterTabRow(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding20),
        )
    }
    item(key = "ingredient_tab_spacer") {
        Spacer(Modifier.height(NeveraTheme.spacing.gap20))
    }
    item(key = "ingredient_empty") {
        IngredientEmptyContent()
    }
    // 향후: items(ingredients, key = { it.id }) { IngredientItem(it) }
}

@Composable
private fun RecentIngredientSectionHeader(
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap4)
    ) {
        Text(
            text = stringResource(R.string.home_ingredient_section_title),
            style = NeveraTheme.typography.titleMedium,
            color = NeveraTheme.colors.textSecondary,
        )
        Image(
            painter = NeveraIcons.CircleHelp,
            contentDescription = stringResource(R.string.home_ingredient_section_help_description),
            modifier = Modifier
                .size(NeveraTheme.iconSize.small)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onHelpClick,
                ),
        )
    }
}

@Composable
private fun IngredientFilterTabRow(
    selectedTab: IngredientFilterTab,
    onTabSelected: (IngredientFilterTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerShape = RoundedCornerShape(NeveraTheme.radius.medium)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(containerShape)
            .background(NeveraTheme.colors.surfaceSecondary)
            .padding(TabContainerPadding),
    ) {
        IngredientFilterTab.entries.toUiModel().forEach { tab ->
            IngredientFilterTabItem(
                tab = tab,
                isSelected = tab.type == selectedTab,
                onClick = { onTabSelected(tab.type) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun IngredientFilterTabItem(
    tab: IngredientFilterTabUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabShape = RoundedCornerShape(NeveraTheme.radius.medium)
    val iconRes = if (isSelected) tab.selectedIconRes else tab.unselectedIconRes
    val textColor = if (isSelected) NeveraTheme.colors.textSecondary else NeveraTheme.colors.textCaption

    Row(
        modifier = modifier
            .height(TabHeight)
            .then(
                if (isSelected) Modifier
                    .clip(tabShape)
                    .background(NeveraTheme.colors.surfacePrimary)
                else Modifier
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(NeveraTheme.iconSize.xSmall),
        )
        Text(
            text = stringResource(tab.labelRes),
            style = NeveraTheme.typography.titleXSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding4)
        )
    }
}

@Composable
private fun IngredientEmptyContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = NeveraTheme.spacing.padding40),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap16),
    ) {
        Image(
            painter = NeveraIcons.EmptyStateWarning,
            contentDescription = null,
            modifier = Modifier.size(EmptyIconSize),
        )
        Text(
            text = stringResource(R.string.home_ingredient_empty_message),
            style = NeveraTheme.typography.titleSmall,
            color = NeveraTheme.colors.textCaption,
        )
    }
}

@Preview(
    name = "RecentIngredientSection - Rescue",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun RecentIngredientSectionRescuePreview() {
    NeveraTheme {
        LazyColumn {
            recentIngredientSection(
                selectedTab = IngredientFilterTab.Rescue,
                onTabSelected = {},
                onHelpClick = {},
            )
        }
    }
}

@Preview(
    name = "RecentIngredientSection - Disposal",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun RecentIngredientSectionDisposalPreview() {
    NeveraTheme {
        LazyColumn {
            recentIngredientSection(
                selectedTab = IngredientFilterTab.Disposal,
                onTabSelected = {},
                onHelpClick = {},
            )
        }
    }
}
