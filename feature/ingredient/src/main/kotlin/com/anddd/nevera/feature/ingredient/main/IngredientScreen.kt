package com.anddd.nevera.feature.ingredient.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBar
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarAction
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarNavigation
import com.anddd.nevera.core.designsystem.component.dialog.NeveraConfirmDialog
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.ingredient.R
import com.anddd.nevera.feature.ingredient.main.component.IngredientContent
import com.anddd.nevera.feature.ingredient.main.component.ocrscanning.OcrScanningDialog
import com.anddd.nevera.feature.ingredient.main.model.IngredientIntent
import com.anddd.nevera.feature.ingredient.main.model.IngredientPhase
import com.anddd.nevera.feature.ingredient.main.model.IngredientSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

/**
 * 식재료 등록 메인 화면
 *
 * 진입 즉시 OCR API를 호출하며, 스캔 중에는 [OcrScanningDialog]를 오버레이로 표시합니다.
 * 스캔 중 배경 화면은 흰색으로 처리됩니다.
 *
 * @param onNavigateBack            X 버튼 확인 후 이전 화면으로 이탈
 * @param onNavigateToError         OCR API 실패 시 [com.anddd.nevera.feature.ingredient.ocrerror.OcrErrorScreen]으로 이동
 * @param onNavigateToSuccess       등록 완료 시 RegisterSuccessScreen으로 이동 (총 금액 전달)
 * @param onNavigateToPhotoDetail   영수증 썸네일 탭 시 사진 상세 화면으로 이동 (imageUri 전달)
 * @param viewModel                 HiltViewModel
 */
@Composable
fun IngredientScreen(
    onNavigateBack: () -> Unit,
    onNavigateToError: () -> Unit,
    onNavigateToSuccess: (totalCost: Int) -> Unit,
    onNavigateToPhotoDetail: (imageUri: String) -> Unit,
    viewModel: IngredientViewModel = hiltViewModel(),
) {
    val uiState = viewModel.collectAsState().value
    val context = LocalContext.current
    var showCloseConfirm by remember { mutableStateOf(false) }

    // SideEffect 처리
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
        }
    }

    // 취소 확인 다이얼로그
    if (showCloseConfirm) {
        NeveraConfirmDialog(
            title = stringResource(R.string.ingredient_screen_close_confirm_title),
            subtitle = stringResource(R.string.ingredient_screen_close_confirm_message),
            positive = stringResource(R.string.ingredient_screen_close_confirm_positive),
            negative = stringResource(R.string.ingredient_screen_close_confirm_negative),
            onPositive = {
                showCloseConfirm = false
                viewModel.handleIntent(IngredientIntent.CancelScan)
            },
            onNegative = { showCloseConfirm = false },
        )
    }

    Scaffold(
        topBar = {
            IngredientAppBar(
                phase = uiState.phase,
                isAllSelected = uiState.isAllSelected,
                onClose = {
                    when (uiState.phase) {
                        IngredientPhase.Scanning -> {
                            viewModel.handleIntent(IngredientIntent.CancelScan)
                        }
                        IngredientPhase.ScanSuccess,
                        IngredientPhase.Registering -> showCloseConfirm = true
                    }
                },
                onToggleAll = {
                    viewModel.handleIntent(
                        IngredientIntent.ToggleAllSelection(!uiState.isAllSelected)
                    )
                },
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.phase) {
                IngredientPhase.Scanning -> {
                    // 스캔 중: 흰색 배경 + OcrScanningDialog 오버레이 (Dialog가 scrim 자동 처리)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    )
                    OcrScanningDialog(
                        videoResId = R.raw.illust_loading,
                        progress = uiState.scanProgress,
                        onDismiss = { viewModel.handleIntent(IngredientIntent.CancelScan) },
                        dismissOnClickOutside = false,
                    )
                }
                IngredientPhase.ScanSuccess,
                IngredientPhase.Registering -> {
                    IngredientContent(
                        uiState = uiState,
                        scannedImageUri = uiState.imageUri,
                        onIntent = viewModel::handleIntent,
                        onImageClick = { onNavigateToPhotoDetail(uiState.imageUri) },
                        modifier = Modifier.fillMaxSize(),
                    )
                    if (uiState.phase is IngredientPhase.Registering) {
                        LoadingContent()
                    }
                }
            }
        }
    }
}

// ─── Private Components ────────────────────────────────────────────────────────

/**
 * 식재료 등록 화면 AppBar
 *
 * @param phase         현재 화면 단계 (action 표시 여부 및 배경 표시 결정)
 * @param isAllSelected 전체 선택 여부 (action 라벨 결정)
 * @param onClose       X 버튼 탭 콜백
 * @param onToggleAll   전체 선택/해제 탭 콜백
 */
@Composable
private fun IngredientAppBar(
    phase: IngredientPhase,
    isAllSelected: Boolean,
    onClose: () -> Unit,
    onToggleAll: () -> Unit,
) {
    NeveraAppBar(
        navigation = NeveraAppBarNavigation.Close(onClick = onClose),
        action = if (phase is IngredientPhase.ScanSuccess) {
            NeveraAppBarAction.Text(
                label = if (isAllSelected) {
                    stringResource(R.string.ingredient_action_deselect_all)
                } else {
                    stringResource(R.string.ingredient_action_select_all)
                },
                onClick = onToggleAll,
                tone = NeveraAppBarAction.Text.Tone.Primary,
            )
        } else {
            NeveraAppBarAction.None
        },
        showBackground = phase !is IngredientPhase.Scanning,
    )
}