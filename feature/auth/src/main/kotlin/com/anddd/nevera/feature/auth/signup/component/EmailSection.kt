package com.anddd.nevera.feature.auth.signup.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonSize
import com.anddd.nevera.core.designsystem.component.button.NeveraOutlinedButton
import com.anddd.nevera.core.designsystem.component.textfield.NeveraEmailTextField
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldState
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.designsystem.ui.theme.shape.NeveraRadius
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.feature.auth.R

@Composable
internal fun EmailSection(
    email: String,
    emailValidation: EmailValidationResult = EmailValidationResult.Empty,
    isEmailRequestSent: Boolean,
    isEmailVerified: Boolean,
    isEmailValid: Boolean,
    canResend: Boolean,
    onEmailChange: (String) -> Unit,
    onRequestEmailVerification: () -> Unit,
) {
    val emailFieldState = when {
        isEmailRequestSent -> NeveraTextFieldState.Positive
        emailValidation is EmailValidationResult.InvalidFormat -> NeveraTextFieldState.Negative
        else -> NeveraTextFieldState.Normal
    }
    val emailDescription: String? = when {
        isEmailRequestSent -> stringResource(R.string.signup_email_sent)
        emailValidation is EmailValidationResult.InvalidFormat -> stringResource(R.string.signup_email_invalid)
        else -> null
    }
    val isButtonEnabled = when {
        isEmailVerified -> false
        isEmailRequestSent -> canResend
        else -> isEmailValid
    }

    Column {
        SectionLabel(stringResource(R.string.signup_label_email))
        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap8))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NeveraEmailTextField(
                value = email,
                onValueChange = onEmailChange,
                enabled = !isEmailRequestSent,
                modifier = Modifier.weight(1f),
                config = NeveraTextFieldConfig(
                    placeholder = stringResource(R.string.signup_placeholder_email),
                    state = emailFieldState,
                ),
            )
            Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap12))
            NeveraOutlinedButton(
                label = if (isEmailRequestSent) stringResource(R.string.signup_btn_resend_auth)
                else stringResource(R.string.signup_btn_request_auth),
                onClick = onRequestEmailVerification,
                enabled = isButtonEnabled,
                size = NeveraButtonSize.Small,
                shape = RoundedCornerShape(NeveraRadius.max),
            )
        }
        if (emailDescription != null) {
            FieldDescription(text = emailDescription, state = emailFieldState)
        }
    }
}

@Preview(showBackground = true, name = "EmailSection - 초기")
@Composable
private fun EmailSectionInitialPreview() {
    NeveraTheme {
        EmailSection(
            email = "",
            emailValidation = EmailValidationResult.Empty,
            isEmailRequestSent = false,
            isEmailVerified = false,
            isEmailValid = false,
            canResend = false,
            onEmailChange = {},
            onRequestEmailVerification = {},
        )
    }
}

@Preview(showBackground = true, name = "EmailSection - 이메일 형식 오류")
@Composable
private fun EmailSectionInvalidEmailPreview() {
    NeveraTheme {
        EmailSection(
            email = "invalid-email",
            emailValidation = EmailValidationResult.InvalidFormat,
            isEmailRequestSent = false,
            isEmailVerified = false,
            isEmailValid = false,
            canResend = false,
            onEmailChange = {},
            onRequestEmailVerification = {},
        )
    }
}

@Preview(showBackground = true, name = "EmailSection - 발송완료")
@Composable
private fun EmailSectionSentPreview() {
    NeveraTheme {
        EmailSection(
            email = "test@example.com",
            emailValidation = EmailValidationResult.Valid,
            isEmailRequestSent = true,
            isEmailVerified = false,
            isEmailValid = true,
            canResend = false,
            onEmailChange = {},
            onRequestEmailVerification = {},
        )
    }
}

@Preview(showBackground = true, name = "EmailSection - 재발송 가능")
@Composable
private fun EmailSectionCanResendPreview() {
    NeveraTheme {
        EmailSection(
            email = "test@example.com",
            emailValidation = EmailValidationResult.Valid,
            isEmailRequestSent = true,
            isEmailVerified = false,
            isEmailValid = true,
            canResend = true,
            onEmailChange = {},
            onRequestEmailVerification = {},
        )
    }
}
