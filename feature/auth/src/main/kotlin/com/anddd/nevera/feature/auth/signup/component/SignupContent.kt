package com.anddd.nevera.feature.auth.signup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.common.CountDownTimer
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBar
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarNavigation
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationError
import com.anddd.nevera.domain.model.validation.PasswordValidationResult
import com.anddd.nevera.feature.auth.R
import com.anddd.nevera.feature.auth.signup.model.AuthCodeDescription
import com.anddd.nevera.feature.auth.signup.model.AuthCodeSectionError
import com.anddd.nevera.feature.auth.signup.model.SignupIntent

@Composable
internal fun SignupContent(
    email: String,
    password: String,
    confirmPassword: String,
    authCode: String,
    emailValidation: EmailValidationResult = EmailValidationResult.Empty,
    passwordValidation: PasswordValidationResult = PasswordValidationResult.Empty,
    isPasswordMatched: Boolean,
    isEmailRequestSent: Boolean,
    isEmailVerified: Boolean,
    authCodeSectionError: AuthCodeSectionError = AuthCodeSectionError.None,
    authCodeDescription: AuthCodeDescription = AuthCodeDescription.None,
    timerState: CountDownTimer.State,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onIntent: (SignupIntent) -> Unit,
) {
    val isEmailValid = emailValidation == EmailValidationResult.Valid
    val isPasswordValid = passwordValidation is PasswordValidationResult.Valid
    val canSignup = isEmailVerified &&
        !isLoading &&
        isPasswordValid &&
        isPasswordMatched &&
        confirmPassword.isNotBlank()

    Scaffold(
        topBar = {
            NeveraAppBar(
                title = stringResource(R.string.signup_title),
                navigation = NeveraAppBarNavigation.Back(onClick = onNavigateBack),
            )
        }
    ) { innerPadding ->

        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NeveraTheme.colors.backgroundPrimary)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = NeveraTheme.spacing.padding20)
            ) {
                Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap20))

                val isTimerCanResend = when (timerState) {
                    is CountDownTimer.State.Active -> timerState.canResend
                    CountDownTimer.State.Expired -> true
                    CountDownTimer.State.Idle -> false
                }
                val canResend = isEmailRequestSent && !isEmailVerified && isTimerCanResend

                EmailSection(
                    email = email,
                    emailValidation = emailValidation,
                    isEmailRequestSent = isEmailRequestSent,
                    isEmailVerified = isEmailVerified,
                    isEmailValid = isEmailValid,
                    canResend = canResend,
                    isLoading = isLoading,
                    onEmailChange = { onIntent(SignupIntent.EmailChanged(it)) },
                    onRequestEmailVerification = { onIntent(SignupIntent.RequestEmailVerification) },
                )

                if (isEmailRequestSent) {
                    Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap24))
                    AuthCodeSection(
                        authCode = authCode,
                        isEmailVerified = isEmailVerified,
                        authCodeSectionError = authCodeSectionError,
                        authCodeDescription = authCodeDescription,
                        timerState = timerState,
                        isLoading = isLoading,
                        onAuthCodeChange = { onIntent(SignupIntent.AuthCodeChanged(it)) },
                        onVerifyAuthCode = { onIntent(SignupIntent.VerifyAuthCode) },
                    )
                }

                Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap24))

                PasswordSection(
                    password = password,
                    passwordValidation = passwordValidation,
                    isPasswordValid = isPasswordValid,
                    enabled = isEmailVerified,
                    onPasswordChange = { onIntent(SignupIntent.PasswordChanged(it)) },
                )

                Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap24))

                ConfirmPasswordSection(
                    confirmPassword = confirmPassword,
                    isPasswordMatched = isPasswordMatched,
                    enabled = isEmailVerified && isPasswordValid,
                    onConfirmPasswordChange = { onIntent(SignupIntent.ConfirmPasswordChanged(it)) },
                )

                Spacer(modifier = Modifier.height(80.dp))
            }

            NeveraFilledButton(
                label = stringResource(R.string.signup_btn_signup),
                onClick = { onIntent(SignupIntent.Signup) },
                enabled = canSignup,
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(NeveraTheme.spacing.gap16),
            )
        }
    }
}

@Preview(showBackground = true, name = "SignupContent - Default")
@Composable
private fun SignupContentPreview() {
    NeveraTheme {
        SignupContent(
            email = "",
            password = "",
            confirmPassword = "",
            authCode = "",
            emailValidation = EmailValidationResult.Empty,
            passwordValidation = PasswordValidationResult.Empty,
            isPasswordMatched = false,
            isEmailRequestSent = false,
            isEmailVerified = false,
            authCodeSectionError = AuthCodeSectionError.None,
            authCodeDescription = AuthCodeDescription.None,
            timerState = CountDownTimer.State.Idle,
            isLoading = false,
            onNavigateBack = {},
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "SignupContent - 에러")
@Composable
private fun SignupContentErrorPreview() {
    NeveraTheme {
        SignupContent(
            email = "invalid-email",
            password = "1234",
            confirmPassword = "5678",
            authCode = "000000",
            emailValidation = EmailValidationResult.InvalidFormat,
            passwordValidation = PasswordValidationResult.Invalid(
                listOf(PasswordValidationError.TooShort(minLength = 8))
            ),
            isPasswordMatched = false,
            isEmailRequestSent = true,
            isEmailVerified = false,
            authCodeSectionError = AuthCodeSectionError.InvalidCode,
            authCodeDescription = AuthCodeDescription.InvalidCode(remainingSeconds = 90),
            timerState = CountDownTimer.State.Active(remainingSeconds = 90, canResend = false),
            isLoading = false,
            onNavigateBack = {},
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "SignupContent - 인증코드 입력")
@Composable
private fun SignupContentAuthCodePreview() {
    NeveraTheme {
        SignupContent(
            email = "test@example.com",
            password = "",
            confirmPassword = "",
            authCode = "",
            emailValidation = EmailValidationResult.Valid,
            passwordValidation = PasswordValidationResult.Empty,
            isPasswordMatched = false,
            isEmailRequestSent = true,
            isEmailVerified = false,
            authCodeSectionError = AuthCodeSectionError.None,
            authCodeDescription = AuthCodeDescription.Timer(remainingSeconds = 150),
            timerState = CountDownTimer.State.Active(remainingSeconds = 150, canResend = false),
            isLoading = false,
            onNavigateBack = {},
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "SignupContent - 입력완료")
@Composable
private fun SignupContentCompletePreview() {
    NeveraTheme {
        SignupContent(
            email = "test@example.com",
            password = "Password1!",
            confirmPassword = "Password1!",
            authCode = "123456",
            emailValidation = EmailValidationResult.Valid,
            passwordValidation = PasswordValidationResult.Valid,
            isPasswordMatched = true,
            isEmailRequestSent = true,
            isEmailVerified = true,
            authCodeSectionError = AuthCodeSectionError.None,
            authCodeDescription = AuthCodeDescription.Verified,
            timerState = CountDownTimer.State.Idle,
            isLoading = false,
            onNavigateBack = {},
            onIntent = {},
        )
    }
}
