package com.anddd.nevera.feature.login.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.model.SnsProvider
import com.anddd.nevera.domain.usecase.EmailLoginUseCase
import com.anddd.nevera.domain.usecase.GoogleLoginUseCase
import com.anddd.nevera.domain.usecase.SnsLoginUseCase
import com.anddd.nevera.domain.usecase.validator.EmailValidationResult
import com.anddd.nevera.domain.usecase.validator.PasswordValidationResult
import com.anddd.nevera.feature.login.main.model.LoginSideEffect
import com.anddd.nevera.feature.login.main.model.LoginStatus
import com.anddd.nevera.feature.login.main.model.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val emailLoginUseCase: EmailLoginUseCase,
    private val snsLoginUseCase: SnsLoginUseCase,
    private val googleLogin: GoogleLoginUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _sideEffect = Channel<LoginSideEffect>(Channel.Factory.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(emailValidation = emailLoginUseCase.validateEmail(email)) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(passwordValidation = emailLoginUseCase.validatePassword(password)) }
    }

    fun login(email: String, password: String) {
        if (!validateInputs(email, password)) return

        viewModelScope.launch {
            _uiState.update { it.copy(status = LoginStatus.Loading) }
            when (val result = emailLoginUseCase(email, password)) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(status = LoginStatus.Success(result.data.user.id)) }

                is ApiResult.Error -> {
                    _uiState.update { it.copy(status = LoginStatus.Idle) }
                    _sideEffect.send(LoginSideEffect.ShowErrorToast(result.error.message ?: "로그인에 실패했습니다."))
                }
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        val emailResult = emailLoginUseCase.validateEmail(email)
        val passwordResult = emailLoginUseCase.validatePassword(password)
        _uiState.update { it.copy(emailValidation = emailResult, passwordValidation = passwordResult) }
        return emailResult == EmailValidationResult.Valid && passwordResult is PasswordValidationResult.Valid
    }

    fun snsLogin(provider: SnsProvider, token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(status = LoginStatus.Loading) }
            when (val result = snsLoginUseCase(provider, token)) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(status = LoginStatus.Success(result.data.user.id)) }

                is ApiResult.Error -> {
                    _uiState.update { it.copy(status = LoginStatus.Idle) }
                    _sideEffect.send(LoginSideEffect.ShowErrorToast(result.error.message ?: "SNS 로그인에 실패했습니다."))
                }
            }
        }
    }

    fun onGoogleLogin() {
        viewModelScope.launch {
            googleLogin()
        }
    }
}