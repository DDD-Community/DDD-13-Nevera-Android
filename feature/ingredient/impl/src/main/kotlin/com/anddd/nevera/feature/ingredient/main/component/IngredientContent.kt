package com.anddd.nevera.feature.ingredient.main.component

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBar
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarAction
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarNavigation
import com.anddd.nevera.core.designsystem.component.dialog.NeveraConfirmDialog
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.ingredient.R
import com.anddd.nevera.feature.ingredient.main.component.ocrscanning.OcrScanningDialog
import com.anddd.nevera.feature.ingredient.main.model.IngredientIntent
import com.anddd.nevera.feature.ingredient.main.model.IngredientPhase
import com.anddd.nevera.feature.ingredient.main.model.IngredientUiModel
import com.anddd.nevera.feature.ingredient.main.model.IngredientUiState
import com.anddd.nevera.domain.model.ingredient.FoodCategory
import com.anddd.nevera.domain.model.ingredient.StorageLocation
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

/**
 * 식재료 등록 메인 콘텐츠
 *
 * [uiState.phase]에 따라 세 단계를 렌더링한다.
 * - [IngredientPhase.Scanning]: 흰색 배경 + [OcrScanningDialog] 오버레이
 * - [IngredientPhase.ScanSuccess]: [IngredientListSection] (스캔 결과 목록·편집)
 * - [IngredientPhase.Registering]: 목록 위에 [LoadingContent] 오버레이
 */
@Composable
internal fun IngredientContent(
    uiState: IngredientUiState,
    onIntent: (IngredientIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 취소 확인 다이얼로그 — 리셋되어도 비즈니스 영향이 없는 순수 UI 상호작용 상태
    var showCloseConfirm by remember { mutableStateOf(false) }

    if (showCloseConfirm) {
        NeveraConfirmDialog(
            title = stringResource(R.string.ingredient_screen_close_confirm_title),
            subtitle = stringResource(R.string.ingredient_screen_close_confirm_message),
            positive = stringResource(R.string.ingredient_screen_close_confirm_positive),
            negative = stringResource(R.string.ingredient_screen_close_confirm_negative),
            onPositive = {
                showCloseConfirm = false
                onIntent(IngredientIntent.CancelScan)
            },
            onNegative = { showCloseConfirm = false },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            IngredientAppBar(
                phase = uiState.phase,
                isAllSelected = uiState.isAllSelected,
                onClose = {
                    when (uiState.phase) {
                        IngredientPhase.Scanning -> {
                            onIntent(IngredientIntent.CancelScan)
                        }
                        IngredientPhase.ScanSuccess,
                        IngredientPhase.Registering -> showCloseConfirm = true
                    }
                },
                onToggleAll = {
                    onIntent(IngredientIntent.ToggleAllSelection(!uiState.isAllSelected))
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
                        onDismiss = { onIntent(IngredientIntent.CancelScan) },
                        dismissOnClickOutside = false,
                    )
                }
                IngredientPhase.ScanSuccess,
                IngredientPhase.Registering -> {
                    IngredientListSection(
                        uiState = uiState,
                        onIntent = onIntent,
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

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
private fun IngredientContentScanSuccessPreview() {
    NeveraTheme {
        IngredientContent(
            uiState = IngredientUiState(
                phase = IngredientPhase.ScanSuccess,
                imageUri = "content://preview/scanned_image",
                items = persistentListOf(
                    IngredientUiModel(
                        name = "아침에주스 ABC 주스, 18개입",
                        category = FoodCategory.Drink,
                        location = StorageLocation.Fridge,
                        quantity = 2,
                        cost = 1000,
                        expiryDate = LocalDate.of(2026, 12, 17),
                        isSelected = true,
                    ),
                ),
            ),
            onIntent = {},
        )
    }
}

@Preview(name = "스캔 중", showBackground = true, widthDp = 360, locale = "ko")
@Composable
private fun IngredientContentScanningPreview() {
    NeveraTheme {
        IngredientContent(
            uiState = IngredientUiState(
                phase = IngredientPhase.Scanning,
                scanProgress = 0.6f,
            ),
            onIntent = {},
        )
    }
}
