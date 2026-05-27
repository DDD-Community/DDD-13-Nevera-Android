package com.anddd.nevera.feature.ingredient.main.component.ingredient.internal

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R

/**
 * 필드 레이블 공통 컴포넌트
 *
 * 수량·금액·카테고리·보관방법·유통기한 행의 좌측 레이블에 사용됩니다.
 * [IngredientItemCardDimension.FieldLabelWidth] 고정 너비로 모든 값 영역의 시작점을 정렬합니다.
 */
@Composable
internal fun IngredientFieldLabel(@StringRes resId: Int) {
    Text(
        text = stringResource(resId),
        modifier = Modifier.width(IngredientItemCardDimension.FieldLabelWidth),
        color = NeveraTheme.colors.textCaption,
        style = NeveraTheme.typography.titleXSmall,
    )
}

@Preview(
    name = "IngredientFieldLabel",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun IngredientFieldLabelPreview() {
    NeveraTheme {
        IngredientFieldLabel(resId = R.string.ingredient_item_label_quantity)
    }
}
