package com.anddd.nevera.feature.auth.signup

import androidx.lifecycle.viewModelScope
import com.anddd.nevera.core.common.CountDownTimer
import com.anddd.nevera.core.common.onFailure
import com.anddd.nevera.core.common.onSuccess
import com.anddd.nevera.core.mvi.NeveraViewModel
import com.anddd.nevera.domain.model.auth.EmailRequestError
import com.anddd.nevera.domain.model.auth.EmailVerifyError
import com.anddd.nevera.domain.model.auth.SignupError
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationResult
import com.anddd.nevera.domain.usecase.auth.SignupUseCase
import com.anddd.nevera.domain.usecase.email.EmailRequestUseCase
import com.anddd.nevera.domain.usecase.email.EmailVerifyUseCase
import com.anddd.nevera.domain.usecase.validation.ValidateEmailUseCase
import com.anddd.nevera.domain.usecase.validation.ValidatePasswordUseCase
import com.anddd.nevera.feature.auth.signup.model.AuthCodeSectionError
import com.anddd.nevera.feature.auth.signup.model.SignupIntent
import com.anddd.nevera.feature.auth.signup.model.SignupMutation
import com.anddd.nevera.feature.auth.signup.model.SignupSideEffect
import com.anddd.nevera.feature.auth.signup.model.SignupUiState
import com.anddd.nevera.feature.auth.signup.model.withAuthCodeDescription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val emailRequestUseCase: EmailRequestUseCase,
    private val emailVerifyUseCase: EmailVerifyUseCase,
    private val signupUseCase: SignupUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
) : NeveraViewModel<SignupUiState, SignupSideEffect, SignupIntent, SignupMutation>(
    SignupUiState()
) {
    private val countdownTimer = CountDownTimer()
    private var timerJob: Job? = null

    // 인증번호 유효시간 타이머를 구독해 UiState로 반영한다.
    init {
        countdownTimer.state
            .onEach(::onTimerStateChanged)
            .launchIn(viewModelScope)
    }

    private fun startCountdownTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch { countdownTimer.start(totalSeconds = 180, canResendAfterSeconds = 120) }
    }

    private fun resetCountdownTimer() {
        timerJob?.cancel()
        countdownTimer.reset()
    }

    // 타이머 상태 변경을 화면 상태와 만료 side effect로 변환한다.
    private fun onTimerStateChanged(timerState: CountDownTimer.State) = intent {
        applyMutation(SignupMutation.TimerStateChanged(timerState))
        if (timerState is CountDownTimer.State.Expired) {
            postSideEffect(SignupSideEffect.TimerExpired)
        }
    }

    // 화면 이벤트를 회원가입 플로우의 내부 처리 함수로 라우팅한다.
    override fun handleIntent(intent: SignupIntent) {
        when (intent) {
            is SignupIntent.EmailChanged -> onEmailChange(intent.email)
            is SignupIntent.PasswordChanged -> onPasswordChange(intent.password)
            is SignupIntent.ConfirmPasswordChanged -> onConfirmPasswordChange(intent.confirmPassword)
            is SignupIntent.AuthCodeChanged -> onAuthCodeChange(intent.authCode)
            is SignupIntent.RequestEmailVerification -> requestEmailVerification()
            is SignupIntent.VerifyAuthCode -> verifyAuthCode()
            is SignupIntent.Signup -> signup()
            is SignupIntent.NavigateBack -> navigateBack()
        }
    }

    // 이메일 입력값을 저장하고 즉시 형식을 검증한다.
    private fun onEmailChange(email: String) = intent {
        resetCountdownTimer()
        val emailValidation = validateEmailUseCase(email)
        applyMutation(
            SignupMutation.EmailChanged(
                email = email,
                emailValidation = emailValidation,
            )
        )
    }

    // 비밀번호 입력값을 저장하고 조건 충족 및 확인 비밀번호 일치 여부를 갱신한다.
    private fun onPasswordChange(password: String) = intent {
        val passwordValidation = validatePasswordUseCase(password)
        val isPasswordMatched = password == state.confirmPassword
        applyMutation(
            SignupMutation.PasswordChanged(
                password = password,
                passwordValidation = passwordValidation,
                isPasswordMatched = isPasswordMatched,
            )
        )
    }

    // 비밀번호 확인 입력값을 저장하고 원본 비밀번호와의 일치 여부를 갱신한다.
    private fun onConfirmPasswordChange(confirmPassword: String) = intent {
        val isPasswordMatched = state.password == confirmPassword
        applyMutation(
            SignupMutation.ConfirmPasswordChanged(
                confirmPassword = confirmPassword,
                isPasswordMatched = isPasswordMatched,
            )
        )
    }

    // 인증코드 입력값을 저장하고 기존 인증코드 오류를 초기화한다.
    private fun onAuthCodeChange(authCode: String) = intent {
        applyMutation(SignupMutation.AuthCodeChanged(authCode))
    }

    // 이메일 인증번호 발송 또는 재발송을 요청한다.
    private fun requestEmailVerification() = intent {
        val requestedEmail = state.email
        val emailResult = validateEmailUseCase(requestedEmail)
        if (emailResult !is EmailValidationResult.Valid) {
            resetCountdownTimer()
            applyMutation(SignupMutation.EmailChanged(requestedEmail, emailResult))
            return@intent
        }
        val isResend = state.isEmailRequestSent
        applyMutation(SignupMutation.Loading)
        emailRequestUseCase(requestedEmail)
            .onSuccess { onEmailRequestSuccess(requestedEmail) }
            .onFailure { onEmailRequestFailure(requestedEmail, it, isResend) }
    }

    // 인증번호 발송 성공을 현재 이메일 상태에만 반영한다.
    private suspend fun Syntax<SignupUiState, SignupSideEffect>.onEmailRequestSuccess(
        requestedEmail: String,
    ) {
        // 요청 중 이메일이 바뀐 경우 오래된 응답은 무시한다.
        if (state.email == requestedEmail) {
            startCountdownTimer()
            applyMutation(SignupMutation.EmailRequestSuccess)
        } else {
            applyMutation(SignupMutation.Idle)
        }
    }

    // 인증번호 발송 실패를 토스트와 필요 시 인증코드 섹션 오류로 변환한다.
    private suspend fun Syntax<SignupUiState, SignupSideEffect>.onEmailRequestFailure(
        requestedEmail: String,
        error: EmailRequestError,
        isResend: Boolean,
    ) {
        if (state.email != requestedEmail) {
            applyMutation(SignupMutation.Idle)
            return
        }
        when (error) {
            is EmailRequestError.DuplicateEmail -> {
                // 재발송 중에는 이미 열린 인증코드 섹션에도 오류를 표시한다.
                val sectionError = if (isResend) AuthCodeSectionError.EmailAlreadyRegistered else AuthCodeSectionError.None
                applyMutation(SignupMutation.EmailRequestFailed(sectionError))
                postSideEffect(SignupSideEffect.EmailRequestDuplicateEmail(error.serverMessage))
            }
            is EmailRequestError.MailSendError -> {
                applyMutation(SignupMutation.EmailRequestFailed(AuthCodeSectionError.None))
                postSideEffect(SignupSideEffect.EmailRequestMailSendError(error.serverMessage))
            }
            is EmailRequestError.Common -> {
                applyMutation(SignupMutation.EmailRequestFailed(AuthCodeSectionError.None))
                postSideEffect(SignupSideEffect.EmailRequestNetworkError(null))
            }
        }
    }

    // 입력된 인증코드가 현재 이메일에 대해 유효한지 확인한다.
    private fun verifyAuthCode() = intent {
        if (state.authCode.isBlank()) return@intent
        val requestedEmail = state.email
        val requestedAuthCode = state.authCode
        applyMutation(SignupMutation.Loading)
        emailVerifyUseCase(requestedEmail, requestedAuthCode)
            .onSuccess {
                // 요청 중 이메일이 바뀐 경우 인증 완료로 반영하지 않는다.
                if (state.email == requestedEmail) {
                    resetCountdownTimer()
                    applyMutation(SignupMutation.AuthCodeVerified)
                } else {
                    applyMutation(SignupMutation.Idle)
                }
            }
            .onFailure { error ->
                // 서버 오류를 인증코드 섹션에서 표현 가능한 상태로 축소한다.
                val sectionError = when (error) {
                    is EmailVerifyError.InvalidCode -> AuthCodeSectionError.InvalidCode
                    is EmailVerifyError.ExpiredCode -> AuthCodeSectionError.ServerExpired
                    is EmailVerifyError.NotFound -> AuthCodeSectionError.NotFound
                    is EmailVerifyError.Common -> AuthCodeSectionError.InvalidCode
                }
                applyMutation(SignupMutation.AuthCodeVerifyFailed(sectionError))
                if (error is EmailVerifyError.NotFound) {
                    postSideEffect(SignupSideEffect.EmailVerifyNotFound(error.serverMessage))
                }
            }
    }

    // 모든 입력과 이메일 인증 상태를 확인한 뒤 회원가입을 요청한다.
    private fun signup() = intent {
        if (!state.isEmailVerified) {
            postSideEffect(SignupSideEffect.SignupEmailNotVerified)
            return@intent
        }
        if (!validateInputs(state.email, state.password, state.confirmPassword)) {
            val emailValidation = validateEmailUseCase(state.email)
            val passwordValidation = validatePasswordUseCase(state.password)
            val isPasswordMatched = state.password == state.confirmPassword
            applyMutation(
                SignupMutation.ValidationFailed(
                    emailValidation = emailValidation,
                    passwordValidation = passwordValidation,
                    isPasswordMatched = isPasswordMatched,
                )
            )
            return@intent
        }
        applyMutation(SignupMutation.Loading)
        signupUseCase(state.email, state.password)
            .onSuccess {
                postSideEffect(SignupSideEffect.MoveToLoginScreen)
            }
            .onFailure { error ->
                when (error) {
                    is SignupError.UnverifiedEmail -> {
                        applyMutation(SignupMutation.SignupFailed)
                        postSideEffect(SignupSideEffect.SignupUnverifiedEmail(error.serverMessage))
                    }
                    is SignupError.NotFound -> {
                        applyMutation(SignupMutation.SignupFailed)
                        postSideEffect(SignupSideEffect.SignupAuthNotFound(error.serverMessage))
                    }
                    is SignupError.Common -> {
                        applyMutation(SignupMutation.SignupFailed)
                        postSideEffect(SignupSideEffect.SignupServerError)
                    }
                }
            }
    }

    private fun navigateBack() = intent {
        postSideEffect(SignupSideEffect.MoveToLoginScreen)
    }

    // 제출 전 이메일, 비밀번호, 비밀번호 확인 조건을 최종 검증한다.
    private fun validateInputs(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        val isEmailValid = validateEmailUseCase(email) is EmailValidationResult.Valid
        val isPasswordValid = validatePasswordUseCase(password) is PasswordValidationResult.Valid
        val isConfirmPasswordEntered = confirmPassword.isNotBlank()
        val isPasswordMatched = password == confirmPassword
        return isEmailValid && isPasswordValid && isConfirmPasswordEntered && isPasswordMatched
    }

    // Mutation을 단일 상태 변경 지점에서 처리한다.
    override suspend fun Syntax<SignupUiState, SignupSideEffect>.applyMutation(
        mutation: SignupMutation
    ) {
        when (mutation) {
            SignupMutation.Loading -> reduce { state.copy(isLoading = true) }
            SignupMutation.Idle -> reduce { state.copy(isLoading = false) }
            is SignupMutation.EmailChanged -> {
                reduce {
                    state.copy(
                        email = mutation.email,
                        emailValidation = mutation.emailValidation,
                        authCode = "",
                        isEmailRequestSent = false,
                        isEmailVerified = false,
                        authCodeSectionError = AuthCodeSectionError.None,
                        timerState = CountDownTimer.State.Idle,
                    ).withAuthCodeDescription()
                }
            }
            is SignupMutation.PasswordChanged -> reduce {
                state.copy(
                    password = mutation.password,
                    passwordValidation = mutation.passwordValidation,
                    isPasswordMatched = mutation.isPasswordMatched,
                )
            }
            is SignupMutation.ConfirmPasswordChanged -> reduce {
                state.copy(
                    confirmPassword = mutation.confirmPassword,
                    isPasswordMatched = mutation.isPasswordMatched,
                )
            }
            is SignupMutation.AuthCodeChanged -> reduce {
                // 새 입력값에 대해서는 이전 인증 실패 메시지를 유지하지 않는다.
                state.copy(
                    authCode = mutation.authCode, 
                    authCodeSectionError = AuthCodeSectionError.None
                ).withAuthCodeDescription()
            }
            SignupMutation.EmailRequestSuccess -> {
                reduce {
                    state.copy(
                        isLoading = false,
                        isEmailRequestSent = true,
                        emailRequestCount = state.emailRequestCount + 1,
                        authCodeSectionError = AuthCodeSectionError.None,
                    ).withAuthCodeDescription()
                }
            }
            is SignupMutation.EmailRequestFailed -> reduce {
                state.copy(isLoading = false, authCodeSectionError = mutation.sectionError)
                    .withAuthCodeDescription()
            }
            SignupMutation.AuthCodeVerified -> reduce {
                state.copy(
                    isLoading = false,
                    isEmailVerified = true,
                    authCodeSectionError = AuthCodeSectionError.None,
                    timerState = CountDownTimer.State.Idle,
                ).withAuthCodeDescription()
            }
            is SignupMutation.AuthCodeVerifyFailed -> reduce {
                state.copy(isLoading = false, authCodeSectionError = mutation.sectionError)
                    .withAuthCodeDescription()
            }
            is SignupMutation.ValidationFailed -> reduce {
                state.copy(
                    emailValidation = mutation.emailValidation,
                    passwordValidation = mutation.passwordValidation,
                    isPasswordMatched = mutation.isPasswordMatched,
                )
            }
            SignupMutation.SignupFailed -> reduce { state.copy(isLoading = false) }
            is SignupMutation.TimerStateChanged -> reduce {
                state.copy(timerState = mutation.timerState)
                    .withAuthCodeDescription()
            }
        }
    }
}
