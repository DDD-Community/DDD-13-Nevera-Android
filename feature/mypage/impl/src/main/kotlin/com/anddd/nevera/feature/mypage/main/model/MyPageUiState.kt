package com.anddd.nevera.feature.mypage.main.model

import androidx.compose.runtime.Immutable
import com.anddd.nevera.core.mvi.NeveraState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class MyPageUiState(
    val isLoading: Boolean = false,
    val hasUnreadNotification: Boolean = false,
    val profile: ProfileUiModel = ProfileUiModel(),
    val settingItems: ImmutableList<SettingItem> = persistentListOf(),
) : NeveraState
