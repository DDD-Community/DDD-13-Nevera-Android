package com.anddd.nevera.feature.ingredient.main.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.datepicker.NeveraDatePickerDialog
import com.anddd.nevera.core.designsystem.component.stepper.NeveraQuantityStepper
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextField
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldSuffix
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.designsystem.ui.theme.shadow.NeveraShadow
import com.anddd.nevera.core.designsystem.ui.theme.shadow.neveraShadow
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import com.anddd.nevera.feature.ingredient.R
import com.anddd.nevera.feature.ingredient.main.displayName
import com.anddd.nevera.feature.ingredient.main.model.IngredientUiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 식재료 항목 카드 (Stateless)
 *
 * OCR 분석 결과 또는 직접 추가한 식재료 항목을 표시하고 수정할 수 있는 카드 컴포넌트입니다.
 * 상태 관리는 호출 측에서 담당하며, 변경 사항은 [onItemChanged] 콜백으로 전달됩니다.
 *
 * @param item              현재 식재료 모델
 * @param onItemChanged     필드 변경 시 업데이트된 모델 전달
 * @param onSelectionChanged 체크박스 토글 콜백
 * @param modifier          외부 Modifier
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
            modifier = Modifier.padding(NeveraTheme.spacing.padding16),
        ) {
            // 헤더: 체크박스 + 식재료명 + 편집 아이콘
            HeaderRow(
                name = item.name,
                isSelected = item.isSelected,
                onSelectionChanged = onSelectionChanged,
                onEditClick = { showNameEditDialog = true },
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = NeveraTheme.spacing.padding8),
                color = NeveraTheme.colors.dividerNormal,
            )

            // 수량
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FieldLabel(R.string.ingredient_item_label_quantity)
                Spacer(modifier = Modifier.weight(1f))
                NeveraQuantityStepper(
                    quantity = item.quantity,
                    onDecrease = {
                        onItemChanged(item.copy(quantity = (item.quantity - 1).coerceAtLeast(1)))
                    },
                    onIncrease = {
                        onItemChanged(item.copy(quantity = item.quantity + 1))
                    },
                )
            }

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            // 금액
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FieldLabel(R.string.ingredient_item_label_cost)
                Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap8))
                NeveraTextField(
                    value = item.cost.takeIf { it > 0 }?.toString() ?: "",
                    onValueChange = { input ->
                        val cost = input.filter { it.isDigit() }.toIntOrNull() ?: 0
                        onItemChanged(item.copy(cost = cost))
                    },
                    modifier = Modifier.weight(1f),
                    useIcon = false,
                    suffix = { NeveraTextFieldSuffix(stringResource(R.string.ingredient_item_cost_unit)) },
                    config = NeveraTextFieldConfig(
                        placeholder = "0",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    ),
                )
            }

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            // 카테고리
            DropdownField(
                labelResId = R.string.ingredient_item_label_category,
                value = item.category?.displayName(),
                onClick = { showCategorySheet = true },
            )

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            // 보관 방법
            DropdownField(
                labelResId = R.string.ingredient_item_label_location,
                value = item.location?.displayName(),
                onClick = { showLocationSheet = true },
            )

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            // 유통기한
            ExpiryDateRow(
                expiryDate = item.expiryDate,
                onClick = { showDatePicker = true },
            )
        }
    }

    // 바텀시트 / 다이얼로그
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
        NameEditDialog(
            currentName = item.name,
            onConfirm = { newName ->
                if (newName.isNotBlank()) onItemChanged(item.copy(name = newName.trim()))
                showNameEditDialog = false
            },
            onDismiss = { showNameEditDialog = false },
        )
    }
}

@Composable
private fun HeaderRow(
    name: String,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    onEditClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectionChanged,
            colors = CheckboxDefaults.colors(
                checkedColor = NeveraTheme.colors.primaryNormal,
                uncheckedColor = NeveraTheme.colors.iconDisabled,
            ),
        )
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap8))
        Text(
            text = name,
            style = NeveraTheme.typography.titleSmall,
            color = NeveraTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onEditClick) {
            Icon(
                painter = NeveraIcons.Edit,
                contentDescription = stringResource(R.string.ingredient_item_edit_icon_description),
                tint = NeveraTheme.colors.iconSecondary,
                modifier = Modifier.size(NeveraTheme.iconSize.medium),
            )
        }
    }
}

@Composable
private fun FieldLabel(@StringRes resId: Int) {
    Text(
        text = stringResource(resId),
        style = NeveraTheme.typography.bodyMedium,
        color = NeveraTheme.colors.textSecondary,
    )
}

@Composable
private fun DropdownField(
    @StringRes labelResId: Int,
    value: String?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FieldLabel(labelResId)
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap8))
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(NeveraTheme.radius.small))
                .background(NeveraTheme.colors.surfaceSecondary)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = NeveraTheme.spacing.padding12,
                    vertical = NeveraTheme.spacing.padding12,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value ?: stringResource(R.string.ingredient_item_placeholder_select),
                style = NeveraTheme.typography.bodyMedium,
                color = if (value != null) NeveraTheme.colors.textPrimary
                        else NeveraTheme.colors.textTertiary,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = NeveraIcons.ChevronSmallDown,
                contentDescription = null,
                tint = NeveraTheme.colors.iconSecondary,
                modifier = Modifier.size(NeveraTheme.iconSize.small),
            )
        }
    }
}

@Composable
private fun ExpiryDateRow(
    expiryDate: LocalDate?,
    onClick: () -> Unit,
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy.MM.dd") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FieldLabel(R.string.ingredient_item_label_expiry)
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap8))
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(NeveraTheme.radius.small))
                .background(NeveraTheme.colors.surfaceSecondary)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = NeveraTheme.spacing.padding12,
                    vertical = NeveraTheme.spacing.padding12,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = expiryDate?.format(dateFormatter)
                       ?: stringResource(R.string.ingredient_item_placeholder_date),
                style = NeveraTheme.typography.bodyMedium,
                color = if (expiryDate != null) NeveraTheme.colors.textPrimary
                        else NeveraTheme.colors.textTertiary,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = NeveraIcons.ChevronSmallRight,
                contentDescription = null,
                tint = NeveraTheme.colors.iconSecondary,
                modifier = Modifier.size(NeveraTheme.iconSize.small),
            )
        }
    }
}

@Composable
private fun NameEditDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var editingName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NeveraTheme.colors.surfacePrimary,
        shape = RoundedCornerShape(NeveraTheme.radius.medium),
        title = {
            Text(
                text = stringResource(R.string.ingredient_item_edit_name),
                style = NeveraTheme.typography.titleMedium,
                color = NeveraTheme.colors.textPrimary,
            )
        },
        text = {
            NeveraTextField(
                value = editingName,
                onValueChange = { editingName = it },
                useIcon = false,
                config = NeveraTextFieldConfig(
                    placeholder = stringResource(R.string.ingredient_item_edit_name_placeholder),
                    singleLine = true,
                ),
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(editingName) }) {
                Text(
                    text = stringResource(R.string.ingredient_item_edit_confirm),
                    color = NeveraTheme.colors.primaryNormal,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.ingredient_item_edit_dismiss),
                    color = NeveraTheme.colors.textSecondary,
                )
            }
        },
    )
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
