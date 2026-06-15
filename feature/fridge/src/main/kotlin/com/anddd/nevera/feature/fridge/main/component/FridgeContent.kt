package com.anddd.nevera.feature.fridge.main.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarAction
import com.anddd.nevera.core.designsystem.component.appbar.NeveraDisplayAppBar
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonSize
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.EmptyContent
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import com.anddd.nevera.feature.fridge.R
import com.anddd.nevera.feature.fridge.main.model.FridgeIngredientUiModel
import com.anddd.nevera.feature.fridge.main.model.FridgeIntent
import com.anddd.nevera.feature.fridge.main.model.FridgeUiState
import com.anddd.nevera.feature.fridge.main.model.StorageLocationFilter
import java.time.LocalDate

/**
 * 식재료 아이템 목록([uiState.ingredients]) 앞에 위치한 고정 아이템 개수.
 * 특정 식재료로 스크롤할 때 인덱스 보정에 사용한다.
 */
internal const val FRIDGE_LIST_HEADER_ITEM_COUNT = 5

@Composable
internal fun FridgeContent(
    uiState: FridgeUiState,
    onIntent: (FridgeIntent) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            NeveraDisplayAppBar(
                title = stringResource(R.string.fridge_title),
                action = NeveraAppBarAction.Icons.of(
                    NeveraAppBarAction.Icons.Item(
                        painter = if (uiState.hasUnreadNotification) NeveraIcons.BellOn else NeveraIcons.Bell,
                        contentDescription = stringResource(R.string.fridge_notification_icon_desc),
                        onClick = { onIntent(FridgeIntent.NotificationIconClicked) },
                    ),
                ),
            )
        },
        floatingActionButton = {
            NeveraFilledButton(
                label = stringResource(R.string.fridge_fab_label),
                onClick = { onIntent(FridgeIntent.AddIngredientClick) },
                size = NeveraButtonSize.Medium,
                shape = RoundedCornerShape(NeveraTheme.radius.max),
            )
        },
        containerColor = NeveraTheme.colors.surfacePrimary,
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                item {
                    Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap16))
                }
                item {
                    FridgeFilterTabRow(
                        selectedFilter = uiState.selectedStorageFilter,
                        onFilterSelected = { onIntent(FridgeIntent.SelectStorageFilter(it)) },
                        modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding16),
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap16))
                }
                item {
                    FridgeCategoryFilterRow(
                        selectedFilter = uiState.selectedCategoryFilter,
                        onFilterSelected = { onIntent(FridgeIntent.SelectCategoryFilter(it)) },
                    )
                }
                item {
                    FridgeIngredientListHeader(
                        totalCount = uiState.totalCount,
                        selectedStorageFilter = uiState.selectedStorageFilter,
                        selectedSortOrder = uiState.selectedSortOrder,
                        onSortOrderSelected = { onIntent(FridgeIntent.SelectSortOrder(it)) },
                        modifier = Modifier.padding(top = NeveraTheme.spacing.gap8),
                    )
                }
                if (uiState.ingredients.isEmpty()) {
                    item {
                        EmptyContent(
                            message = stringResource(R.string.fridge_list_empty_message),
                            modifier = Modifier.padding(vertical = NeveraTheme.spacing.gap40),
                        )
                    }
                } else {
                    // TODO: 페이지네이션 아이템 목록
                    items(uiState.ingredients, key = { it.id }) { item ->
                        FridgeIngredientItem(
                            item = item,
                            onRescueClick = { onIntent(FridgeIntent.RescueClick(item)) },
                            onDisposeClick = { onIntent(FridgeIntent.DisposeClick(item)) },
                            onMoreClick = { onIntent(FridgeIntent.IngredientMoreClick(item)) },
                        )
                    }
                }
            }
            if (uiState.isLoading) {
                LoadingContent()
            }
        }
    }
}

private val mockIngredients = listOf(
    FridgeIngredientUiModel(id = 1L, name = "제주 햇당근", category = FoodCategory.Veg, quantity = 1, cost = 6500, expiryDate = LocalDate.now().plusDays(28)),
    FridgeIngredientUiModel(id = 2L, name = "삼겹살", category = FoodCategory.MeatEggs, quantity = 1, cost = 0, expiryDate = LocalDate.now().minusDays(3)),
    FridgeIngredientUiModel(id = 3L, name = "서울우유 1L", category = FoodCategory.Dairy, quantity = 2, cost = 3200, expiryDate = LocalDate.now().plusDays(5)),
    FridgeIngredientUiModel(id = 4L, name = "새우", category = FoodCategory.Sea, quantity = 3, cost = 12000, expiryDate = LocalDate.now().plusDays(1)),
    FridgeIngredientUiModel(id = 5L, name = "청포도", category = FoodCategory.Fruit, quantity = 1, cost = 4500, expiryDate = LocalDate.now().plusDays(14)),
    FridgeIngredientUiModel(id = 6L, name = "간장", category = FoodCategory.Sauce, quantity = 1, cost = 2800, expiryDate = LocalDate.now().plusDays(180)),
    FridgeIngredientUiModel(id = 7L, name = "콜라 1.5L", category = FoodCategory.Drink, quantity = 2, cost = 1800, expiryDate = LocalDate.now().plusDays(90)),
    FridgeIngredientUiModel(id = 8L, name = "두부", category = FoodCategory.Processed, quantity = 1, cost = 1500, expiryDate = LocalDate.now()),
    FridgeIngredientUiModel(id = 9L, name = "계란 10구", category = FoodCategory.MeatEggs, quantity = 10, cost = 3000, expiryDate = LocalDate.now().minusDays(1)),
    FridgeIngredientUiModel(id = 10L, name = "오징어", category = FoodCategory.Sea, quantity = 2, cost = 8000, expiryDate = LocalDate.now().plusDays(3)),
)

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun FridgeContentPreview() {
    NeveraTheme {
        FridgeContent(
            uiState = FridgeUiState(ingredients = mockIngredients),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun FridgeContentFridgeSelectedPreview() {
    NeveraTheme {
        FridgeContent(
            uiState = FridgeUiState(
                selectedStorageFilter = StorageLocationFilter.Specific(StorageLocation.Fridge),
                ingredients = mockIngredients,
            ),
            onIntent = {},
        )
    }
}
