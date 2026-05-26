package com.anddd.nevera.domain.model.ingredient

import com.anddd.nevera.domain.model.common.CommonError

/**
 * OCR 식재료 추출 API 에러 도메인 모델
 *
 * | 에러 코드 | HTTP | 설명 |
 * |-----------|------|------|
 * | 6004 | 400 | 이미지 파일이 비어있음 |
 * | 6001 | 500 | OCR 처리 중 오류 발생 |
 * | 6003 | 500 | Google Vision API 오류 발생 |
 * | 6005 | 400 | 이미지 용량 초과 |
 * | 6002 | 400 | 유효하지 않은 이미지 포맷 |
 * | 5002 | 500 | LLM 텍스트 생성 오류 |
 * | 5001 | 500 | LLM 파싱 오류 |
 */
sealed interface OcrExtractError {
    /** 6004 — 이미지 파일이 비어있는 경우 */
    data object EmptyImageFile : OcrExtractError

    /** 6001 — OCR 처리 중 오류 발생 */
    data object OcrProcessFailed : OcrExtractError

    /** 6003 — Google Vision API 오류 발생 */
    data object GoogleVisionApiFailed : OcrExtractError

    /** 6005 — 이미지 용량 초과 */
    data object FileSizeExceeded : OcrExtractError

    /** 6002 — 유효하지 않은 이미지 포맷 */
    data object InvalidImageFormat : OcrExtractError

    /** 5002 — LLM 텍스트 생성 오류 */
    data object LlmGenerateError : OcrExtractError

    /** 5001 — LLM 파싱 오류 */
    data object LlmParseError : OcrExtractError

    /** 인프라/공통 에러 래핑 */
    data class Common(val error: CommonError) : OcrExtractError
}
