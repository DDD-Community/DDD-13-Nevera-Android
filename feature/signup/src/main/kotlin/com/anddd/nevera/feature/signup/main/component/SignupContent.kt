package com.anddd.nevera.feature.signup.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.domain.usecase.validator.EmailValidationResult
import com.anddd.nevera.domain.usecase.validator.PasswordValidationError
import com.anddd.nevera.domain.usecase.validator.PasswordValidationResult
import com.anddd.nevera.feature.signup.main.model.SignupStatus

@Composable
internal fun SignupContent(
    name: String,
    email: String,
    password: String,
    authCode: String,
    emailValidation: EmailValidationResult?,
    passwordValidation: PasswordValidationResult?,
    isEmailRequestSent: Boolean,
    isEmailVerified: Boolean,
    status: SignupStatus,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onAuthCodeChange: (String) -> Unit,
    onRequestEmailVerification: () -> Unit,
    onVerifyAuthCode: () -> Unit,
    onSignupClick: () -> Unit
) {
    val isLoading = status is SignupStatus.Loading
    val emailError = emailValidation.toErrorMessage()
    val passwordErrors = passwordValidation.toErrorMessages()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "회원가입", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("이름") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("이메일") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = !isLoading,
                isError = emailError != null,
                supportingText = if (emailError != null) {
                    { ValidationErrorText(emailError) }
                } else null
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = onRequestEmailVerification,
                enabled = !isLoading,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("인증 요청")
            }
        }

        if (isEmailRequestSent) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = authCode,
                    onValueChange = onAuthCodeChange,
                    label = { Text("인증 코드") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = !isLoading && !isEmailVerified
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onVerifyAuthCode,
                    enabled = !isLoading && !isEmailVerified
                ) {
                    Text(if (isEmailVerified) "인증 완료" else "인증 확인")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("비밀번호") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            enabled = !isLoading,
            isError = passwordErrors.isNotEmpty(),
            supportingText = if (passwordErrors.isNotEmpty()) {
                { ValidationErrorText(passwordErrors.first()) }
            } else null
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSignupClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = isEmailVerified && !isLoading && name.isNotBlank() && passwordErrors.isEmpty()
        ) {
            Text("회원가입")
        }
    }
}

@Composable
private fun ValidationErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall
    )
}

private fun EmailValidationResult?.toErrorMessage(): String? = when (this) {
    EmailValidationResult.Empty -> "이메일을 입력해주세요"
    EmailValidationResult.InvalidFormat -> "올바른 이메일 형식을 입력해주세요"
    else -> null
}

private fun PasswordValidationResult?.toErrorMessages(): List<String> = when (this) {
    is PasswordValidationResult.Invalid -> errors.map { it.toMessage() }
    else -> emptyList()
}

private fun PasswordValidationError.toMessage(): String = when (this) {
    is PasswordValidationError.TooShort -> "최소 ${minLength}자 이상이어야 합니다"
    PasswordValidationError.MissingUppercase -> "대문자를 포함해야 합니다"
    PasswordValidationError.MissingLowercase -> "소문자를 포함해야 합니다"
    PasswordValidationError.MissingDigit -> "숫자를 포함해야 합니다"
    PasswordValidationError.MissingSpecialChar -> "특수문자를 포함해야 합니다"
}

@Preview(showBackground = true)
@Composable
private fun SignupContentPreview() {
    NeveraTheme {
        SignupContent(
            name = "",
            email = "",
            password = "",
            authCode = "",
            emailValidation = null,
            passwordValidation = null,
            isEmailRequestSent = false,
            isEmailVerified = false,
            status = SignupStatus.Idle,
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onAuthCodeChange = {},
            onRequestEmailVerification = {},
            onVerifyAuthCode = {},
            onSignupClick = {}
        )
    }
}

@Preview(showBackground = true, name = "SignupContent - 인증 코드 입력")
@Composable
private fun SignupContentAuthCodePreview() {
    NeveraTheme {
        SignupContent(
            name = "홍길동",
            email = "test@example.com",
            password = "Password1!",
            authCode = "",
            emailValidation = EmailValidationResult.Valid,
            passwordValidation = PasswordValidationResult.Valid,
            isEmailRequestSent = true,
            isEmailVerified = false,
            status = SignupStatus.Idle,
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onAuthCodeChange = {},
            onRequestEmailVerification = {},
            onVerifyAuthCode = {},
            onSignupClick = {}
        )
    }
}
