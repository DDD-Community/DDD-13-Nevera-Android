package com.anddd.nevera.feature.ingredient.main.component.ingredient

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import com.anddd.nevera.core.ui.R as CoreUiR
import com.anddd.nevera.core.ui.component.field.CostFieldRow
import com.anddd.nevera.core.ui.component.field.DropdownFieldRow
import com.anddd.nevera.core.ui.component.field.ExpiryDateFieldRow
import com.anddd.nevera.core.ui.component.field.QuantityFieldRow
import com.anddd.nevera.feature.ingredient.main.component.ingredient.internal.IngredientHeaderRow
import com.anddd.nevera.core.ui.displayName
import com.anddd.nevera.feature.ingredient.main.model.IngredientUiModel
import java.time.LocalDate

/**
 * 식재료 항목 카드 (Stateless)
 *
 * OCR 분석 결과 또는 직접 추가한 식재료 항목을 표시하고 수정할 수 있는 카드 컴포넌트입니다.
 * 상태 관리는 호출 측에서 담당하며, 변경된 값은 각 콜백으로 개별 전달됩니다.
 *
 * 다이얼로그·바텀시트 표시 여부는 부모 컴포넌트에서 관리합니다.
 * 탭 이벤트는 `onXxxClick` 콜백으로 부모에게 알립니다.
 *
 * @param item               현재 식재료 모델
 * @param onSelectionChanged 체크박스 토글 콜백
 * @param onQuantityChanged  수량 변경 값 전달
 * @param onCostChanged      금액 변경 값 전달
 * @param onCategoryClick    카테고리 탭 → 부모가 바텀시트 표시
 * @param onLocationClick    보관방법 탭 → 부모가 바텀시트 표시
 * @param onDateClick        유통기한 탭 → 부모가 DatePickerDialog 표시
 * @param modifier           외부 Modifier
 */
@Composable
fun IngredientItemCard(
    item: IngredientUiModel,
    onSelectionChanged: (Boolean) -> Unit,
    onQuantityChanged: (Int) -> Unit,
    onCostChanged: (Int) -> Unit,
    onCategoryClick: () -> Unit,
    onLocationClick: () -> Unit,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = NeveraTheme.colors.surfacePrimary,
        shape = RoundedCornerShape(NeveraTheme.radius.large),
        border = BorderStroke(1.dp, NeveraTheme.colors.borderNormal),
    ) {
        Column(
            modifier = Modifier.padding(bottom = NeveraTheme.spacing.gap16),
        ) {
            IngredientHeaderRow(
                name = item.name,
                isSelected = item.isSelected,
                onSelectionChanged = onSelectionChanged,
            )

            QuantityFieldRow(
                quantity = item.quantity,
                onQuantityChanged = onQuantityChanged,
            )

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            CostFieldRow(
                cost = item.cost,
                onCostChanged = onCostChanged,
            )

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            DropdownFieldRow(
                label = stringResource(CoreUiR.string.field_label_category),
                value = item.category?.displayName(),
                onClick = onCategoryClick,
            )

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            DropdownFieldRow(
                label = stringResource(CoreUiR.string.field_label_storage_location),
                value = item.location?.displayName(),
                onClick = onLocationClick,
            )

            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap8))

            ExpiryDateFieldRow(
                expiryDate = item.expiryDate,
                onClick = onDateClick,
            )
        }
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
                    category = FoodCategory.Drink,
                    location = StorageLocation.Fridge,
                    quantity = 2,
                    cost = 1000,
                    expiryDate = LocalDate.of(2026, 12, 17),
                    isSelected = true,
                ),
                onSelectionChanged = {},
                onQuantityChanged = {},
                onCostChanged = {},
                onCategoryClick = {},
                onLocationClick = {},
                onDateClick = {},
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
                onSelectionChanged = {},
                onQuantityChanged = {},
                onCostChanged = {},
                onCategoryClick = {},
                onLocationClick = {},
                onDateClick = {},
            )
        }
    }
}
