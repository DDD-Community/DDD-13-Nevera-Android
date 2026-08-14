package com.anddd.nevera.feature.mypage.settingaccount.model

import com.anddd.nevera.core.mvi.NeveraIntent

sealed interface SettingAccountIntent : NeveraIntent {
    data object NavigateBack : SettingAccountIntent
    data object LogoutClicked : SettingAccountIntent
    data object WithdrawClicked : SettingAccountIntent
    data object ConfirmLogoutClicked : SettingAccountIntent
    data object CancelLogoutClicked : SettingAccountIntent
    data object ConfirmWithdrawClicked : SettingAccountIntent
    data object CancelWithdrawClicked : SettingAccountIntent
}
