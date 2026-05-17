package com.anddd.nevera.feature.mypage.appinfo.model

import com.anddd.nevera.core.mvi.NeveraState

data class AppInfoUiState(
    val isLoading: Boolean = false,
    val appInfo: AppInfoUiModel = AppInfoUiModel(),
) : NeveraState
