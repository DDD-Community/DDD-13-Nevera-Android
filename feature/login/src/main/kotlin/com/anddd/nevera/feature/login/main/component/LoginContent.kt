package com.anddd.nevera.feature.login.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.designsystem.ui.theme.spacing.NeveraSpacing
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationError
import com.anddd.nevera.domain.model.validation.PasswordValidationResult

@Composable
internal fun LoginContent(
    email: String,
    password: String,
    emailValidation: EmailValidationResult?,
    passwordValidation: PasswordValidationResult?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(NeveraTheme.colors.backgroundPrimary)
            .statusBarsPadding()
            .padding(horizontal = NeveraSpacing.padding24),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(NeveraSpacing.gap20))
        LoginHeader()
        Spacer(Modifier.height(NeveraSpacing.gap16))
        LoginInputSection(
            email = email,
            password = password,
            emailValidation = emailValidation,
            passwordValidation = passwordValidation,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onLoginClick = onLoginClick,
        )
        LoginEtcSection(
            onGoogleLoginClick = onGoogleLoginClick,
            onSignupClick = onSignupClick,
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
            emailValidation = null,
            passwordValidation = null,
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onSignupClick = {},
            onGoogleLoginClick = {},
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
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onSignupClick = {},
            onGoogleLoginClick = {},
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
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onSignupClick = {},
            onGoogleLoginClick = {},
        )
    }
}
