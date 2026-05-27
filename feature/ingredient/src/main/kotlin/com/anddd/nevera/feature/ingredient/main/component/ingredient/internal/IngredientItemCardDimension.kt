package com.anddd.nevera.feature.ingredient.main.component.ingredient.internal

import androidx.compose.ui.unit.dp

internal object IngredientItemCardDimension {
    /** [IngredientHeaderRow] — 체크박스 + 식재료명 + 편집 아이콘 행 최소 높이 */
    val HeaderHeight = 67.dp

    /** [IngredientQuantityField] — 수량 행 최소 높이 */
    val QuantityRowHeight = 48.dp

    /** [IngredientExpiryDateRow] — 날짜 선택 영역(내부 Row) 최소 높이 */
    val ExpiryDateRowHeight = 48.dp

    /** [IngredientFieldLabel] — 모든 필드 레이블(수량/금액/카테고리/보관방법/유통기한) 고정 너비 */
    val FieldLabelWidth = 96.dp
}
