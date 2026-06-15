package com.anddd.nevera.feature.auth.signup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBar
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarNavigation
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.common.CountDownTimer
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationResult
import com.anddd.nevera.feature.auth.R
import com.anddd.nevera.feature.auth.signup.model.AuthCodeDescription
import com.anddd.nevera.feature.auth.signup.model.AuthCodeSectionError
import com.anddd.nevera.feature.auth.signup.model.SignupIntent
import com.anddd.nevera.feature.auth.signup.model.SignupUiState

@Composable
internal fun SignupContent(
    uiState: SignupUiState,
    onIntent: (SignupIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            NeveraAppBar(
                title = stringResource(R.string.signup_title),
                navigation = NeveraAppBarNavigation.Back(onClick = { onIntent(SignupIntent.NavigateBack) }),
            )
        }
    ) { innerPadding ->

        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier.fillMaxSize()
                .background(NeveraTheme.colors.backgroundPrimary)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = NeveraTheme.spacing.padding20)
                    .imePadding()
            ) {
                Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap20))

                EmailSection(
                    email = uiState.email,
                    emailValidation = uiState.emailValidation,
                    isEmailRequestSent = uiState.isEmailRequestSent,
                    isEmailVerified = uiState.isEmailVerified,
                    isEmailValid = uiState.isEmailValid,
                    canResend = uiState.canResend,
                    onEmailChange = { onIntent(SignupIntent.EmailChanged(it)) },
                    onRequestEmailVerification = { onIntent(SignupIntent.RequestEmailVerification) },
                )

                if (uiState.isEmailRequestSent) {
                    Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap24))
                    AuthCodeSection(
                        authCode = uiState.authCode,
                        authCodeSectionError = uiState.authCodeSectionError,
                        authCodeDescription = uiState.authCodeDescription,
                        timerState = uiState.timerState,
                        onAuthCodeChange = { onIntent(SignupIntent.AuthCodeChanged(it)) },
                        onVerifyAuthCode = { onIntent(SignupIntent.VerifyAuthCode) },
                    )
                }

                Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap24))

                PasswordSection(
                    password = uiState.password,
                    passwordValidation = uiState.passwordValidation,
                    isPasswordValid = uiState.isPasswordValid,
                    enabled = uiState.isEmailVerified,
                    onPasswordChange = { onIntent(SignupIntent.PasswordChanged(it)) },
                )

                Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap24))

                ConfirmPasswordSection(
                    confirmPassword = uiState.confirmPassword,
                    isPasswordMatched = uiState.isPasswordMatched,
                    enabled = uiState.isEmailVerified && uiState.isPasswordValid,
                    onConfirmPasswordChange = { onIntent(SignupIntent.ConfirmPasswordChanged(it)) },
                )

                Spacer(modifier = Modifier.height(80.dp))
            }

            NeveraFilledButton(
                label = stringResource(R.string.signup_btn_signup),
                onClick = { onIntent(SignupIntent.Signup) },
                enabled = uiState.canSignup,
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
            uiState = SignupUiState(),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "SignupContent - 에러")
@Composable
private fun SignupContentErrorPreview() {
    NeveraTheme {
        SignupContent(
            uiState = SignupUiState(
                email = "invalid-email",
                password = "1234",
                confirmPassword = "5678",
                authCode = "000000",
                emailValidation = EmailValidationResult.InvalidFormat,
                passwordValidation = PasswordValidationResult.Invalid(emptyList()),
                isPasswordMatched = false,
                isEmailRequestSent = true,
                isEmailVerified = false,
                authCodeSectionError = AuthCodeSectionError.InvalidCode,
                authCodeDescription = AuthCodeDescription.InvalidCode(remainingSeconds = 90),
                timerState = CountDownTimer.State.Active(remainingSeconds = 90, canResend = false),
            ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "SignupContent - 인증코드 입력")
@Composable
private fun SignupContentAuthCodePreview() {
    NeveraTheme {
        SignupContent(
            uiState = SignupUiState(
                email = "test@example.com",
                emailValidation = EmailValidationResult.Valid,
                isEmailRequestSent = true,
                authCodeDescription = AuthCodeDescription.Timer(remainingSeconds = 150),
                timerState = CountDownTimer.State.Active(remainingSeconds = 150, canResend = false),
            ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "SignupContent - 입력완료")
@Composable
private fun SignupContentCompletePreview() {
    NeveraTheme {
        SignupContent(
            uiState = SignupUiState(
                email = "test@example.com",
                password = "Password1!",
                confirmPassword = "Password1!",
                authCode = "123456",
                emailValidation = EmailValidationResult.Valid,
                passwordValidation = PasswordValidationResult.Valid,
                isPasswordMatched = true,
                isEmailRequestSent = true,
                isEmailVerified = true,
                authCodeDescription = AuthCodeDescription.Verified,
            ),
            onIntent = {},
        )
    }
}
