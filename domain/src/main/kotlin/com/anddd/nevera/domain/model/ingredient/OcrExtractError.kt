package com.anddd.nevera.domain.model.ingredient

import com.anddd.nevera.domain.model.common.CommonError

/**
 * OCR 식재료 추출 API 에러 도메인 모델
 *
 * | 에러 코드 | 설명 |
 * |-----------|------|
 * | 3004 | 이미지 파일이 비어있음 |
 * | 3001 | OCR 처리 중 오류 발생 |
 * | 3003 | Google Vision API 오류 발생 |
 */
sealed interface OcrExtractError {
    /** 3004 — 이미지 파일이 비어있는 경우 */
    data object EmptyImageFile : OcrExtractError

    /** 3001 — OCR 처리 중 오류 발생 */
    data object OcrProcessFailed : OcrExtractError

    /** 3003 — Google Vision API 오류 발생 */
    data object GoogleVisionApiFailed : OcrExtractError

    /** 인프라/공통 에러 래핑 */
    data class Common(val error: CommonError) : OcrExtractError
}
