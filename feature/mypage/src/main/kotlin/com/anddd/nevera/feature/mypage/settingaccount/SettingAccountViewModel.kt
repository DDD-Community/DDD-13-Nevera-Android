package com.anddd.nevera.feature.mypage.settingaccount

import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.domain.model.auth.WithdrawError
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.usecase.auth.LogoutUseCase
import com.anddd.nevera.domain.usecase.auth.WithdrawUseCase
import com.anddd.nevera.feature.mypage.BuildConfig
import com.anddd.nevera.feature.mypage.settingaccount.model.SettingAccountIntent
import com.anddd.nevera.feature.mypage.settingaccount.model.SettingAccountMutation
import com.anddd.nevera.feature.mypage.settingaccount.model.SettingAccountSideEffect
import com.anddd.nevera.feature.mypage.settingaccount.model.SettingAccountUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class SettingAccountViewModel @Inject constructor(
    private val logout: LogoutUseCase,
    private val withdraw: WithdrawUseCase,
) : NeveraViewModel<SettingAccountUiState, SettingAccountSideEffect, SettingAccountIntent, SettingAccountMutation>(
    SettingAccountUiState()
) {

    override fun handleIntent(intent: SettingAccountIntent) {
        when (intent) {
            SettingAccountIntent.NavigateBack -> intent {
                postSideEffect(SettingAccountSideEffect.NavigateBack)
            }

            SettingAccountIntent.LogoutClicked -> intent {
                applyMutation(SettingAccountMutation.ShowLogoutDialog)
            }

            SettingAccountIntent.CancelLogoutClicked -> intent {
                applyMutation(SettingAccountMutation.HideLogoutDialog)
            }

            SettingAccountIntent.ConfirmLogoutClicked -> onLogout()

            SettingAccountIntent.WithdrawClicked -> intent {
                applyMutation(SettingAccountMutation.ShowWithdrawDialog)
            }

            SettingAccountIntent.CancelWithdrawClicked -> intent {
                applyMutation(SettingAccountMutation.HideWithdrawDialog)
            }

            SettingAccountIntent.ConfirmWithdrawClicked -> onWithdraw()
        }
    }

    private fun onLogout() = intent {
        applyMutation(SettingAccountMutation.HideLogoutDialog)
        applyMutation(SettingAccountMutation.Loading)
        logout(BuildConfig.DEBUG).onSuccess {
            postSideEffect(SettingAccountSideEffect.NavigateToLogin)
        }.onFailure {
            applyMutation(SettingAccountMutation.LoadingComplete)
            postSideEffect(SettingAccountSideEffect.ShowNetworkErrorToast)
        }
    }

    private fun onWithdraw() = intent {
        applyMutation(SettingAccountMutation.HideWithdrawDialog)
        applyMutation(SettingAccountMutation.Loading)
        withdraw().onSuccess {
            postSideEffect(SettingAccountSideEffect.NavigateToLogin)
        }.onFailure { error ->
            when (error) {
                is WithdrawError.SessionInvalid ->
                    postSideEffect(SettingAccountSideEffect.NavigateToLogin)
                is WithdrawError.Common -> {
                    applyMutation(SettingAccountMutation.LoadingComplete)
                    postSideEffect(SettingAccountSideEffect.ShowNetworkErrorToast)
                }
            }
        }
    }

    override suspend fun Syntax<SettingAccountUiState, SettingAccountSideEffect>.applyMutation(mutation: SettingAccountMutation) {
        when (mutation) {
            SettingAccountMutation.Loading -> reduce { state.copy(isLoading = true) }

            SettingAccountMutation.LoadingComplete -> reduce { state.copy(isLoading = false) }

            SettingAccountMutation.ShowLogoutDialog -> reduce { state.copy(showLogoutDialog = true) }

            SettingAccountMutation.HideLogoutDialog -> reduce { state.copy(showLogoutDialog = false) }

            SettingAccountMutation.ShowWithdrawDialog -> reduce { state.copy(showWithdrawDialog = true) }

            SettingAccountMutation.HideWithdrawDialog -> reduce { state.copy(showWithdrawDialog = false) }
        }
    }
}
