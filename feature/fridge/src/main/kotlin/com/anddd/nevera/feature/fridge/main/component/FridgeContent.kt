package com.anddd.nevera.feature.fridge.main.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarAction
import com.anddd.nevera.core.designsystem.component.appbar.NeveraDisplayAppBar
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonSize
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.EmptyContent
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import com.anddd.nevera.feature.fridge.R
import com.anddd.nevera.feature.fridge.main.model.FridgeIntent
import com.anddd.nevera.feature.fridge.main.model.FridgeUiState
import com.anddd.nevera.feature.fridge.main.model.StorageLocationFilter

@Composable
internal fun FridgeContent(
    uiState: FridgeUiState,
    onIntent: (FridgeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            NeveraDisplayAppBar(
                title = stringResource(R.string.fridge_title),
                action = NeveraAppBarAction.Icons.of(
                    NeveraAppBarAction.Icons.Item(
                        painter = NeveraIcons.Bell,
                        contentDescription = stringResource(R.string.fridge_notification_icon_desc),
                        onClick = {},
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
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
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
                // TODO: 페이지네이션 아이템 목록
                item {
                    EmptyContent(
                        message = stringResource(R.string.fridge_list_empty_message),
                        modifier = Modifier.height(400.dp),
                    )
                }
            }
            if (uiState.isLoading) {
                LoadingContent()
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun FridgeContentPreview() {
    NeveraTheme {
        FridgeContent(
            uiState = FridgeUiState(),
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
            ),
            onIntent = {},
        )
    }
}
