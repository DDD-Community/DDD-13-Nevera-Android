package com.anddd.nevera.data.model.ingredient

import com.google.gson.annotations.SerializedName

/**
 * OCR API 응답 식재료 항목 DTO
 *
 * 서버 필드명은 [SerializedName]으로 명시합니다.
 * 도메인 모델로의 변환은 [com.anddd.nevera.data.mapper.IngredientMapper.toDomain]을 사용합니다.
 *
 * ※ `expiryDate`는 OCR API 응답에 포함되지 않습니다. 사용자가 UI에서 직접 입력합니다.
 * ※ `unit`은 현재 도메인/UI 모델에 반영되지 않으며 추후 확장 시 활용합니다.
 */
internal data class OcrIngredientDto(
    @SerializedName("name")     val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("location") val location: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unit")     val unit: String,   // "EA", "PACK" 등 — 현재 미사용
    @SerializedName("cost")     val cost: Int,
)
