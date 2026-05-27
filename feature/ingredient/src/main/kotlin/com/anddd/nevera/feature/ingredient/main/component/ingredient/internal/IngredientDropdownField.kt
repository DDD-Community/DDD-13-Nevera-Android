package com.anddd.nevera.feature.ingredient.main.component.ingredient.internal

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R

/**
 * 드롭다운 선택 필드 행 (카테고리 / 보관 방법 공통)
 *
 * 레이블 + 선택 영역(배경 surfaceSecondary) + ChevronSmallRight 아이콘으로 구성됩니다.
 * [value]가 null이면 "선택" placeholder를 dim 색상으로 표시합니다.
 * 탭 시 [onClick]을 호출하며, 바텀시트 표시 여부는 호출 측에서 관리합니다.
 */
@Composable
internal fun IngredientDropdownField(
    @StringRes labelResId: Int,
    value: String?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IngredientFieldLabel(labelResId)
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap8))
        Row(
            modifier = Modifier.weight(1f)
                .clip(RoundedCornerShape(NeveraTheme.radius.small))
                .background(NeveraTheme.colors.surfaceSecondary)
                .clickable(onClick = onClick)
                .padding(NeveraTheme.spacing.padding12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value ?: stringResource(R.string.ingredient_item_placeholder_select),
                modifier = Modifier.weight(1f),
                color = if (value != null) NeveraTheme.colors.textSecondary
                else NeveraTheme.colors.textCaption,
                style = NeveraTheme.typography.bodyLarge,
            )
            Icon(
                painter = NeveraIcons.ChevronSmallRight,
                contentDescription = null,
                modifier = Modifier.size(NeveraTheme.iconSize.small),
                tint = NeveraTheme.colors.iconCaption,
            )
        }
    }
}

@Preview(
    name = "IngredientDropdownField - 선택 없음",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun IngredientDropdownFieldEmptyPreview() {
    NeveraTheme {
        IngredientDropdownField(
            labelResId = R.string.ingredient_item_label_category,
            value = null,
            onClick = {},
        )
    }
}

@Preview(
    name = "IngredientDropdownField - 선택됨",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun IngredientDropdownFieldSelectedPreview() {
    NeveraTheme {
        IngredientDropdownField(
            labelResId = R.string.ingredient_item_label_category,
            value = "음료",
            onClick = {},
        )
    }
}
