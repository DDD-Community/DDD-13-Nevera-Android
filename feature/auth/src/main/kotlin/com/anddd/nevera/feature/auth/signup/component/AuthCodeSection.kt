package com.anddd.nevera.feature.auth.signup.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.common.CountDownTimer
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonSize
import com.anddd.nevera.core.designsystem.component.button.NeveraOutlinedButton
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextField
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldConfig
import com.anddd.nevera.core.designsystem.component.textfield.NeveraTextFieldState
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.designsystem.ui.theme.shape.NeveraRadius
import com.anddd.nevera.feature.auth.R
import com.anddd.nevera.feature.auth.signup.model.AuthCodeDescription
import com.anddd.nevera.feature.auth.signup.model.AuthCodeSectionError

@Composable
internal fun AuthCodeSection(
    authCode: String,
    authCodeSectionError: AuthCodeSectionError,
    authCodeDescription: AuthCodeDescription,
    timerState: CountDownTimer.State,
    onAuthCodeChange: (String) -> Unit,
    onVerifyAuthCode: () -> Unit,
) {
    val isFieldError = authCodeSectionError is AuthCodeSectionError.InvalidCode
    val isServerExpired = authCodeSectionError is AuthCodeSectionError.ServerExpired
    val isNotFound = authCodeSectionError is AuthCodeSectionError.NotFound
    val isEmailAlreadyRegistered = authCodeSectionError is AuthCodeSectionError.EmailAlreadyRegistered

    val isTimerExpired = timerState is CountDownTimer.State.Expired

    val authCodeFieldState = when {
        authCodeDescription is AuthCodeDescription.Verified -> NeveraTextFieldState.Positive
        isFieldError || isNotFound || isEmailAlreadyRegistered ||
            isTimerExpired || isServerExpired -> NeveraTextFieldState.Negative
        else -> NeveraTextFieldState.Normal
    }
    val authCodeDescriptionText = authCodeDescription.asText()

    Column {
        SectionLabel(stringResource(R.string.signup_label_auth_code))
        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap8))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NeveraTextField(
                value = authCode,
                onValueChange = onAuthCodeChange,
                enabled = authCodeDescription !is AuthCodeDescription.Verified,
                modifier = Modifier.weight(1f),
                config = NeveraTextFieldConfig(
                    placeholder = stringResource(R.string.signup_placeholder_auth_code),
                    state = authCodeFieldState,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                ),
            )
            Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap8))
            NeveraOutlinedButton(
                label = stringResource(R.string.signup_btn_verify_auth),
                onClick = onVerifyAuthCode,
                enabled = authCodeDescription !is AuthCodeDescription.Verified && !isTimerExpired,
                size = NeveraButtonSize.Small,
                shape = RoundedCornerShape(NeveraRadius.max),
            )
        }
        if (authCodeDescription != AuthCodeDescription.None) {
            FieldDescription(text = authCodeDescriptionText, state = authCodeFieldState)
        }
    }
}

@Composable
private fun AuthCodeDescription.asText(): String = when (this) {
    AuthCodeDescription.None -> ""
    AuthCodeDescription.Verified -> stringResource(R.string.signup_auth_verified)
    AuthCodeDescription.EmailAlreadyRegistered ->
        stringResource(R.string.signup_auth_email_already_registered)
    AuthCodeDescription.NotFound -> stringResource(R.string.signup_auth_not_found)
    AuthCodeDescription.Expired -> "00:00"
    is AuthCodeDescription.InvalidCode ->
        "${remainingSeconds.toTimerText()}  ${stringResource(R.string.signup_auth_code_error)}"
    is AuthCodeDescription.Timer -> remainingSeconds.toTimerText()
}

private fun Int.toTimerText(): String = "%02d:%02d".format(this / 60, this % 60)

@Preview(showBackground = true, name = "AuthCodeSection - 타이머 진행")
@Composable
private fun AuthCodeSectionActivePreview() {
    NeveraTheme {
        AuthCodeSection(
            authCode = "",
            authCodeSectionError = AuthCodeSectionError.None,
            authCodeDescription = AuthCodeDescription.Timer(remainingSeconds = 150),
            timerState = CountDownTimer.State.Active(remainingSeconds = 150, canResend = false),
            onAuthCodeChange = {},
            onVerifyAuthCode = {},
        )
    }
}

@Preview(showBackground = true, name = "AuthCodeSection - 타이머 만료")
@Composable
private fun AuthCodeSectionExpiredPreview() {
    NeveraTheme {
        AuthCodeSection(
            authCode = "",
            authCodeSectionError = AuthCodeSectionError.None,
            authCodeDescription = AuthCodeDescription.Expired,
            timerState = CountDownTimer.State.Expired,
            onAuthCodeChange = {},
            onVerifyAuthCode = {},
        )
    }
}

@Preview(showBackground = true, name = "AuthCodeSection - 인증 완료")
@Composable
private fun AuthCodeSectionVerifiedPreview() {
    NeveraTheme {
        AuthCodeSection(
            authCode = "123456",
            authCodeSectionError = AuthCodeSectionError.None,
            authCodeDescription = AuthCodeDescription.Verified,
            timerState = CountDownTimer.State.Idle,
            onAuthCodeChange = {},
            onVerifyAuthCode = {},
        )
    }
}

@Preview(showBackground = true, name = "AuthCodeSection - 코드 오류")
@Composable
private fun AuthCodeSectionErrorPreview() {
    NeveraTheme {
        AuthCodeSection(
            authCode = "000000",
            authCodeSectionError = AuthCodeSectionError.InvalidCode,
            authCodeDescription = AuthCodeDescription.InvalidCode(remainingSeconds = 90),
            timerState = CountDownTimer.State.Active(remainingSeconds = 90, canResend = true),
            onAuthCodeChange = {},
            onVerifyAuthCode = {},
        )
    }
}
