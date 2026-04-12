package com.anddd.nevera.feature.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.domain.usecase.auth.LogoutUseCase
import com.anddd.nevera.domain.usecase.auth.WithdrawUseCase
import com.anddd.nevera.feature.main.home.model.HomeSideEffect
import com.anddd.nevera.feature.main.home.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val withdrawUseCase: WithdrawUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Success)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _sideEffect = Channel<HomeSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
                .onSuccess {
                    _sideEffect.send(HomeSideEffect.NavigateToLogin)
                }.onFailure {
                    _sideEffect.send(HomeSideEffect.ShowError("로그아웃에 실패했습니다."))
                }
        }
    }

    fun withdraw() {
        viewModelScope.launch {
            withdrawUseCase()
                .onSuccess {
                    _sideEffect.send(HomeSideEffect.NavigateToLogin)
                }.onFailure {
                    _sideEffect.send(HomeSideEffect.ShowError("회원 탈퇴에 실패했습니다."))
                }
        }
    }
}
