package com.anddd.nevera.feature.mypage.appinfo.model

import com.anddd.nevera.core.mvi.NeveraSideEffect

sealed interface AppInfoSideEffect : NeveraSideEffect {
    data object NavigateBack : AppInfoSideEffect
    data object ShowNetworkErrorToast : AppInfoSideEffect
    data class OpenUrl(val url: String) : AppInfoSideEffect
}
