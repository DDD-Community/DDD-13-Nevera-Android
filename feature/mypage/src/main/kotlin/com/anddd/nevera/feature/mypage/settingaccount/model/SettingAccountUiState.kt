package com.anddd.nevera.feature.mypage.settingaccount.model

import androidx.compose.runtime.Immutable
import com.anddd.nevera.core.mvi.NeveraState

@Immutable
data class SettingAccountUiState(
    val isLoading: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showWithdrawDialog: Boolean = false,
) : NeveraState
