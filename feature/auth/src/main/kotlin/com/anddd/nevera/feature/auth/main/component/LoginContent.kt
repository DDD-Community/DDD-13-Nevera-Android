package com.anddd.nevera.feature.auth.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationError
import com.anddd.nevera.domain.model.validation.PasswordValidationResult
import com.anddd.nevera.feature.auth.main.model.LoginIntent

@Composable
internal fun LoginContent(
    email: String,
    password: String,
    emailValidation: EmailValidationResult = EmailValidationResult.Empty,
    passwordValidation: PasswordValidationResult = PasswordValidationResult.Empty,
    onIntent: (LoginIntent) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize()
            .background(NeveraTheme.colors.backgroundPrimary)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(scrollState)
            .padding(horizontal = NeveraTheme.spacing.padding24),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(NeveraTheme.spacing.gap20))
        LoginHeader()
        Spacer(Modifier.height(NeveraTheme.spacing.gap16))
        LoginInputSection(
            email = email,
            password = password,
            emailValidation = emailValidation,
            passwordValidation = passwordValidation,
            onEmailChange = { onIntent(LoginIntent.EmailChanged(it)) },
            onPasswordChange = { onIntent(LoginIntent.PasswordChanged(it)) },
            onLoginClick = { onIntent(LoginIntent.LoginWithEmailClicked) },
        )
        LoginEtcSection(
            onGoogleLoginClick = { onIntent(LoginIntent.GoogleLoginButtonClicked) },
            onSignupClick = { onIntent(LoginIntent.SignupClicked) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentPreview() {
    NeveraTheme {
        LoginContent(
            email = "",
            password = "",
            emailValidation = EmailValidationResult.Empty,
            passwordValidation = PasswordValidationResult.Empty,
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "LoginContent - 유효성 오류")
@Composable
private fun LoginContentErrorPreview() {
    NeveraTheme {
        LoginContent(
            email = "invalid-email",
            password = "short",
            emailValidation = EmailValidationResult.InvalidFormat,
            passwordValidation = PasswordValidationResult.Invalid(
                listOf(PasswordValidationError.TooShort(8))
            ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, name = "LoginContent - 로그인 가능")
@Composable
private fun LoginContentEnabledPreview() {
    NeveraTheme {
        LoginContent(
            email = "hello@email.com",
            password = "Password1!",
            emailValidation = EmailValidationResult.Valid,
            passwordValidation = PasswordValidationResult.Valid,
            onIntent = {},
        )
    }
}
