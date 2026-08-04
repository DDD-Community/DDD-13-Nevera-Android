package com.anddd.nevera.feature.mypage.settingaccount.model

import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface SettingAccountMutation : NeveraMutation {
    data object Loading : SettingAccountMutation
    data object LoadingComplete : SettingAccountMutation
    data object ShowLogoutDialog : SettingAccountMutation
    data object HideLogoutDialog : SettingAccountMutation
    data object ShowWithdrawDialog : SettingAccountMutation
    data object HideWithdrawDialog : SettingAccountMutation
}
