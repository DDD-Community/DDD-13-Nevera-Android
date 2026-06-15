package com.anddd.nevera.feature.fridge.main

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.designsystem.component.bottomsheet.NeveraActionBottomSheet
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonColor
import com.anddd.nevera.core.designsystem.component.dialog.NeveraConfirmDialog
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.NotificationPermissionDescriptionItem
import com.anddd.nevera.core.ui.component.ReceiptCaptureModeBottomSheet
import com.anddd.nevera.feature.fridge.R
import com.anddd.nevera.feature.fridge.main.component.FRIDGE_LIST_HEADER_ITEM_COUNT
import com.anddd.nevera.feature.fridge.main.component.FridgeContent
import com.anddd.nevera.feature.fridge.main.component.FridgeIngredientDisposeBottomSheet
import com.anddd.nevera.feature.fridge.main.component.FridgeIngredientRescueBottomSheet
import com.anddd.nevera.feature.fridge.main.model.FridgeIngredientUiModel
import com.anddd.nevera.feature.fridge.main.model.FridgeIntent
import com.anddd.nevera.feature.fridge.main.model.FridgeSideEffect
import com.anddd.nevera.infra.permission.AppPermission
import com.anddd.nevera.infra.permission.PermissionRequester
import com.anddd.nevera.infra.permission.PermissionState
import com.anddd.nevera.infra.permission.rememberPermissionState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToEditIngredient: (Long) -> Unit,
    viewModel: FridgeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value
    var showCaptureModeBottomSheet by remember { mutableStateOf(false) }
    var showRescueBottomSheet by remember { mutableStateOf(false) }
    var showRescueConfirmDialog by remember { mutableStateOf(false) }
    var rescueTargetItem by remember { mutableStateOf<FridgeIngredientUiModel?>(null) }
    var pendingRescueRatio by remember { mutableFloatStateOf(1.0f) }
    var showDisposeBottomSheet by remember { mutableStateOf(false) }
    var showDisposeConfirmDialog by remember { mutableStateOf(false) }
    var disposeTargetItem by remember { mutableStateOf<FridgeIngredientUiModel?>(null) }
    var pendingDisposeRatio by remember { mutableFloatStateOf(1.0f) }
    var permissionHandled by remember { mutableStateOf(false) }
    val captureModeSheetState = rememberModalBottomSheetState()
    val rescueSheetState = rememberModalBottomSheetState()
    val disposeSheetState = rememberModalBottomSheetState()
    val notificationPermissionSheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()
    // SideEffect 수신 시점의 uiState.ingredients는 아직 갱신 전일 수 있으므로,
    // pendingScrollIndex에 보관해두고 ingredients가 갱신된 뒤 스크롤을 수행한다.
    var pendingScrollIndex by remember { mutableStateOf<Int?>(null) }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is FridgeSideEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()

            FridgeSideEffect.ShowCaptureModeBottomSheet -> showCaptureModeBottomSheet = true

            FridgeSideEffect.NavigateToNotification -> onNavigateToNotification()

            is FridgeSideEffect.ShowRescueBottomSheet -> {
                rescueTargetItem = effect.item
                showRescueBottomSheet = true
            }

            is FridgeSideEffect.ShowDisposeBottomSheet -> {
                disposeTargetItem = effect.item
                showDisposeBottomSheet = true
            }

            is FridgeSideEffect.NavigateToEditIngredient -> onNavigateToEditIngredient(effect.ingredientId)

            is FridgeSideEffect.ScrollToIngredient -> pendingScrollIndex = effect.index
        }
    }

    LaunchedEffect(pendingScrollIndex, uiState.ingredients) {
        val index = pendingScrollIndex ?: return@LaunchedEffect
        if (index < uiState.ingredients.size) {
            listState.animateScrollToItem(FRIDGE_LIST_HEADER_ITEM_COUNT + index)
            pendingScrollIndex = null
        }
    }

    FridgeContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
        listState = listState,
    )

    if (showRescueBottomSheet && rescueTargetItem != null) {
        FridgeIngredientRescueBottomSheet(
            item = rescueTargetItem!!,
            sheetState = rescueSheetState,
            onConfirmClick = { ratio ->
                pendingRescueRatio = ratio
                showRescueConfirmDialog = true
            },
            onDismissRequest = {
                showRescueConfirmDialog = false
                showRescueBottomSheet = false
            },
        )

        if (showRescueConfirmDialog) {
            NeveraConfirmDialog(
                title = stringResource(R.string.fridge_rescue_confirm_dialog_title),
                positive = stringResource(R.string.fridge_rescue_sheet_confirm),
                negative = stringResource(R.string.fridge_rescue_confirm_dialog_close),
                onPositive = {
                    showRescueConfirmDialog = false
                    showRescueBottomSheet = false
                    viewModel.handleIntent(FridgeIntent.RescueConfirm(rescueTargetItem!!, pendingRescueRatio))
                },
                onNegative = { showRescueConfirmDialog = false },
                negativeButtonColor = NeveraButtonColor.Secondary,
            )
        }
    }

    if (showDisposeBottomSheet && disposeTargetItem != null) {
        FridgeIngredientDisposeBottomSheet(
            item = disposeTargetItem!!,
            sheetState = disposeSheetState,
            onConfirmClick = { ratio ->
                pendingDisposeRatio = ratio
                showDisposeConfirmDialog = true
            },
            onDismissRequest = {
                showDisposeConfirmDialog = false
                showDisposeBottomSheet = false
            },
        )

        if (showDisposeConfirmDialog) {
            NeveraConfirmDialog(
                title = stringResource(R.string.fridge_dispose_confirm_dialog_title),
                positive = stringResource(R.string.fridge_dispose_sheet_confirm),
                negative = stringResource(R.string.fridge_dispose_confirm_dialog_close),
                onPositive = {
                    showDisposeConfirmDialog = false
                    showDisposeBottomSheet = false
                    viewModel.handleIntent(FridgeIntent.DisposeConfirm(disposeTargetItem!!, pendingDisposeRatio))
                },
                onNegative = { showDisposeConfirmDialog = false },
                negativeButtonColor = NeveraButtonColor.Secondary,
            )
        }
    }

    if (showCaptureModeBottomSheet) {
        ReceiptCaptureModeBottomSheet(
            sheetState = captureModeSheetState,
            onReceiptScan = {
                showCaptureModeBottomSheet = false
                onNavigateToCamera()
            },
            onOnlineCapture = {
                showCaptureModeBottomSheet = false
                onNavigateToGallery()
            },
            onDismiss = { showCaptureModeBottomSheet = false },
        )
    }

    if (!permissionHandled) {
        val notificationPermissionState = rememberPermissionState(AppPermission.Notification)

        when (notificationPermissionState) {
            PermissionState.Granted -> LaunchedEffect(Unit) { permissionHandled = true }
            else -> PermissionRequester(
                permission = AppPermission.Notification,
                onGranted = { permissionHandled = true },
                onDenied = { permissionHandled = true },
                content = { onConfirm, _ ->
                    NeveraActionBottomSheet(
                        sheetState = notificationPermissionSheetState,
                        title = stringResource(R.string.fridge_notification_permission_request_title),
                        confirmLabel = stringResource(R.string.fridge_notification_permission_request_confirm),
                        onConfirm = onConfirm,
                        onDismissRequest = { permissionHandled = true },
                    ) {
                        NotificationPermissionDescriptionItem(
                            modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding20)
                        )
                    }
                },
            )
        }
    }
}
