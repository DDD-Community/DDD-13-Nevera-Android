package com.anddd.nevera.feature.ingredient.main.component.ingredient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.datepicker.NeveraDatePickerDialog
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.designsystem.ui.theme.shadow.NeveraShadow
import com.anddd.nevera.core.designsystem.ui.theme.shadow.neveraShadow
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import com.anddd.nevera.feature.ingredient.main.component.CategoryBottomSheet
import com.anddd.nevera.feature.ingredient.main.component.StorageLocationBottomSheet
import com.anddd.nevera.feature.ingredient.main.component.ingredient.internal.IngredientCostField
import com.anddd.nevera.feature.ingredient.main.component.ingredient.internal.IngredientDropdownField
import com.anddd.nevera.feature.ingredient.main.component.ingredient.internal.IngredientExpiryDateRow
import com.anddd.nevera.feature.ingredient.main.component.ingredient.internal.IngredientHeaderRow
import com.anddd.nevera.feature.ingredient.main.component.ingredient.internal.IngredientNameEditDialog
import com.anddd.nevera.feature.ingredient.main.component.ingredient.internal.IngredientQuantityField
import com.anddd.nevera.feature.ingredient.main.displayName
import com.anddd.nevera.feature.ingredient.main.model.IngredientUiModel
import java.time.LocalDate

/**
 * 식재료 항목 카드 (Stateless)
 *
 * OCR 분석 결과 또는 직접 추가한 식재료 항목을 표시하고 수정할 수 있는 카드 컴포넌트입니다.
 * 상태 관리는 호출 측에서 담당하며, 변경 사항은 [onItemChanged] 콜백으로 전달됩니다.
 *
 * 이름 편집 UI는 디자이너와 추가 논의 후 개선 예정입니다.
 * 현재는 임시로 AlertDialog를 통해 이름을 수정합니다.
 *
 * @param item               현재 식재료 모델
 * @param onItemChanged      필드 변경 시 업데이트된 모델 전달
 * @param onSelectionChanged 체크박스 토글 콜백
 * @param modifier           외부 Modifier
 */
@Composable
fun IngredientItemCard(
    item: IngredientUiModel,
    onItemChanged: (IngredientUiModel) -> Unit,
    onSelectionChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCategorySheet  by remember { mutableStateOf(false) }
    var showLocationSheet  by remember { mutableStateOf(false) }
    var showDatePicker     by remember { mutableStateOf(false) }
    var showNameEditDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .neveraShadow(
                layers = NeveraShadow.small,
                cornerRadius = NeveraTheme.radius.medium,
            ),
        color = NeveraTheme.colors.surfacePrimary,
        shape = RoundedCornerShape(NeveraTheme.radius.medium),
    ) {
        Column(
            modifier = Modifier.padding(bottom = NeveraTheme.spacing.gap16),
        ) {
            IngredientHeaderRow(
                name = item.name,
                isSelected = item.isSelected,
                onSelectionChanged = onSelectionChanged,
                onEditClick = { showNameEditDialog = true },
            )

            IngredientQuantityField(
                quantity = item.quantity,
                onQuantityChanged = { onItemChanged(item.copy(quantity = it)) },
            )

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            IngredientCostField(
                cost = item.cost,
                onCostChanged = { onItemChanged(item.copy(cost = it)) },
            )

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            IngredientDropdownField(
                labelResId = com.anddd.nevera.feature.ingredient.R.string.ingredient_item_label_category,
                value = item.category?.displayName(),
                onClick = { showCategorySheet = true },
            )

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            IngredientDropdownField(
                labelResId = com.anddd.nevera.feature.ingredient.R.string.ingredient_item_label_location,
                value = item.location?.displayName(),
                onClick = { showLocationSheet = true },
            )

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            IngredientExpiryDateRow(
                expiryDate = item.expiryDate,
                onClick = { showDatePicker = true },
            )
        }
    }

    if (showCategorySheet) {
        CategoryBottomSheet(
            selectedCategory = item.category,
            onCategorySelected = { selected ->
                onItemChanged(item.copy(category = selected))
                showCategorySheet = false
            },
            onDismiss = { showCategorySheet = false },
        )
    }

    if (showLocationSheet) {
        StorageLocationBottomSheet(
            selectedLocation = item.location,
            onLocationSelected = { selected ->
                onItemChanged(item.copy(location = selected))
                showLocationSheet = false
            },
            onDismiss = { showLocationSheet = false },
        )
    }

    if (showDatePicker) {
        NeveraDatePickerDialog(
            selectedDate = item.expiryDate,
            onDateSelected = { date ->
                onItemChanged(item.copy(expiryDate = date))
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }

    if (showNameEditDialog) {
        IngredientNameEditDialog(
            currentName = item.name,
            onConfirm = { newName ->
                if (newName.isNotBlank()) onItemChanged(item.copy(name = newName.trim()))
                showNameEditDialog = false
            },
            onDismiss = { showNameEditDialog = false },
        )
    }
}

@Preview(showBackground = true, locale = "ko")
@Composable
private fun IngredientItemCardSelectedPreview() {
    NeveraTheme {
        Column(
            modifier = Modifier.padding(NeveraTheme.spacing.padding16),
            verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap8),
        ) {
            IngredientItemCard(
                item = IngredientUiModel(
                    name = "아침에주스 ABC 주스, 18개입 과즙 음료",
                    category = FoodCategory.Beverage,
                    location = StorageLocation.Fridge,
                    quantity = 2,
                    cost = 1000,
                    expiryDate = LocalDate.of(2026, 12, 17),
                    isSelected = true,
                ),
                onItemChanged = {},
                onSelectionChanged = {},
            )
        }
    }
}

@Preview(showBackground = true, locale = "ko")
@Composable
private fun IngredientItemCardUnselectedPreview() {
    NeveraTheme {
        Column(
            modifier = Modifier.padding(NeveraTheme.spacing.padding16),
            verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap8),
        ) {
            IngredientItemCard(
                item = IngredientUiModel(
                    name = "롯데 핸디카페 초콜릿 다크",
                    category = null,
                    location = null,
                    quantity = 1,
                    cost = 4800,
                    expiryDate = null,
                    isSelected = false,
                ),
                onItemChanged = {},
                onSelectionChanged = {},
            )
        }
    }
}
