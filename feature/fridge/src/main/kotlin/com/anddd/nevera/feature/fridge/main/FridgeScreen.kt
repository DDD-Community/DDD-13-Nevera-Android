package com.anddd.nevera.feature.fridge.main

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.anddd.nevera.core.designsystem.component.bottomsheet.NeveraActionBottomSheet
import com.anddd.nevera.core.designsystem.component.dialog.NeveraConfirmDialog
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.ReceiptCaptureModeBottomSheet
import com.anddd.nevera.feature.fridge.R
import com.anddd.nevera.feature.fridge.main.component.FridgeContent
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
    viewModel: FridgeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value
    var showCaptureModeBottomSheet by remember { mutableStateOf(false) }
    var permissionHandled by remember { mutableStateOf(false) }
    val captureModeSheetState = rememberModalBottomSheetState()
    val notificationPermissionSheetState = rememberModalBottomSheetState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is FridgeSideEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()

            FridgeSideEffect.ShowCaptureModeBottomSheet -> showCaptureModeBottomSheet = true

            FridgeSideEffect.NavigateToNotification -> onNavigateToNotification()
        }
    }

    FridgeContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
    )

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
                content = { onConfirm, onDismiss ->
                    NeveraActionBottomSheet(
                        sheetState = notificationPermissionSheetState,
                        title = stringResource(R.string.notification_permission_request_title),
                        confirmLabel = stringResource(R.string.notification_permission_request_confirm),
                        onConfirm = onConfirm,
                        onDismissRequest = onDismiss,
                    ) {
                        Text(
                            text = stringResource(R.string.notification_permission_request_description),
                            style = NeveraTheme.typography.bodyMedium,
                            color = NeveraTheme.colors.textSecondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = NeveraTheme.spacing.padding20),
                        )
                    }
                },
                rationaleContent = { onConfirm, onDismiss ->
                    NeveraConfirmDialog(
                        title = stringResource(R.string.notification_permission_rationale_title),
                        subtitle = stringResource(R.string.notification_permission_rationale_message),
                        negative = stringResource(R.string.notification_permission_rationale_dismiss),
                        positive = stringResource(R.string.notification_permission_rationale_confirm),
                        onNegative = onDismiss,
                        onPositive = onConfirm,
                    )
                },
            )
        }
    }
}
