package com.anddd.nevera.feature.mypage.settingnotification.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface SettingNotificationSideEffect : NeveraSideEffect {
    data object NavigateBack : SettingNotificationSideEffect
    data object ShowTimePickerDialog : SettingNotificationSideEffect
    data object ShowPermissionDeniedDialog : SettingNotificationSideEffect
    data object OpenNotificationSettings : SettingNotificationSideEffect
    data object ShowLoadAlarmTimeError : SettingNotificationSideEffect
    data object ShowUpdateAlarmTimeError : SettingNotificationSideEffect
    data object ShowUpdateNotificationEnabledError : SettingNotificationSideEffect
}
