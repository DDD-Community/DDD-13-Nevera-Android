package com.anddd.nevera.feature.auth.main

import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationResult
import com.anddd.nevera.domain.scheduler.FcmSyncScheduler
import com.anddd.nevera.domain.usecase.auth.EmailLoginUseCase
import com.anddd.nevera.domain.usecase.auth.GoogleLoginUseCase
import com.anddd.nevera.domain.usecase.validation.ValidateEmailUseCase
import com.anddd.nevera.domain.usecase.validation.ValidatePasswordUseCase
import com.anddd.nevera.feature.auth.main.model.GoogleLoginErrorUiModel
import com.anddd.nevera.feature.auth.main.model.LoginIntent
import com.anddd.nevera.feature.auth.main.model.LoginMutation
import com.anddd.nevera.feature.auth.main.model.LoginSideEffect
import com.anddd.nevera.feature.auth.main.model.LoginUiState
import com.anddd.nevera.feature.auth.main.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.syntax.Syntax
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val emailLoginUseCase: EmailLoginUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val fcmSyncScheduler: FcmSyncScheduler,
) : NeveraViewModel<LoginUiState, LoginSideEffect, LoginIntent, LoginMutation>(LoginUiState()) {

    override fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> onEmailChanged(intent.email)
            is LoginIntent.PasswordChanged -> onPasswordChanged(intent.password)
            is LoginIntent.LoginWithEmailClicked -> onLoginWithEmailClicked()
            is LoginIntent.GoogleLoginButtonClicked -> onGoogleLoginButtonClicked()
            is LoginIntent.SignupClicked -> onSignupClicked()
            is LoginIntent.GoogleLoginSucceeded -> onLoginWithGoogle(intent.token)
            is LoginIntent.GoogleLoginFailed -> onGoogleLoginFailed()
        }
    }

    private fun onEmailChanged(email: String) = intent {
        applyMutation(LoginMutation.EmailChanged(email, validateEmailUseCase(email)))
    }

    private fun onPasswordChanged(password: String) = intent {
        applyMutation(LoginMutation.PasswordChanged(password, validatePasswordUseCase(password)))
    }

    private fun onLoginWithEmailClicked() = intent {
        val email = state.email
        val password = state.password
        val emailResult = validateEmailUseCase(email)
        val passwordResult = validatePasswordUseCase(password)
        if (!validateInputs(emailResult, passwordResult)) {
            applyMutation(LoginMutation.ValidationFailed(emailResult, passwordResult))
            return@intent
        }
        applyMutation(LoginMutation.Loading)
        emailLoginUseCase(email, password)
            .onSuccess {
                fcmSyncScheduler.scheduleSyncFcmToken()
                postSideEffect(LoginSideEffect.MoveToHomeScreen)
            }.onFailure { cause ->
                applyMutation(LoginMutation.LoginFailed)
                postSideEffect(LoginSideEffect.EmailLoginFailed(cause.toUiModel()))
            }
    }

    private fun onLoginWithGoogle(token: String) = intent {
        applyMutation(LoginMutation.Loading)
        googleLoginUseCase(token)
            .onSuccess {
                fcmSyncScheduler.scheduleSyncFcmToken()
                postSideEffect(LoginSideEffect.MoveToHomeScreen)
            }.onFailure { cause ->
                val errorUiModel = cause.toUiModel()
                Timber.e("googleLoginUseCase, Google 로그인 실패: ${errorUiModel::class.simpleName}")
                applyMutation(LoginMutation.LoginFailed)
                postSideEffect(LoginSideEffect.GoogleLoginFailed(errorUiModel))
            }
    }

    private fun onGoogleLoginButtonClicked() = intent {
        postSideEffect(LoginSideEffect.StartGoogleLogin)
    }

    private fun onSignupClicked() = intent {
        postSideEffect(LoginSideEffect.MoveToSignupScreen)
    }

    private fun onGoogleLoginFailed() = intent {
        applyMutation(LoginMutation.LoginFailed)
        postSideEffect(LoginSideEffect.GoogleLoginFailed(GoogleLoginErrorUiModel.Unknown))
    }

    private fun validateInputs(
        emailResult: EmailValidationResult,
        passwordResult: PasswordValidationResult,
    ): Boolean {
        return emailResult == EmailValidationResult.Valid &&
                passwordResult is PasswordValidationResult.Valid
    }

    override suspend fun Syntax<LoginUiState, LoginSideEffect>.applyMutation(mutation: LoginMutation) {
        when (mutation) {
            is LoginMutation.EmailChanged -> reduce {
                state.copy(
                    email = mutation.email,
                    emailValidation = mutation.validation
                )
            }
            is LoginMutation.PasswordChanged -> reduce {
                state.copy(
                    password = mutation.password,
                    passwordValidation = mutation.validation
                )
            }
            is LoginMutation.ValidationFailed -> reduce {
                state.copy(
                    emailValidation = mutation.emailResult,
                    passwordValidation = mutation.passwordResult
                )
            }
            LoginMutation.Loading -> reduce { state.copy(isLoading = true) }
            LoginMutation.LoginFailed -> reduce { state.copy(isLoading = false) }
        }
    }
}
