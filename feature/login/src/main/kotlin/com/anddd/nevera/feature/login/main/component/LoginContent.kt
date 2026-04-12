package com.anddd.nevera.feature.login.main.component

import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
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
    onGoogleLoginClick: () -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val emailError = emailValidation.toErrorMessage()
    val passwordErrors = passwordValidation.toErrorMessages()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "로그인", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("이메일") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError != null,
            supportingText = if (emailError != null) {
                { ValidationErrorText(emailError) }
            } else null
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("비밀번호") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            isError = passwordErrors.isNotEmpty(),
            trailingIcon = {
                PasswordVisibilityToggle(
                    visible = isPasswordVisible,
                    onToggle = { isPasswordVisible = !isPasswordVisible }
                )
            },
            supportingText = if (passwordErrors.isNotEmpty()) {
                { ValidationErrorText(passwordErrors.first()) }
            } else null
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onSignupClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("이메일 회원가입")
        }
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("이메일 로그인")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "  SNS 로그인  ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onGoogleLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Google로 계속하기")
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

@Composable
private fun PasswordVisibilityToggle(
    visible: Boolean,
    onToggle: () -> Unit
) {
    TextButton(
        onClick = onToggle,
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Text(
            text = if (visible) "숨김" else "보기",
            style = MaterialTheme.typography.labelMedium
        )
    }
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
            onGoogleLoginClick = {}
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
            onGoogleLoginClick = {}
        )
    }
}
