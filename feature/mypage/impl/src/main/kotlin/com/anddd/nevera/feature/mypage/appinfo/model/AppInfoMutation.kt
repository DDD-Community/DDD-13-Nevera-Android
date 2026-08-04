package com.anddd.nevera.feature.mypage.appinfo.model

import com.anddd.nevera.core.mvi.NeveraMutation

sealed interface AppInfoMutation : NeveraMutation {
    data object Loading : AppInfoMutation
    data class LoadCompleted(val appInfo: AppInfoUiModel) : AppInfoMutation
    data object LoadFailure : AppInfoMutation
}
