package com.anddd.nevera.feature.ingredient.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonColor
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.component.button.NeveraWeakIconButton
import com.anddd.nevera.core.designsystem.component.datepicker.NeveraDatePickerDialog
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import com.anddd.nevera.feature.ingredient.R
import com.anddd.nevera.feature.ingredient.main.component.ingredient.IngredientItemCard
import com.anddd.nevera.feature.ingredient.main.model.IngredientIntent
import com.anddd.nevera.feature.ingredient.main.model.IngredientUiModel
import com.anddd.nevera.feature.ingredient.main.model.IngredientUiState
import java.time.LocalDate
import androidx.core.net.toUri
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonSize

// ─── 상수 ──────────────────────────────────────────────────────────────────
private val ScannedImageSize = 72.dp
private val ScannedImageBorderWidth = 1.dp
private val SectionDividerHeight = 8.dp

// ─── 바텀시트 / 다이얼로그 상태 ───────────────────────────────────────────────
private sealed interface IngredientEditState {
    data object None : IngredientEditState
    data class EditingCategory(val itemId: String) : IngredientEditState
    data class EditingLocation(val itemId: String) : IngredientEditState
    data class EditingDate(val itemId: String) : IngredientEditState
}

/**
 * 식재료 목록 편집 콘텐츠
 *
 * @param uiState   현재 UI 상태
 * @param listState 리스트 스크롤 상태
 * @param onIntent  Intent 전달 콜백
 * @param modifier  외부 Modifier
 */
@Composable
internal fun IngredientContent(
    uiState: IngredientUiState,
    listState: LazyListState,
    onIntent: (IngredientIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editState by remember { mutableStateOf<IngredientEditState>(IngredientEditState.None) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NeveraTheme.colors.backgroundPrimary)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = NeveraTheme.spacing.padding16,
                    end = NeveraTheme.spacing.padding16,
                    top = NeveraTheme.spacing.padding16,
                    bottom = NeveraTheme.spacing.gap32,
                ),
                verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap16),
            ) {
                // 헤더: 타이틀 + 부제목 + 이미지 썸네일
                item {
                    IngredientListHeader(
                        imageUri = uiState.imageUri,
                        onImageClick = { onIntent(IngredientIntent.ImageClick) },
                    )
                }

                // 식재료 카드 목록
                items(uiState.items, key = { it.id }) { item ->
                    IngredientItemCard(
                        item = item,
                        onSelectionChanged = { isSelected ->
                            onIntent(IngredientIntent.UpdateItem(item.copy(isSelected = isSelected)))
                        },
                        onQuantityChanged = { qty ->
                            onIntent(IngredientIntent.UpdateItem(item.copy(quantity = qty)))
                        },
                        onCostChanged = { cost ->
                            onIntent(IngredientIntent.UpdateItem(item.copy(cost = cost)))
                        },
                        onCategoryClick = {
                            editState = IngredientEditState.EditingCategory(item.id)
                        },
                        onLocationClick = {
                            editState = IngredientEditState.EditingLocation(item.id)
                        },
                        onDateClick = {
                            editState = IngredientEditState.EditingDate(item.id)
                        },
                    )
                }

                // 직접 등록 버튼 (리스트 마지막 아이템)
                item {
                    AddItemButton(
                        onClick = { onIntent(IngredientIntent.AddEmptyItem) }
                    )
                }
            }

            // 하단 고정 버튼 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(NeveraTheme.spacing.padding16),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                NeveraFilledButton(
                    label = stringResource(R.string.ingredient_register_button, uiState.selectedItems.size),
                    onClick = { onIntent(IngredientIntent.Register) },
                    enabled = uiState.isRegisterEnabled,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // 바텀시트 / 다이얼로그
        when (val state = editState) {
            is IngredientEditState.EditingCategory -> {
                val item = uiState.items.find { it.id == state.itemId }
                    ?: run { editState = IngredientEditState.None; return@Box }
                CategoryBottomSheet(
                    selectedCategory = item.category,
                    onCategorySelected = { category ->
                        onIntent(IngredientIntent.UpdateItem(item.copy(category = category)))
                    },
                    onDismiss = { editState = IngredientEditState.None },
                )
            }
            is IngredientEditState.EditingLocation -> {
                val item = uiState.items.find { it.id == state.itemId }
                    ?: run { editState = IngredientEditState.None; return@Box }
                StorageLocationBottomSheet(
                    selectedLocation = item.location,
                    onLocationSelected = { location ->
                        onIntent(IngredientIntent.UpdateItem(item.copy(location = location)))
                    },
                    onDismiss = { editState = IngredientEditState.None },
                )
            }
            is IngredientEditState.EditingDate -> {
                val item = uiState.items.find { it.id == state.itemId }
                    ?: run { editState = IngredientEditState.None; return@Box }
                NeveraDatePickerDialog(
                    selectedDate = item.expiryDate,
                    onDateSelected = { date ->
                        onIntent(IngredientIntent.UpdateItem(item.copy(expiryDate = date)))
                    },
                    onDismiss = { editState = IngredientEditState.None },
                )
            }
            IngredientEditState.None -> Unit
        }
    }
}

@Composable
private fun IngredientListHeader(imageUri: String, onImageClick: () -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.ingredient_list_title),
            style = NeveraTheme.typography.headlineSmall,
            color = NeveraTheme.colors.textSecondary,
        )
        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap4))
        Text(
            text = stringResource(R.string.ingredient_list_subtitle),
            style = NeveraTheme.typography.bodySmall,
            color = NeveraTheme.colors.textQuaternary,
        )
        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap16))
        Box(
            modifier = Modifier
                .size(ScannedImageSize)
                .clip(RoundedCornerShape(NeveraTheme.radius.medium))
                .border(
                    width = ScannedImageBorderWidth,
                    color = NeveraTheme.colors.borderStrong,
                    shape = RoundedCornerShape(NeveraTheme.radius.medium),
                )
                .background(NeveraTheme.colors.backgroundSecondary)
                .clickable(
                    enabled = imageUri.isNotEmpty(),
                    onClick = onImageClick,
                ),
        ) {
            if (imageUri.isNotEmpty()) {
                AsyncImage(
                    model = imageUri.toUri(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap20))
        val horizontalPadding = NeveraTheme.spacing.padding16
        Box(
            modifier = Modifier
                .height(SectionDividerHeight)
                .layout { measurable, constraints ->
                    val paddingPx = horizontalPadding.roundToPx()
                    val expandedWidth = constraints.maxWidth + paddingPx * 2
                    val placeable = measurable.measure(
                        constraints.copy(maxWidth = expandedWidth, minWidth = expandedWidth),
                    )
                    layout(constraints.maxWidth, placeable.height) {
                        placeable.placeRelative(-paddingPx, 0)
                    }
                }
                .background(NeveraTheme.colors.dividerNormal),
        )
        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap4))
    }
}

@Composable
private fun AddItemButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        NeveraWeakIconButton(
            painter = NeveraIcons.Plus,
            contentDescription = stringResource(R.string.ingredient_add_item_button),
            onClick = onClick,
            color = NeveraButtonColor.Secondary,
            size = NeveraButtonSize.Medium,
            shape = CircleShape,
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
private fun IngredientContentPreview() {
    NeveraTheme {
        IngredientContent(
            uiState = IngredientUiState(
                imageUri = "content://preview/scanned_image",
                items = listOf(
                    IngredientUiModel(
                        name = "아침에주스 ABC 주스, 18개입",
                        category = FoodCategory.Drink,
                        location = StorageLocation.Fridge,
                        quantity = 2,
                        cost = 1000,
                        expiryDate = LocalDate.of(2026, 12, 17),
                        isSelected = true,
                    ),
                    IngredientUiModel(
                        name = "롯데 핸디카페 초콜릿",
                        category = null,
                        location = null,
                        quantity = 1,
                        cost = 4800,
                        isSelected = false,
                    ),
                ),
            ),
            listState = rememberLazyListState(),
            onIntent = {},
        )
    }
}
