package com.anddd.nevera.feature.mypage.appinfo.model

import androidx.compose.runtime.Immutable
import com.anddd.nevera.core.mvi.NeveraState

@Immutable
data class AppInfoUiState(
    val isLoading: Boolean = false,
    val appInfo: AppInfoUiModel = AppInfoUiModel(),
) : NeveraState
