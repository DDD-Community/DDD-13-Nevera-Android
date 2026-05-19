package com.anddd.nevera.feature.login.main.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.component.textfield.NeveraEmailTextField
import com.anddd.nevera.core.designsystem.component.textfield.NeveraPasswordTextField
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldState
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationError
import com.anddd.nevera.domain.model.validation.PasswordValidationResult
import com.anddd.nevera.feature.login.R

@Composable
internal fun LoginInputSection(
    email: String,
    password: String,
    emailValidation: EmailValidationResult = EmailValidationResult.Empty,
    passwordValidation: PasswordValidationResult = PasswordValidationResult.Empty,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
) {
    val canLogin = emailValidation == EmailValidationResult.Valid &&
            passwordValidation is PasswordValidationResult.Valid

    val emailState = when (emailValidation) {
        EmailValidationResult.Valid -> NeveraTextFieldState.Positive
        EmailValidationResult.InvalidFormat -> NeveraTextFieldState.Negative
        else -> NeveraTextFieldState.Normal
    }
    val passwordState = when (passwordValidation) {
        is PasswordValidationResult.Valid -> NeveraTextFieldState.Positive
        is PasswordValidationResult.Invalid -> NeveraTextFieldState.Negative
        else -> NeveraTextFieldState.Normal
    }

    val emailDescription = emailValidation.toErrorDescription()
    val passwordDescription = passwordValidation.toErrorDescription()

    Column {
        NeveraEmailTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth()
                .padding(top = NeveraTheme.spacing.gap20),
            useIcon = true,
            config = NeveraTextFieldConfig(
                heading = stringResource(R.string.login_email_header),
                placeholder = stringResource(R.string.login_email_placeholder),
                state = emailState,
                description = emailDescription,
            ),
        )
        Spacer(Modifier.height(NeveraTheme.spacing.gap16))
        NeveraPasswordTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            useIcon = true,
            config = NeveraTextFieldConfig(
                heading = stringResource(R.string.login_password_header),
                placeholder = stringResource(R.string.login_password_placeholder),
                state = passwordState,
                description = passwordDescription,
            ),
        )
        Spacer(Modifier.height(NeveraTheme.spacing.gap16))
        NeveraFilledButton(
            label = stringResource(R.string.login_submit_button),
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = canLogin,
        )
    }
}

@Composable
private fun EmailValidationResult.toErrorDescription(): String? = when (this) {
    EmailValidationResult.InvalidFormat ->
        stringResource(R.string.login_email_error_invalid_format)
    else -> null
}

@Composable
private fun PasswordValidationResult.toErrorDescription(): String? = when (this) {
    is PasswordValidationResult.Invalid ->
        stringResource(R.string.login_password_error_requirement)
    else -> null
}

@Preview(showBackground = true)
@Composable
private fun LoginInputSectionPreview() {
    NeveraTheme {
        LoginInputSection(
            email = "",
            password = "",
            emailValidation = EmailValidationResult.Empty,
            passwordValidation = PasswordValidationResult.Empty,
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
        )
    }
}

@Preview(showBackground = true, name = "LoginInputSection - 유효성 오류")
@Composable
private fun LoginInputSectionErrorPreview() {
    NeveraTheme {
        LoginInputSection(
            email = "invalid-email",
            password = "short",
            emailValidation = EmailValidationResult.InvalidFormat,
            passwordValidation = PasswordValidationResult.Invalid(
                listOf(PasswordValidationError.TooShort(8))
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
        )
    }
}

@Preview(showBackground = true, name = "LoginInputSection - 로그인 가능")
@Composable
private fun LoginInputSectionEnabledPreview() {
    NeveraTheme {
        LoginInputSection(
            email = "hello@email.com",
            password = "Password1!",
            emailValidation = EmailValidationResult.Valid,
            passwordValidation = PasswordValidationResult.Valid,
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
        )
    }
}
