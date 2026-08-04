package com.anddd.nevera.feature.ingredient.main.model

import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import java.time.LocalDate
import java.util.UUID

/**
 * 식재료 등록·수정 화면 UI 모델
 *
 * OCR API 응답([com.anddd.nevera.feature.ingredient.data.remote.dto.OcrIngredientDto])을
 * [com.anddd.nevera.feature.ingredient.data.remote.mapper.OcrMapper]를 통해 변환하거나,
 * 사용자가 직접 추가하는 경우 [empty]로 생성됩니다.
 *
 * @param id          리스트 key용 고유 ID (서버 전송 불필요, 클라이언트 식별용)
 * @param name        식재료명
 * @param category    카테고리 (null = 미선택, UI에서 "선택" placeholder 표시)
 * @param location    보관 방법 (null = 미선택, UI에서 "선택" placeholder 표시)
 * @param quantity    수량 (최소 1)
 * @param expiryDate  유통기한 (null = 미설정)
 * @param cost        금액 (원)
 * @param isSelected  등록 대상 여부 (체크박스 선택 상태)
 */
data class IngredientUiModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: FoodCategory?,
    val location: StorageLocation?,
    val quantity: Int,
    val expiryDate: LocalDate? = null,
    val cost: Int,
    val isSelected: Boolean = true,
) {
    companion object {
        /** "직접 추가" 버튼으로 생성하는 빈 모델 */
        fun empty(): IngredientUiModel = IngredientUiModel(
            name = "",
            category = null,
            location = null,
            quantity = 1,
            expiryDate = LocalDate.now().plusDays(7),
            cost = 0,
            isSelected = false
        )
    }
}
