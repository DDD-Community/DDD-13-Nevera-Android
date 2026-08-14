package com.anddd.nevera.feature.auth.signup.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.textfield.NeveraPasswordTextField
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldState
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.domain.model.validation.PasswordValidationError
import com.anddd.nevera.domain.model.validation.PasswordValidationResult
import com.anddd.nevera.feature.auth.R

@Composable
internal fun PasswordSection(
    password: String,
    passwordValidation: PasswordValidationResult?,
    isPasswordValid: Boolean,
    enabled: Boolean,
    onPasswordChange: (String) -> Unit,
) {
    val passwordFieldState = when {
        password.isBlank() || passwordValidation == null -> NeveraTextFieldState.Normal
        isPasswordValid -> NeveraTextFieldState.Positive
        else -> NeveraTextFieldState.Negative
    }
    val passwordDescription = if (isPasswordValid) stringResource(R.string.signup_password_valid)
    else stringResource(R.string.signup_password_hint)

    Column {
        SectionLabel(stringResource(R.string.signup_label_password))
        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap8))
        NeveraPasswordTextField(
            value = password,
            onValueChange = onPasswordChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            config = NeveraTextFieldConfig(
                placeholder = stringResource(R.string.signup_placeholder_password),
                state = passwordFieldState,
            ),
        )
        FieldDescription(text = passwordDescription, state = passwordFieldState)
    }
}

@Preview(showBackground = true, name = "PasswordSection - 초기")
@Composable
private fun PasswordSectionInitialPreview() {
    NeveraTheme {
        PasswordSection(
            password = "",
            passwordValidation = null,
            isPasswordValid = false,
            enabled = true,
            onPasswordChange = {},
        )
    }
}

@Preview(showBackground = true, name = "PasswordSection - 유효")
@Composable
private fun PasswordSectionValidPreview() {
    NeveraTheme {
        PasswordSection(
            password = "Password1!",
            passwordValidation = PasswordValidationResult.Valid,
            isPasswordValid = true,
            enabled = true,
            onPasswordChange = {},
        )
    }
}

@Preview(showBackground = true, name = "PasswordSection - 유효하지 않음")
@Composable
private fun PasswordSectionInvalidPreview() {
    NeveraTheme {
        PasswordSection(
            password = "1234",
            passwordValidation = PasswordValidationResult.Invalid(
                listOf(PasswordValidationError.TooShort(minLength = 8))
            ),
            isPasswordValid = false,
            enabled = true,
            onPasswordChange = {},
        )
    }
}
