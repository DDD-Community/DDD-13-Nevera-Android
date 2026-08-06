package com.anddd.nevera.feature.ingredient.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.feature.ingredient.R
import com.anddd.nevera.feature.ingredient.main.component.IngredientContent
import com.anddd.nevera.feature.ingredient.main.model.IngredientSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

/**
 * 식재료 등록 메인 화면
 *
 * 진입 즉시 OCR API를 호출하며, 단계별 렌더링은 [IngredientContent]에 위임합니다.
 * Screen은 ViewModel 구독과 SideEffect(네비게이션·토스트) 처리만 담당합니다.
 *
 * @param onNavigateBack            X 버튼 확인 후 이전 화면으로 이탈
 * @param onNavigateToError         OCR API 실패 시 [com.anddd.nevera.feature.ingredient.ocrerror.OcrErrorScreen]으로 이동
 * @param onNavigateToSuccess       등록 완료 시 RegisterSuccessScreen으로 이동 (총 금액 전달)
 * @param onNavigateToPhotoDetail   영수증 썸네일 탭 시 사진 상세 화면으로 이동 (imageUri 전달)
 * @param viewModel                 HiltViewModel
 */
@Composable
fun IngredientScreen(
    imageUri: String,

    onNavigateBack: () -> Unit,
    onNavigateToError: () -> Unit,
    onNavigateToSuccess: (totalCost: Int) -> Unit,
    onNavigateToPhotoDetail: (imageUri: String) -> Unit,
    viewModel: IngredientViewModel = hiltViewModel<IngredientViewModel, IngredientViewModel.Factory>(
        key = imageUri,
    ) { factory -> factory.create(imageUri) },
) {
    val uiState = viewModel.collectAsState().value
    val context = LocalContext.current

    viewModel.collectSideEffect { effect ->
        when (effect) {
            IngredientSideEffect.NavigateToOcrError -> onNavigateToError()
            is IngredientSideEffect.NavigateToSuccess -> onNavigateToSuccess(effect.totalCost)
            IngredientSideEffect.NavigateBack -> onNavigateBack()
            IngredientSideEffect.ShowRegisterFailedToast -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.ingredient_register_failed),
                    Toast.LENGTH_SHORT,
                ).show()
            }
            is IngredientSideEffect.NavigateToPhotoDetail ->
                onNavigateToPhotoDetail(effect.imageUri)
        }
    }

    IngredientContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
    )
}
