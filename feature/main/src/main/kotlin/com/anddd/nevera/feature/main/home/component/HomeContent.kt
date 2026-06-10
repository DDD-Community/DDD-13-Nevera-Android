package com.anddd.nevera.feature.main.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarAction
import com.anddd.nevera.core.designsystem.component.appbar.NeveraLogoAppBar
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonSize
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.main.R
import com.anddd.nevera.feature.main.home.model.HomeIntent
import com.anddd.nevera.feature.main.home.model.HomeUiState

private val HorizontalDividerHeight = 8.dp

@Composable
internal fun HomeContent(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            NeveraLogoAppBar(
                action = NeveraAppBarAction.Icons.of(
                    NeveraAppBarAction.Icons.Item(
                        painter = if (uiState.hasUnreadNotification) NeveraIcons.BellOn else NeveraIcons.Bell,
                        contentDescription = stringResource(R.string.home_notification_icon_description),
                        onClick = { onIntent(HomeIntent.NotificationIconClicked) },
                    ),
                ),
            )
        },
        floatingActionButton = {
            NeveraFilledButton(
                label = stringResource(R.string.home_fab_label),
                onClick = { onIntent(HomeIntent.AddIngredientClick) },
                size = NeveraButtonSize.Medium,
                shape = RoundedCornerShape(NeveraTheme.radius.max)
            )
        },
        containerColor = NeveraTheme.colors.surfacePrimary,
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
            ) {
                item(key = "wish_banner") {
                    WishBanner(
                        nickname = uiState.profile.nickname,
                        wish = uiState.wish,
                        onCreateWish = { onIntent(HomeIntent.CreateWishClick) },
                        onEditWish = { onIntent(HomeIntent.WishEditClick) },
                        modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding20),
                    )
                }
                item(key = "wish_banner_spacer") {
                    Spacer(Modifier.height(NeveraTheme.spacing.gap16))
                }
                item(key = "cost_card") {
                    RescueDisposalCostCard(
                        rescueAmount = uiState.savings.rescuedAmount,
                        disposalAmount = uiState.savings.disposalAmount,
                        modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding20),
                    )
                }
                item(key = "cost_card_spacer") {
                    Spacer(Modifier.height(NeveraTheme.spacing.gap20))
                }
                item(key = "divider") {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = HorizontalDividerHeight,
                        color = NeveraTheme.colors.dividerNormal,
                    )
                }
                item(key = "divider_spacer") {
                    Spacer(Modifier.height(NeveraTheme.spacing.gap20))
                }
                recentIngredientSection(
                    selectedTab = uiState.ingredientFilterTab,
                    rescuedIngredients = uiState.rescuedIngredients,
                    disposalIngredients = uiState.disposalIngredients,
                    listState = listState,
                    onTabSelected = { tab ->
                        onIntent(HomeIntent.RecentIngredientTabClick(tab))
                    },
                    onLoadMore = { onIntent(HomeIntent.LoadMoreIngredients(uiState.ingredientFilterTab)) },
                )
            }

            if (uiState.isLoading) {
                LoadingContent()
            }
        }
    }
}

@Preview(
    name = "HomeContent",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun HomeContentPreview() {
    NeveraTheme {
        HomeContent(
            uiState = HomeUiState(),
            onIntent = {},
        )
    }
}
