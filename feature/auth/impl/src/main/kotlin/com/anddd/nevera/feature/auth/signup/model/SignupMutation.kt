package com.anddd.nevera.feature.auth.signup.model

import com.anddd.nevera.core.common.CountDownTimer
import com.anddd.nevera.core.mvi.NeveraMutation
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationResult

sealed interface SignupMutation : NeveraMutation {
    data object Loading : SignupMutation
    data object Idle : SignupMutation
    data class EmailChanged(
        val email: String,
        val emailValidation: EmailValidationResult,
    ) : SignupMutation
    data class PasswordChanged(
        val password: String,
        val passwordValidation: PasswordValidationResult,
        val isPasswordMatched: Boolean,
    ) : SignupMutation
    data class ConfirmPasswordChanged(
        val confirmPassword: String,
        val isPasswordMatched: Boolean,
    ) : SignupMutation
    data class AuthCodeChanged(val authCode: String) : SignupMutation
    data object EmailRequestSuccess : SignupMutation
    data class EmailRequestFailed(val sectionError: AuthCodeSectionError) : SignupMutation
    data object AuthCodeVerified : SignupMutation
    data class AuthCodeVerifyFailed(val sectionError: AuthCodeSectionError) : SignupMutation
    data class ValidationFailed(
        val emailValidation: EmailValidationResult,
        val passwordValidation: PasswordValidationResult,
        val isPasswordMatched: Boolean,
    ) : SignupMutation
    data object SignupFailed : SignupMutation
    data class TimerStateChanged(val timerState: CountDownTimer.State) : SignupMutation
}
