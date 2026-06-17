package com.anddd.nevera.feature.mypage.settingnotification.model

import androidx.compose.runtime.Immutable
import com.anddd.nevera.core.mvi.NeveraState

@Immutable
data class SettingNotificationUiState(
    val isExpiryAlarmEnabled: Boolean = false,
    val alarmHour: Int = 18,
    val alarmMinute: Int = 0,
) : NeveraState

val SettingNotificationUiState.formattedAlarmTime: String
    get() = "%02d:%02d".format(alarmHour, alarmMinute)
