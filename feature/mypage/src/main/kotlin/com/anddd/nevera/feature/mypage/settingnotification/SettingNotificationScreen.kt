package com.anddd.nevera.feature.mypage.settingnotification

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonColor
import com.anddd.nevera.core.designsystem.component.dialog.NeveraConfirmDialog
import com.anddd.nevera.core.designsystem.component.timepicker.NeveraTimePickerDialog
import com.anddd.nevera.feature.mypage.settingnotification.component.SettingNotificationContent
import com.anddd.nevera.feature.mypage.settingnotification.model.SettingNotificationIntent
import com.anddd.nevera.feature.mypage.settingnotification.model.SettingNotificationSideEffect
import com.anddd.nevera.infra.permission.AppPermission
import com.anddd.nevera.infra.permission.DefaultPermissionChecker
import com.anddd.nevera.infra.permission.PermissionRequester
import com.anddd.nevera.feature.mypage.R as MyPageR
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SettingNotificationScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingNotificationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value
    var showTimePicker by remember { mutableStateOf(false) }
    var showPermissionRequester by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val isPermissionGranted =
            DefaultPermissionChecker.isGranted(context, AppPermission.Notification)
        viewModel.handleIntent(SettingNotificationIntent.LoadSettings(isPermissionGranted))
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SettingNotificationSideEffect.NavigateBack -> onNavigateBack()

            SettingNotificationSideEffect.ShowTimePickerDialog -> {
                showTimePicker = true
            }

            SettingNotificationSideEffect.RequestNotificationPermission -> {
                showPermissionRequester = true
            }

            SettingNotificationSideEffect.ShowLoadAlarmTimeError -> {
                Toast.makeText(
                    context,
                    context.getString(MyPageR.string.setting_notification_load_alarm_time_error),
                    Toast.LENGTH_SHORT,
                ).show()
            }

            SettingNotificationSideEffect.ShowUpdateAlarmTimeError -> {
                Toast.makeText(
                    context,
                    context.getString(MyPageR.string.setting_notification_update_alarm_time_error),
                    Toast.LENGTH_SHORT,
                ).show()
            }

            SettingNotificationSideEffect.ShowUpdateNotificationEnabledError -> {
                Toast.makeText(
                    context,
                    context.getString(MyPageR.string.setting_notification_update_notification_enabled_error),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    SettingNotificationContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
    )

    if (showTimePicker) {
        NeveraTimePickerDialog(
            initialHour = uiState.alarmHour,
            initialMinute = uiState.alarmMinute,
            onTimeSelected = { hour, minute ->
                showTimePicker = false
                viewModel.handleIntent(SettingNotificationIntent.AlarmTimeSelected(hour, minute))
            },
            onDismiss = { showTimePicker = false },
        )
    }

    if (showPermissionRequester) {
        PermissionRequester(
            permission = AppPermission.Notification,
            onGranted = {
                showPermissionRequester = false
                viewModel.handleIntent(
                    SettingNotificationIntent.ExpiryAlarmToggled(
                        enabled = true,
                        isPermissionGranted = true,
                    )
                )
            },
            onDenied = {
                showPermissionRequester = false
            },
        ) { onConfirm, onDismiss ->
            NeveraConfirmDialog(
                title = stringResource(MyPageR.string.setting_notification_permission_dialog_title),
                subtitle = stringResource(MyPageR.string.setting_notification_permission_dialog_subtitle),
                positive = stringResource(MyPageR.string.setting_notification_permission_dialog_positive),
                negative = stringResource(MyPageR.string.setting_notification_permission_dialog_negative),
                onNegative = { onDismiss() },
                onPositive = { onConfirm() },
                negativeButtonColor = NeveraButtonColor.Secondary,
                positiveButtonColor = NeveraButtonColor.Primary,
            )
        }
    }
}
