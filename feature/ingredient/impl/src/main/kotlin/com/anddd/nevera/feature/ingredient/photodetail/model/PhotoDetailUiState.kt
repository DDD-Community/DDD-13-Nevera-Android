package com.anddd.nevera.feature.ingredient.photodetail.model

import com.anddd.nevera.core.mvi.NeveraState

/**
 * 사진 상세 화면 상태
 *
 * ViewModel 없이 navigation 인자만으로 구성되는 화면이지만,
 * Screen/Content 계층 규칙의 일관성을 위해 UiState로 감싸 Content에 전달한다.
 *
 * @param imageUri 표시할 이미지 URI. null 이면 검정 배경만 표시한다.
 */
data class PhotoDetailUiState(
    val imageUri: String? = null,
) : NeveraState
