package com.anddd.nevera.feature.ingredient.main.component.ingredient.internal

import androidx.compose.ui.unit.dp

internal object IngredientItemCardDimension {
    /** [IngredientHeaderRow] — 체크박스 + 식재료명 + 편집 아이콘 행 최소 높이 */
    val HeaderHeight = 67.dp

    /** [IngredientHeaderRow] — 식재료명 텍스트 + 편집 아이콘 Row 최소 높이 (하단 border 기준선) */
    val NameRowHeight = 35.dp

    /** [IngredientQuantityField] — 수량 행 최소 높이 */
    val QuantityRowHeight = 48.dp

    /** [IngredientHeaderRow] — 식재료명 하단 border 선 굵기 */
    val BorderStrokeWidth = 1.dp

    /** [IngredientFieldLabel] — 모든 필드 레이블(수량/금액/카테고리/보관방법/유통기한) 고정 너비 */
    val FieldLabelWidth = 96.dp
}
