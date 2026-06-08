package com.anddd.nevera.feature.mypage.settingnotification.model

import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface SettingNotificationMutation : NeveraMutation {
    data class ExpiryAlarmUpdated(val enabled: Boolean) : SettingNotificationMutation
    data class AlarmTimeUpdated(val hour: Int, val minute: Int) : SettingNotificationMutation
}
