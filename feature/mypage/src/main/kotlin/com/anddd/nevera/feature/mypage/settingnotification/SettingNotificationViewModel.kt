package com.anddd.nevera.feature.mypage.settingnotification

import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.usecase.notification.GetNotificationTimeUseCase
import com.anddd.nevera.domain.usecase.notification.UpdateNotificationEnabledUseCase
import com.anddd.nevera.domain.usecase.notification.UpdateNotificationTimeUseCase
import com.anddd.nevera.feature.mypage.settingnotification.model.SettingNotificationIntent
import com.anddd.nevera.feature.mypage.settingnotification.model.SettingNotificationMutation
import com.anddd.nevera.feature.mypage.settingnotification.model.SettingNotificationSideEffect
import com.anddd.nevera.feature.mypage.settingnotification.model.SettingNotificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class SettingNotificationViewModel @Inject constructor(
    private val getNotificationTime: GetNotificationTimeUseCase,
    private val updateNotificationEnabled: UpdateNotificationEnabledUseCase,
    private val updateNotificationTime: UpdateNotificationTimeUseCase,
) : NeveraViewModel<SettingNotificationUiState, SettingNotificationSideEffect, SettingNotificationIntent, SettingNotificationMutation>(
    SettingNotificationUiState()
) {

    override fun handleIntent(intent: SettingNotificationIntent) {
        when (intent) {
            SettingNotificationIntent.NavigateBack -> intent {
                postSideEffect(SettingNotificationSideEffect.NavigateBack)
            }

            is SettingNotificationIntent.LoadSettings ->
                loadNotificationSettings(intent.isPermissionGranted)

            is SettingNotificationIntent.ExpiryAlarmToggled ->
                onExpiryAlarmToggled(intent.enabled, intent.isPermissionGranted)

            SettingNotificationIntent.AlarmTimeClicked -> intent {
                postSideEffect(SettingNotificationSideEffect.ShowTimePickerDialog)
            }

            is SettingNotificationIntent.AlarmTimeSelected ->
                onAlarmTimeSelected(intent.hour, intent.minute)
        }
    }

    private fun loadNotificationSettings(isPermissionGranted: Boolean) = intent {
        getNotificationTime()
            .onSuccess { setting ->
                if (setting.enabled && !isPermissionGranted) {
                    updateNotificationEnabled(false)
                }
                val effectiveEnabled = setting.enabled && isPermissionGranted
                applyMutation(SettingNotificationMutation.ExpiryAlarmUpdated(effectiveEnabled))
                applyMutation(SettingNotificationMutation.AlarmTimeUpdated(setting.hour, setting.minute))
            }.onFailure {
                postSideEffect(SettingNotificationSideEffect.ShowLoadAlarmTimeError)
            }
    }

    private fun onExpiryAlarmToggled(enabled: Boolean, isPermissionGranted: Boolean) = intent {
        when {
            enabled && !isPermissionGranted ->
                postSideEffect(SettingNotificationSideEffect.RequestNotificationPermission)
            else ->
                updateNotificationEnabled(enabled)
                    .onSuccess {
                        applyMutation(SettingNotificationMutation.ExpiryAlarmUpdated(it.enabled))
                    }.onFailure {
                        postSideEffect(SettingNotificationSideEffect.ShowUpdateNotificationEnabledError)
                    }
        }
    }

    private fun onAlarmTimeSelected(hour: Int, minute: Int) = intent {
        updateNotificationTime(hour, minute)
            .onSuccess {
                applyMutation(SettingNotificationMutation.AlarmTimeUpdated(it.hour, it.minute))
            }.onFailure {
                postSideEffect(SettingNotificationSideEffect.ShowUpdateAlarmTimeError)
            }
    }

    override suspend fun Syntax<SettingNotificationUiState, SettingNotificationSideEffect>.applyMutation(
        mutation: SettingNotificationMutation,
    ) {
        when (mutation) {
            is SettingNotificationMutation.ExpiryAlarmUpdated ->
                reduce { state.copy(isExpiryAlarmEnabled = mutation.enabled) }

            is SettingNotificationMutation.AlarmTimeUpdated ->
                reduce { state.copy(alarmHour = mutation.hour, alarmMinute = mutation.minute) }
        }
    }
}
