package com.anddd.nevera.feature.ingredient.registersuccess

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.anddd.nevera.feature.ingredient.registersuccess.component.RegisterSuccessContent
import com.anddd.nevera.feature.ingredient.registersuccess.model.RegisterSuccessUiState

/**
 * 식재료 등록 완료 화면
 *
 * ViewModel 없이 navigation 인자([totalSavedAmount])만으로 구성되는 화면.
 * Screen은 뒤로가기 처리만 담당하고 렌더링은 [RegisterSuccessContent]에 위임한다.
 *
 * @param totalSavedAmount 등록된 식재료 금액 합계 (절약 가능 금액으로 표시)
 * @param onViewFridge     "나의 냉장고 보기" 탭 콜백
 * @param onClose          X 버튼 탭 콜백
 */
@Composable
fun RegisterSuccessScreen(
    totalSavedAmount: Int,
    onViewFridge: () -> Unit,
    onClose: () -> Unit,
) {
    BackHandler(onBack = onClose)

    RegisterSuccessContent(
        uiState = RegisterSuccessUiState(totalSavedAmount = totalSavedAmount),
        onViewFridgeClick = onViewFridge,
        onCloseClick = onClose,
    )
}
