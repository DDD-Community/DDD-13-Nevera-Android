package com.anddd.nevera.feature.login.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.domain.model.notification.logFcmSyncFailure
import timber.log.Timber
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationResult
import com.anddd.nevera.domain.usecase.auth.EmailLoginUseCase
import com.anddd.nevera.domain.usecase.auth.GoogleLoginUseCase
import com.anddd.nevera.domain.usecase.notification.SyncFcmTokenUseCase
import com.anddd.nevera.domain.usecase.validation.ValidateEmailUseCase
import com.anddd.nevera.domain.usecase.validation.ValidatePasswordUseCase
import com.anddd.nevera.feature.login.BuildConfig
import com.anddd.nevera.feature.login.main.model.LoginSideEffect
import com.anddd.nevera.feature.login.main.model.LoginStatus
import com.anddd.nevera.feature.login.main.model.LoginUiState
import com.anddd.nevera.feature.login.main.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val emailLoginUseCase: EmailLoginUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val syncFcmTokenUseCase: SyncFcmTokenUseCase,
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
            emailLoginUseCase(email, password)
                .onSuccess {
                    // 로그인 성공 후 FCM 토큰 동기화 시도 (실패해도 화면 이동 진행)
                    syncFcmToken()
                    _uiState.update { it.copy(status = LoginStatus.Success) }
                    _sideEffect.send(LoginSideEffect.MoveToHomeScreen)
                }.onFailure { cause ->
                    _uiState.update { it.copy(status = LoginStatus.Idle) }
                    _sideEffect.send(LoginSideEffect.ShowErrorToast(cause.toUiModel().message))
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
            googleLoginUseCase(token)
                .onSuccess {
                    // 로그인 성공 후 FCM 토큰 동기화 시도 (실패해도 화면 이동 진행)
                    syncFcmToken()
                    _uiState.update { it.copy(status = LoginStatus.Success) }
                    _sideEffect.send(LoginSideEffect.MoveToHomeScreen)
                }.onFailure { cause ->
                    _uiState.update { it.copy(status = LoginStatus.Idle) }
                    _sideEffect.send(LoginSideEffect.ShowErrorToast(cause.toUiModel().message))
                }
        }
    }

    private suspend fun syncFcmToken() {
        try {
            syncFcmTokenUseCase()
                .logFcmSyncFailure(TAG, BuildConfig.DEBUG) { tag, message -> Timber.tag(tag).w(message) }
        } catch (ce: CancellationException) {
            throw ce
        } catch (t: Throwable) {
            Timber.e(t, "syncFcmToken")
        }
    }

    fun handleGoogleLoginFailure(throwable: Throwable) {
        viewModelScope.launch {
            if (BuildConfig.DEBUG) throwable.printStackTrace()
            _uiState.update { it.copy(status = LoginStatus.Idle) }
            _sideEffect.send(LoginSideEffect.ShowErrorToast("SNS 로그인에 실패했습니다."))
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}
