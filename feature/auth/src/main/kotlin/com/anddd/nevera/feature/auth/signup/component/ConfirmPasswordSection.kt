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
import com.anddd.nevera.feature.auth.R

@Composable
internal fun ConfirmPasswordSection(
    confirmPassword: String,
    isPasswordMatched: Boolean,
    enabled: Boolean,
    onConfirmPasswordChange: (String) -> Unit,
) {
    val confirmFieldState = when {
        confirmPassword.isBlank() -> NeveraTextFieldState.Normal
        isPasswordMatched -> NeveraTextFieldState.Positive
        else -> NeveraTextFieldState.Negative
    }
    val confirmDescription: String = when (confirmFieldState) {
        NeveraTextFieldState.Positive -> stringResource(R.string.signup_confirm_password_match)
        NeveraTextFieldState.Negative -> stringResource(R.string.signup_confirm_password_mismatch)
        NeveraTextFieldState.Normal -> ""
    }

    Column {
        SectionLabel(stringResource(R.string.signup_label_confirm_password))
        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap8))
        NeveraPasswordTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            config = NeveraTextFieldConfig(
                placeholder = stringResource(R.string.signup_placeholder_confirm_password),
                state = confirmFieldState,
            ),
        )
        if (confirmDescription.isNotEmpty()) {
            FieldDescription(text = confirmDescription, state = confirmFieldState)
        }
    }
}

@Preview(showBackground = true, name = "ConfirmPasswordSection - 초기")
@Composable
private fun ConfirmPasswordSectionInitialPreview() {
    NeveraTheme {
        ConfirmPasswordSection(
            confirmPassword = "",
            isPasswordMatched = false,
            enabled = true,
            onConfirmPasswordChange = {},
        )
    }
}

@Preview(showBackground = true, name = "ConfirmPasswordSection - 일치")
@Composable
private fun ConfirmPasswordSectionMatchPreview() {
    NeveraTheme {
        ConfirmPasswordSection(
            confirmPassword = "Password1!",
            isPasswordMatched = true,
            enabled = true,
            onConfirmPasswordChange = {},
        )
    }
}

@Preview(showBackground = true, name = "ConfirmPasswordSection - 불일치")
@Composable
private fun ConfirmPasswordSectionMismatchPreview() {
    NeveraTheme {
        ConfirmPasswordSection(
            confirmPassword = "Password2!",
            isPasswordMatched = false,
            enabled = true,
            onConfirmPasswordChange = {},
        )
    }
}
