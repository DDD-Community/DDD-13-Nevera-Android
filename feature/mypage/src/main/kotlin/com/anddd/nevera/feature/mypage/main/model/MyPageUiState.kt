package com.anddd.nevera.feature.mypage.main.model

import com.anddd.nevera.core.mvi.NeveraState

data class MyPageUiState(
    val status: MyPageStatus = MyPageStatus.Idle,
    val profile: ProfileUiModel = ProfileUiModel(),
    val settingItems: List<SettingItem> = emptyList(),
) : NeveraState

sealed interface MyPageStatus {
    data object Idle : MyPageStatus
    data object Loading : MyPageStatus
    data object Success : MyPageStatus
}
