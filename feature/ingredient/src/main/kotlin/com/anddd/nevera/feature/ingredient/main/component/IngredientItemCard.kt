package com.anddd.nevera.feature.ingredient.main.component

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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

private object IngredientItemCardDimension {
    val HeaderHeight = 67.dp
    val NameRowHeight = 35.dp
    val QuantityRowHeight = 48.dp
    val BorderStrokeWidth = 1.dp
    val FieldLabelWidth = 96.dp
}

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
        modifier = modifier.fillMaxWidth()
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
            // 헤더: 체크박스 + 식재료명 + 편집 아이콘
            IngredientHeaderRow(
                name = item.name,
                isSelected = item.isSelected,
                onSelectionChanged = onSelectionChanged,
                onEditClick = { showNameEditDialog = true },
            )

            // 수량
            IngredientQuantityField(
                quantity = item.quantity,
                onQuantityChanged = { onItemChanged(item.copy(quantity = it)) },
            )

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            // 금액
            IngredientCostField(
                cost = item.cost,
                onCostChanged = { onItemChanged(item.copy(cost = it)) },
            )

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            // 카테고리
            IngredientDropdownField(
                labelResId = R.string.ingredient_item_label_category,
                value = item.category?.displayName(),
                onClick = { showCategorySheet = true },
            )

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            // 보관 방법
            IngredientDropdownField(
                labelResId = R.string.ingredient_item_label_location,
                value = item.location?.displayName(),
                onClick = { showLocationSheet = true },
            )

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            // 유통기한
            IngredientExpiryDateRow(
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

@Composable
private fun IngredientHeaderRow(
    name: String,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    onEditClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .heightIn(IngredientItemCardDimension.HeaderHeight)
            .padding(horizontal = NeveraTheme.spacing.gap16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(
                if (isSelected) R.drawable.ic_checkbox_check_active_24
                else R.drawable.ic_checkbox_check_disabled_24
            ),
            contentDescription = null,
            modifier = Modifier.size(NeveraTheme.iconSize.medium)
                .toggleable(
                    value = isSelected,
                    role = Role.Checkbox,
                    onValueChange = onSelectionChanged,
                ),
        )
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap12))
        val borderColor = NeveraTheme.colors.borderNormal
        Row(
            modifier = Modifier.weight(1f)
                .heightIn(IngredientItemCardDimension.NameRowHeight)
                .drawBehind {
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = IngredientItemCardDimension.BorderStrokeWidth.toPx(),
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                style = NeveraTheme.typography.titleLarge,
                color = NeveraTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(NeveraTheme.iconSize.xLarge)
            ) {
                Icon(
                    painter = NeveraIcons.Edit,
                    contentDescription = stringResource(R.string.ingredient_item_edit_icon_description),
                    modifier = Modifier.size(NeveraTheme.iconSize.medium),
                    tint = NeveraTheme.colors.iconCaption,
                )
            }
        }
    }
}

@Composable
private fun IngredientQuantityField(
    quantity: Int,
    onQuantityChanged: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(IngredientItemCardDimension.QuantityRowHeight)
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IngredientFieldLabel(R.string.ingredient_item_label_quantity)
        Spacer(modifier = Modifier.weight(1f))
        NeveraQuantityStepper(
            quantity = quantity,
            onDecrease = { onQuantityChanged((quantity - 1).coerceAtLeast(1)) },
            onIncrease = { onQuantityChanged(quantity + 1) },
        )
    }
}

@Composable
private fun IngredientCostField(
    cost: Int,
    onCostChanged: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IngredientFieldLabel(R.string.ingredient_item_label_cost)
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap8))
        NeveraTextField(
            value = cost.takeIf { it > 0 }?.toString() ?: "0",
            onValueChange = { input ->
                val newCost = input.filter { it.isDigit() }.toIntOrNull() ?: 0
                onCostChanged(newCost)
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
}

@Composable
private fun IngredientFieldLabel(@StringRes resId: Int) {
    Text(
        text = stringResource(resId),
        style = NeveraTheme.typography.titleXSmall,
        color = NeveraTheme.colors.textCaption,
        modifier = Modifier.width(IngredientItemCardDimension.FieldLabelWidth),
    )
}

@Composable
private fun IngredientDropdownField(
    @StringRes labelResId: Int,
    value: String?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IngredientFieldLabel(labelResId)
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
private fun IngredientExpiryDateRow(
    expiryDate: LocalDate?,
    onClick: () -> Unit,
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy.MM.dd") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IngredientFieldLabel(R.string.ingredient_item_label_expiry)
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

/**
 * 식재료 이름 편집 다이얼로그
 *
 * TODO: 디자이너와 추가 논의 후 인라인 편집 방식으로 개선 예정
 */
@Composable
private fun IngredientNameEditDialog(
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
