package com.anddd.nevera.feature.signup.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.core.common.ApiResult
import com.anddd.nevera.domain.usecase.email.EmailRequestUseCase
import com.anddd.nevera.domain.usecase.email.EmailVerifyUseCase
import com.anddd.nevera.domain.usecase.auth.SignupUseCase
import com.anddd.nevera.domain.usecase.validation.ValidateEmailUseCase
import com.anddd.nevera.domain.usecase.validation.ValidatePasswordUseCase
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationResult
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
                authCode = "",
                isEmailRequestSent = false,
                isEmailVerified = false
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordValidation = validatePasswordUseCase(password),
                isPasswordMatched = password == it.confirmPassword
            )
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                isPasswordMatched = it.password == confirmPassword
            )
        }
    }

    fun onAuthCodeChange(authCode: String) {
        _uiState.update { it.copy(authCode = authCode) }
    }

    fun requestEmailVerification() {
        val requestedEmail = _uiState.value.email
        val emailResult = validateEmailUseCase(requestedEmail)
        if (emailResult != EmailValidationResult.Valid) {
            _uiState.update { it.copy(emailValidation = emailResult) }
            return
        }
        // 인증코드 초기화
        _uiState.update { it.copy(authCode = "") }

        viewModelScope.launch {
            _uiState.update { it.copy(status = SignupStatus.Loading) }
            when (val result = emailRequestUseCase(requestedEmail)) {
                is ApiResult.Success -> {
                    _uiState.update { current ->
                        if (current.email == requestedEmail)
                            current.copy(status = SignupStatus.Idle, isEmailRequestSent = true)
                        else
                            current.copy(status = SignupStatus.Idle)
                    }
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
        if (state.authCode.isBlank()) {
            _sideEffect.trySend(SignupSideEffect.ShowToast("인증 코드를 입력해주세요."))
            return
        }
        val requestedEmail = state.email
        val requestedAuthCode = state.authCode
        viewModelScope.launch {
            _uiState.update { it.copy(status = SignupStatus.Loading) }
            when (val result = emailVerifyUseCase(requestedEmail, requestedAuthCode)) {
                is ApiResult.Success -> {
                    _uiState.update { current ->
                        if (current.email == requestedEmail)
                            current.copy(status = SignupStatus.Idle, isEmailVerified = true)
                        else
                            current.copy(status = SignupStatus.Idle)
                    }
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
        if (state.name.isBlank()) {
            _sideEffect.trySend(SignupSideEffect.ShowToast("이름을 입력해주세요."))
            return
        }
        if (!state.isEmailVerified) {
            _sideEffect.trySend(SignupSideEffect.ShowToast("이메일 인증을 완료해주세요."))
            return
        }
        if (!validateInputs(state.email, state.password, state.confirmPassword)) return

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

    private fun validateInputs(email: String, password: String, confirmPassword: String): Boolean {
        val emailResult = validateEmailUseCase(email)
        val passwordResult = validatePasswordUseCase(password)
        val isPasswordMatched = password == confirmPassword
        _uiState.update {
            it.copy(
                emailValidation = emailResult,
                passwordValidation = passwordResult,
                isPasswordMatched = isPasswordMatched
            )
        }
        if (!isPasswordMatched) {
            _sideEffect.trySend(SignupSideEffect.ShowToast("비밀번호가 일치하지 않습니다."))
        }
        return emailResult == EmailValidationResult.Valid &&
            passwordResult is PasswordValidationResult.Valid &&
            isPasswordMatched &&
            confirmPassword.isNotBlank()
    }
}
