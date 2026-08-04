package com.anddd.nevera.feature.mypage.settingaccount.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface SettingAccountSideEffect : NeveraSideEffect {
    data object NavigateBack : SettingAccountSideEffect
    data object NavigateToLogin : SettingAccountSideEffect
    data object ShowNetworkErrorToast : SettingAccountSideEffect
}
