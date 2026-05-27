package com.anddd.nevera.feature.ingredient.main.component.ingredient.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 유통기한 선택 필드 행
 *
 * 레이블 + 날짜 영역(배경 surfaceSecondary) + ChevronRight 아이콘으로 구성됩니다.
 * [expiryDate]가 null이면 "날짜 선택" placeholder를 dim 색상으로 표시합니다.
 * 탭 시 [onClick]을 호출하며, [NeveraDatePickerDialog] 표시 여부는 호출 측에서 관리합니다.
 */
@Composable
internal fun IngredientExpiryDateRow(
    expiryDate: LocalDate?,
    onClick: () -> Unit,
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy.MM.dd") }

    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IngredientFieldLabel(R.string.ingredient_item_label_expiry)
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap8))
        Row(
            modifier = Modifier.weight(1f)
                .heightIn(IngredientItemCardDimension.ExpiryDateRowHeight)
                .clip(RoundedCornerShape(NeveraTheme.radius.small))
                .background(NeveraTheme.colors.surfaceSecondary)
                .clickable(onClick = onClick)
                .padding(NeveraTheme.spacing.padding12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = expiryDate?.format(dateFormatter) ?: stringResource(R.string.ingredient_item_placeholder_date),
                modifier = Modifier.weight(1f),
                color = if (expiryDate != null) NeveraTheme.colors.textSecondary
                else NeveraTheme.colors.textDisabled,
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
    name = "IngredientExpiryDateRow - 날짜 없음",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun IngredientExpiryDateRowEmptyPreview() {
    NeveraTheme {
        IngredientExpiryDateRow(
            expiryDate = null,
            onClick = {},
        )
    }
}

@Preview(
    name = "IngredientExpiryDateRow - 날짜 있음",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun IngredientExpiryDateRowPreview() {
    NeveraTheme {
        IngredientExpiryDateRow(
            expiryDate = LocalDate.of(2026, 12, 17),
            onClick = {},
        )
    }
}
