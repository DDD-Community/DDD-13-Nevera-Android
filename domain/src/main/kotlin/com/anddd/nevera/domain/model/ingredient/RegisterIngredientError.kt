package com.anddd.nevera.domain.model.ingredient

import com.anddd.nevera.domain.model.common.CommonError

/**
 * 식재료 등록 API 에러 도메인 모델
 *
 * | 에러 코드 | HTTP | 설명 |
 * |-----------|------|------|
 * | 2041 | 404 | 존재하지 않는 사용자 |
 * | 3003 | 400 | 한 번에 100개 초과 등록 시도 |
 */
sealed interface RegisterIngredientError {
    /** 2041 — 존재하지 않는 사용자 */
    data object MemberNotFound : RegisterIngredientError

    /** 3003 — 한 번에 100개 초과 등록 시도 */
    data object MaxItemsExceeded : RegisterIngredientError

    /** 인프라/공통 에러 래핑 */
    data class Common(val error: CommonError) : RegisterIngredientError
}
