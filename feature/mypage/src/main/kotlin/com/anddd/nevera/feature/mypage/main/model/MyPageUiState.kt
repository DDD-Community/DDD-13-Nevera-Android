package com.anddd.nevera.feature.mypage.main.model

import com.anddd.nevera.core.mvi.NeveraState

data class MyPageUiState(
    val isLoading: Boolean = false,
    val hasUnreadNotification: Boolean = false,
    val profile: ProfileUiModel = ProfileUiModel(),
    val settingItems: List<SettingItem> = emptyList(),
) : NeveraState
