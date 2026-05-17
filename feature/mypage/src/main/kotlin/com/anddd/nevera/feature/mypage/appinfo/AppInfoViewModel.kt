package com.anddd.nevera.feature.mypage.appinfo

import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.usecase.appinfo.GetAppInfoUseCase
import com.anddd.nevera.feature.mypage.appinfo.model.AppInfoIntent
import com.anddd.nevera.feature.mypage.appinfo.model.AppInfoMutation
import com.anddd.nevera.feature.mypage.appinfo.model.AppInfoSideEffect
import com.anddd.nevera.feature.mypage.appinfo.model.AppInfoUiState
import com.anddd.nevera.feature.mypage.appinfo.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class AppInfoViewModel @Inject constructor(
    private val getAppInfo: GetAppInfoUseCase,
) : NeveraViewModel<AppInfoUiState, AppInfoSideEffect, AppInfoIntent, AppInfoMutation>(
    AppInfoUiState()
) {
    init {
        loadAppInfo()
    }

    override fun handleIntent(intent: AppInfoIntent) {
        when (intent) {
            AppInfoIntent.NavigateBack -> onNavigateBack()
            AppInfoIntent.TermsClicked -> {
                // TODO onNavigateToTerms
            }
            AppInfoIntent.PrivacyPolicyClicked -> {
                // TODO onNavigateToPrivacyPolicy
            }
        }
    }

    private fun loadAppInfo() = intent {
        applyMutation(AppInfoMutation.Loading)
        getAppInfo().onSuccess {
            applyMutation(AppInfoMutation.LoadCompleted(it.toUiModel()))
        }.onFailure {
            applyMutation(AppInfoMutation.LoadFailure)
            postSideEffect(AppInfoSideEffect.ShowNetworkErrorToast)
        }
    }

    private fun onNavigateBack() = intent {
        postSideEffect(AppInfoSideEffect.NavigateBack)
    }

    override suspend fun Syntax<AppInfoUiState, AppInfoSideEffect>.applyMutation(mutation: AppInfoMutation) {
        when (mutation) {
            AppInfoMutation.Loading -> reduce { state.copy(isLoading = true) }
            is AppInfoMutation.LoadCompleted -> reduce { state.copy(isLoading = false, appInfo = mutation.appInfo) }
            AppInfoMutation.LoadFailure -> reduce { state.copy(isLoading = false) }
        }
    }
}
