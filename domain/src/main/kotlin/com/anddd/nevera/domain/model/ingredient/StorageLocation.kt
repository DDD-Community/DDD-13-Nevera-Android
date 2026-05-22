package com.anddd.nevera.domain.model.ingredient

/**
 * 식재료 보관 방법 도메인 모델
 *
 * UI 표시 문자열 등 표현 관련 속성은 UI 레이어의 확장 함수로 분리됩니다.
 * @see com.anddd.nevera.feature.ingredient.main.displayName
 */
sealed interface StorageLocation {
    data object Fridge : StorageLocation
    data object Freezer : StorageLocation
    data object Pantry : StorageLocation

    companion object {
        /** 화면에 표시할 모든 보관 방법 목록 (선언 순서 유지) */
        val entries: List<StorageLocation> = listOf(Fridge, Freezer, Pantry)
    }
}
