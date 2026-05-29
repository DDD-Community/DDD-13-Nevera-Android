package com.anddd.nevera.feature.mypage.main.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface MyPageSideEffect : NeveraSideEffect {
    data object ShowNetworkErrorToast : MyPageSideEffect
    data object NavigateToAppInfo : MyPageSideEffect
    data object NavigateToAccountSetting : MyPageSideEffect
    data object NavigateToNotification : MyPageSideEffect
}
