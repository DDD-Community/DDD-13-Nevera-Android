package com.anddd.nevera.feature.main.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.EmptyContent
import com.anddd.nevera.feature.main.R
import com.anddd.nevera.feature.main.home.model.IngredientFilterTab
import com.anddd.nevera.feature.main.home.model.IngredientFilterTabUiModel
import com.anddd.nevera.feature.main.home.model.IngredientUiModel
import com.anddd.nevera.feature.main.home.model.PaginatedListState
import com.anddd.nevera.feature.main.home.model.toUiModel

private val TabContainerPadding = 4.dp
private val TabHeight = 44.dp
private const val PREFETCH_THRESHOLD = 3

fun LazyListScope.recentIngredientSection(
    selectedTab: IngredientFilterTab,
    rescuedIngredients: PaginatedListState<IngredientUiModel>,
    disposalIngredients: PaginatedListState<IngredientUiModel>,
    listState: LazyListState,
    onTabSelected: (IngredientFilterTab) -> Unit,
    onLoadMore: () -> Unit,
) {
    item(key = "ingredient_header") {
        RecentIngredientSectionHeader(
            modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding20),
        )
    }
    item(key = "ingredient_header_spacer") {
        Spacer(Modifier.height(NeveraTheme.spacing.gap16))
    }
    item(key = "ingredient_tab") {
        val currentIngredients = when (selectedTab) {
            IngredientFilterTab.Rescue -> rescuedIngredients
            IngredientFilterTab.Disposal -> disposalIngredients
        }
        IngredientLoadMoreEffect(
            listState = listState,
            currentIngredients = currentIngredients,
            onLoadMore = onLoadMore,
        )
        IngredientFilterTabRow(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding20),
        )
    }
    item(key = "ingredient_tab_spacer") {
        Spacer(Modifier.height(NeveraTheme.spacing.gap20))
    }
    ingredientItems(
        selectedTab = selectedTab,
        rescuedIngredients = rescuedIngredients,
        disposalIngredients = disposalIngredients,
    )
}

private fun LazyListScope.ingredientItems(
    selectedTab: IngredientFilterTab,
    rescuedIngredients: PaginatedListState<IngredientUiModel>,
    disposalIngredients: PaginatedListState<IngredientUiModel>,
) {
    val visibleIngredients = when (selectedTab) {
        IngredientFilterTab.Rescue -> rescuedIngredients.items
        IngredientFilterTab.Disposal -> disposalIngredients.items
    }
    if (visibleIngredients.isEmpty()) {
        item(key = "ingredient_empty") {
            EmptyContent(
                message = stringResource(R.string.home_ingredient_empty_message),
                modifier = Modifier.padding(vertical = NeveraTheme.spacing.padding40),
            )
        }
    } else {
        items(items = visibleIngredients, key = { it.id }) { ingredient ->
            IngredientItem(
                ingredient = ingredient,
                modifier = Modifier.padding(
                    horizontal = NeveraTheme.spacing.padding20,
                    vertical = NeveraTheme.spacing.gap8,
                ),
            )
        }
    }
}

@Composable
private fun RecentIngredientSectionHeader(
    modifier: Modifier = Modifier,
) {
    var showTooltip by remember { mutableStateOf(false) }
    val density = LocalDensity.current

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
        Box {
            Icon(
                painter = NeveraIcons.CircleHelp,
                contentDescription = stringResource(R.string.home_ingredient_section_help_description),
                modifier = Modifier
                    .size(NeveraTheme.iconSize.small)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { showTooltip = !showTooltip },
                    ),
                tint = NeveraTheme.colors.iconCaption,
            )
            if (showTooltip) {
                Popup(
                    popupPositionProvider = remember(density) {
                        object : PopupPositionProvider {
                            override fun calculatePosition(
                                anchorBounds: IntRect,
                                windowSize: IntSize,
                                layoutDirection: LayoutDirection,
                                popupContentSize: IntSize,
                            ): IntOffset = with(density) {
                                IntOffset(
                                    x = anchorBounds.right + 4.dp.roundToPx(),
                                    y = anchorBounds.top + anchorBounds.height / 2 - popupContentSize.height / 2,
                                )
                            }
                        }
                    },
                ) {
                    IngredientHelpTooltip()
                }
            }
        }
    }
}

@Composable
private fun IngredientHelpTooltip() {
    val bgColor = NeveraTheme.colors.secondaryNormal
    Row(
        modifier = Modifier.widthIn(max = 240.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(modifier = Modifier.size(width = 6.dp, height = 12.dp)) {
            drawPath(
                path = Path().apply {
                    moveTo(size.width, 0f)
                    lineTo(0f, size.height / 2f)
                    lineTo(size.width, size.height)
                    close()
                },
                color = bgColor,
            )
        }
        Box(
            modifier = Modifier
                .background(bgColor, RoundedCornerShape(NeveraTheme.radius.small))
                .padding(horizontal = NeveraTheme.spacing.padding12, vertical = NeveraTheme.spacing.gap8),
        ) {
            Text(
                text = stringResource(R.string.home_ingredient_section_tooltip),
                style = NeveraTheme.typography.bodyMedium,
                color = NeveraTheme.colors.textInverse,
            )
        }
    }
}

@Composable
private fun IngredientFilterTabRow(
    selectedTab: IngredientFilterTab,
    onTabSelected: (IngredientFilterTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(NeveraTheme.radius.medium))
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
private fun IngredientLoadMoreEffect(
    listState: LazyListState,
    currentIngredients: PaginatedListState<IngredientUiModel>,
    onLoadMore: () -> Unit,
) {
    val currentIngredientsState = rememberUpdatedState(currentIngredients)
    val shouldLoadMore by remember {
        derivedStateOf {
            val ingredients = currentIngredientsState.value
            if (ingredients.items.isEmpty()) return@derivedStateOf false
            val lastFiveIds = ingredients.items.takeLast(PREFETCH_THRESHOLD).map { it.id }
            listState.layoutInfo.visibleItemsInfo.any { it.key in lastFiveIds }
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
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
        val listState = rememberLazyListState()
        LazyColumn(state = listState) {
            recentIngredientSection(
                selectedTab = IngredientFilterTab.Rescue,
                rescuedIngredients = PaginatedListState(),
                disposalIngredients = PaginatedListState(),
                listState = listState,
                onTabSelected = {},
                onLoadMore = {},
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
        val listState = rememberLazyListState()
        LazyColumn(state = listState) {
            recentIngredientSection(
                selectedTab = IngredientFilterTab.Disposal,
                rescuedIngredients = PaginatedListState(),
                disposalIngredients = PaginatedListState(),
                listState = listState,
                onTabSelected = {},
                onLoadMore = {},
            )
        }
    }
}
