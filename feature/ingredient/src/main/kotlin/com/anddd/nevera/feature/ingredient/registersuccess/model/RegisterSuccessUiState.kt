package com.anddd.nevera.feature.ingredient.registersuccess.model

import com.anddd.nevera.core.mvi.NeveraState

/**
 * 식재료 등록 완료 화면 상태
 *
 * ViewModel 없이 navigation 인자만으로 구성되는 화면이지만,
 * Screen/Content 계층 규칙의 일관성을 위해 UiState로 감싸 Content에 전달한다.
 *
 * @param totalSavedAmount 등록된 식재료 금액 합계 (절약 가능 금액으로 표시)
 */
data class RegisterSuccessUiState(
    val totalSavedAmount: Int = 0,
) : NeveraState
