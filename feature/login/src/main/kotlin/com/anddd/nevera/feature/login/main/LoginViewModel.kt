package com.anddd.nevera.feature.login.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.usecase.EmailLoginUseCase
import com.anddd.nevera.domain.usecase.GoogleLoginUseCase
import com.anddd.nevera.domain.usecase.ValidateEmailUseCase
import com.anddd.nevera.domain.usecase.ValidatePasswordUseCase
import com.anddd.nevera.domain.usecase.validator.EmailValidationResult
import com.anddd.nevera.domain.usecase.validator.PasswordValidationResult
import com.anddd.nevera.feature.login.BuildConfig
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
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _sideEffect = Channel<LoginSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailValidation = validateEmailUseCase(email)
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordValidation = validatePasswordUseCase(password)
            )
        }
    }

    fun loginWithEmail(email: String, password: String) {
        if (!validateInputs(email, password)) return

        viewModelScope.launch {
            _uiState.update { it.copy(status = LoginStatus.Loading) }
            when (val result = emailLoginUseCase(email, password)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(status = LoginStatus.Success) }
                    _sideEffect.send(LoginSideEffect.MoveToHomeScreen)
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(status = LoginStatus.Idle) }
                    _sideEffect.send(LoginSideEffect.ShowErrorToast(result.error.message ?: "로그인에 실패했습니다."))
                }
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        val emailResult = validateEmailUseCase(email)
        val passwordResult = validatePasswordUseCase(password)
        _uiState.update { it.copy(emailValidation = emailResult, passwordValidation = passwordResult) }
        return emailResult == EmailValidationResult.Valid && passwordResult is PasswordValidationResult.Valid
    }

    fun loginWithGoogle(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(status = LoginStatus.Loading) }
            when (val result = googleLoginUseCase(token)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(status = LoginStatus.Success) }
                    _sideEffect.send(LoginSideEffect.MoveToHomeScreen)
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(status = LoginStatus.Idle) }
                    _sideEffect.send(LoginSideEffect.ShowErrorToast(result.error.message ?: "SNS 로그인에 실패했습니다."))
                }
            }
        }
    }

    fun handleGoogleLoginFailure(throwable: Throwable) {
        viewModelScope.launch {
            if (BuildConfig.DEBUG) throwable.printStackTrace()
            _uiState.update { it.copy(status = LoginStatus.Idle) }
            _sideEffect.send(LoginSideEffect.ShowErrorToast("SNS 로그인에 실패했습니다."))
        }
    }
}
