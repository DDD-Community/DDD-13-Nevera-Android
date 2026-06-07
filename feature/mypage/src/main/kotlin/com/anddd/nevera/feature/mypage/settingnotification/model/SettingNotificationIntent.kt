package com.anddd.nevera.feature.mypage.settingnotification.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface SettingNotificationIntent : NeveraIntent {
    data object NavigateBack : SettingNotificationIntent
    data class ExpiryAlarmToggled(
        val enabled: Boolean,
        val isSystemNotificationEnabled: Boolean,
    ) : SettingNotificationIntent
    data object AlarmTimeClicked : SettingNotificationIntent
    data class AlarmTimeSelected(val hour: Int, val minute: Int) : SettingNotificationIntent
    data object NavigateToNotificationSettingsClicked : SettingNotificationIntent
}
