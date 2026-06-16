package com.anddd.nevera.domain.model.ingredient

import com.anddd.nevera.domain.model.common.CommonError

/**
 * 식재료 수정 API 에러 도메인 모델
 *
 * | 에러 코드 | HTTP | 설명 |
 * |-----------|------|------|
 * | 4001 | 404 | 존재하지 않는 재고 |
 * | 4002 | 403 | 해당 재고에 대한 권한 없음 |
 */
sealed interface EditIngredientError {
    /** 4001 — 존재하지 않는 재고 */
    data object InventoryNotFound : EditIngredientError

    /** 4002 — 해당 재고에 대한 권한 없음 */
    data object InventoryForbidden : EditIngredientError

    /** 인프라/공통 에러 래핑 */
    data class Common(val error: CommonError) : EditIngredientError
}
