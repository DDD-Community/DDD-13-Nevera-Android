package com.anddd.nevera.feature.signup.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.usecase.EmailRequestUseCase
import com.anddd.nevera.domain.usecase.EmailVerifyUseCase
import com.anddd.nevera.domain.usecase.SignupUseCase
import com.anddd.nevera.domain.usecase.ValidateEmailUseCase
import com.anddd.nevera.domain.usecase.ValidatePasswordUseCase
import com.anddd.nevera.domain.usecase.validator.EmailValidationResult
import com.anddd.nevera.domain.usecase.validator.PasswordValidationResult
import com.anddd.nevera.feature.signup.main.model.SignupSideEffect
import com.anddd.nevera.feature.signup.main.model.SignupStatus
import com.anddd.nevera.feature.signup.main.model.SignupUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val emailRequestUseCase: EmailRequestUseCase,
    private val emailVerifyUseCase: EmailVerifyUseCase,
    private val signupUseCase: SignupUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState

    private val _sideEffect = Channel<SignupSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailValidation = validateEmailUseCase(email),
                isEmailRequestSent = false,
                isEmailVerified = false
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

    fun onAuthCodeChange(authCode: String) {
        _uiState.update { it.copy(authCode = authCode) }
    }

    fun requestEmailVerification() {
        val email = _uiState.value.email
        val emailResult = validateEmailUseCase(email)
        if (emailResult != EmailValidationResult.Valid) {
            _uiState.update { it.copy(emailValidation = emailResult) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(status = SignupStatus.Loading) }
            when (val result = emailRequestUseCase(email)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(status = SignupStatus.Idle, isEmailRequestSent = true) }
                    _sideEffect.send(SignupSideEffect.ShowToast("인증 코드가 이메일로 발송되었습니다."))
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(status = SignupStatus.Idle) }
                    _sideEffect.send(SignupSideEffect.ShowToast(result.error.message ?: "인증 요청에 실패했습니다."))
                }
            }
        }
    }

    fun verifyAuthCode() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(status = SignupStatus.Loading) }
            when (val result = emailVerifyUseCase(state.email, state.authCode)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(status = SignupStatus.Idle, isEmailVerified = true) }
                    _sideEffect.send(SignupSideEffect.ShowToast("이메일 인증이 완료되었습니다."))
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(status = SignupStatus.Idle) }
                    _sideEffect.send(SignupSideEffect.ShowToast(result.error.message ?: "인증 확인에 실패했습니다."))
                }
            }
        }
    }

    fun signup() {
        val state = _uiState.value
        if (!validateInputs(state.email, state.password)) return

        viewModelScope.launch {
            _uiState.update { it.copy(status = SignupStatus.Loading) }
            when (val result = signupUseCase(state.email, state.password, state.name)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(status = SignupStatus.Success) }
                    _sideEffect.send(SignupSideEffect.MoveToLoginScreen)
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(status = SignupStatus.Idle) }
                    _sideEffect.send(SignupSideEffect.ShowToast(result.error.message ?: "회원가입에 실패했습니다."))
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
}
