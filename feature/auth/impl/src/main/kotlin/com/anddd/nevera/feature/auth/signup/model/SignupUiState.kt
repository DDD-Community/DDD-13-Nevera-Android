package com.anddd.nevera.feature.auth.signup.model

import com.anddd.nevera.core.common.CountDownTimer
import com.anddd.nevera.core.mvi.NeveraState
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationResult

data class SignupUiState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val authCode: String = "",
    val emailValidation: EmailValidationResult = EmailValidationResult.Empty,
    val passwordValidation: PasswordValidationResult = PasswordValidationResult.Empty,
    val isPasswordMatched: Boolean = false,
    val isEmailRequestSent: Boolean = false,
    val isEmailVerified: Boolean = false,
    val emailRequestCount: Int = 0,
    val authCodeSectionError: AuthCodeSectionError = AuthCodeSectionError.None,
    val authCodeDescription: AuthCodeDescription = AuthCodeDescription.None,
    val timerState: CountDownTimer.State = CountDownTimer.State.Idle,
) : NeveraState {
    val isEmailValid: Boolean
        get() = emailValidation is EmailValidationResult.Valid

    val isPasswordValid: Boolean
        get() = passwordValidation is PasswordValidationResult.Valid

    val canSignup: Boolean
        get() = isEmailVerified && isPasswordValid && isPasswordMatched

    val canResend: Boolean
        get() {
            val isTimerCanResend = when (timerState) {
                is CountDownTimer.State.Active -> timerState.canResend
                CountDownTimer.State.Expired -> true
                CountDownTimer.State.Idle -> false
            }
            return isEmailRequestSent && !isEmailVerified && isTimerCanResend
        }
}

sealed interface AuthCodeDescription {
    /** 표시할 설명 없음 */
    data object None : AuthCodeDescription
    /** 인증 완료 — 성공 안내 문구를 표시한다. */
    data object Verified : AuthCodeDescription
    /** 재인증 요청 실패 — 이미 가입된 이메일 안내를 표시한다. */
    data object EmailAlreadyRegistered : AuthCodeDescription
    /** 인증 확인 실패 — 인증 요청 내역 없음 안내를 표시한다. */
    data object NotFound : AuthCodeDescription
    /** 클라이언트 타이머 또는 서버 인증 시간이 만료되어 00:00을 표시한다. */
    data object Expired : AuthCodeDescription
    /** 인증코드 불일치 — 남은 시간과 오류 문구를 함께 표시한다. */
    data class InvalidCode(val remainingSeconds: Int) : AuthCodeDescription
    /** 정상 타이머 진행 — 남은 시간만 표시한다. */
    data class Timer(val remainingSeconds: Int) : AuthCodeDescription
}

internal fun SignupUiState.withAuthCodeDescription(): SignupUiState {
    val isTimerExpired = timerState is CountDownTimer.State.Expired
    val activeTimer = timerState as? CountDownTimer.State.Active
    val remainingSeconds = activeTimer?.remainingSeconds ?: 0
    val description = when {
        // 이메일 인증 완료?
        isEmailVerified -> AuthCodeDescription.Verified
        // 이미 가입된 이메일?
        authCodeSectionError is AuthCodeSectionError.EmailAlreadyRegistered ->
            AuthCodeDescription.EmailAlreadyRegistered
        // 인증 요청 없음?
        authCodeSectionError is AuthCodeSectionError.NotFound -> AuthCodeDescription.NotFound
        // 코드 불일치 + 타이머 진행 중?
        // 오류 문구 + 남은 시간을 함께 표시 — Active 분기보다 먼저 평가해야 가려지지 않음
        authCodeSectionError is AuthCodeSectionError.InvalidCode && activeTimer != null ->
            AuthCodeDescription.InvalidCode(remainingSeconds)
        // 타이머 만료 OR 서버 만료?
        // 클라이언트 타이머 만료와 서버 측 만료 모두 동일 UI로 처리 — Active보다 먼저 평가해야 가려지지 않음
        isTimerExpired || authCodeSectionError is AuthCodeSectionError.ServerExpired ->
            AuthCodeDescription.Expired
        // 타이머 진행 중?
        timerState is CountDownTimer.State.Active -> AuthCodeDescription.Timer(remainingSeconds)
        else -> AuthCodeDescription.None
    }
    return copy(authCodeDescription = description)
}

sealed interface AuthCodeSectionError {
    /** 에러 없음 */
    data object None : AuthCodeSectionError
    /** verify 2001 — 인증 번호 불일치 */
    data object InvalidCode : AuthCodeSectionError
    /** verify 2002 — 서버 측 인증 시간 만료 */
    data object ServerExpired : AuthCodeSectionError
    /** verify 2005 — 인증 요청 내역 없음 */
    data object NotFound : AuthCodeSectionError
    /** re-auth(재인증) 요청 시 2006 — 이미 가입된 이메일 */
    data object EmailAlreadyRegistered : AuthCodeSectionError
}
