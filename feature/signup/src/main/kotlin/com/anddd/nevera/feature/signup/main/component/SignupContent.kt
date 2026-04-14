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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.domain.model.validation.EmailValidationResult
import com.anddd.nevera.domain.model.validation.PasswordValidationError
import com.anddd.nevera.domain.model.validation.PasswordValidationResult
import com.anddd.nevera.feature.signup.main.model.SignupStatus

@Composable
internal fun SignupContent(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    authCode: String,
    emailValidation: EmailValidationResult?,
    passwordValidation: PasswordValidationResult?,
    isPasswordMatched: Boolean,
    isEmailRequestSent: Boolean,
    isEmailVerified: Boolean,
    status: SignupStatus,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onAuthCodeChange: (String) -> Unit,
    onRequestEmailVerification: () -> Unit,
    onVerifyAuthCode: () -> Unit,
    onSignupClick: () -> Unit
) {
    val flags = rememberSignupUiFlags(
        name = name,
        authCode = authCode,
        confirmPassword = confirmPassword,
        emailValidation = emailValidation,
        passwordValidation = passwordValidation,
        isPasswordMatched = isPasswordMatched,
        isEmailVerified = isEmailVerified,
        status = status
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SignupHeader()
        Spacer(modifier = Modifier.height(32.dp))

        NameSection(
            name = name,
            isLoading = flags.isLoading,
            onNameChange = onNameChange
        )
        Spacer(modifier = Modifier.height(16.dp))

        EmailSection(
            email = email,
            emailError = flags.emailError,
            canRequestEmailVerification = flags.canRequestEmailVerification,
            isLoading = flags.isLoading,
            onEmailChange = onEmailChange,
            onRequestEmailVerification = onRequestEmailVerification
        )

        if (isEmailRequestSent) {
            Spacer(modifier = Modifier.height(8.dp))
            AuthCodeSection(
                authCode = authCode,
                isLoading = flags.isLoading,
                isEmailVerified = isEmailVerified,
                canVerifyAuthCode = flags.canVerifyAuthCode,
                onAuthCodeChange = onAuthCodeChange,
                onVerifyAuthCode = onVerifyAuthCode
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PasswordSection(
            password = password,
            passwordErrors = flags.passwordErrors,
            isLoading = flags.isLoading,
            onPasswordChange = onPasswordChange
        )
        Spacer(modifier = Modifier.height(16.dp))

        ConfirmPasswordSection(
            confirmPassword = confirmPassword,
            confirmPasswordError = flags.confirmPasswordError,
            isLoading = flags.isLoading,
            onConfirmPasswordChange = onConfirmPasswordChange
        )
        Spacer(modifier = Modifier.height(24.dp))

        SignupSubmitSection(
            canSignup = flags.canSignup,
            onSignupClick = onSignupClick
        )
    }
}

@Composable
private fun SignupHeader() {
    Text(text = "회원가입", style = MaterialTheme.typography.headlineMedium)
}

@Composable
private fun NameSection(
    name: String,
    isLoading: Boolean,
    onNameChange: (String) -> Unit
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("이름") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = !isLoading
    )
}

@Composable
private fun EmailSection(
    email: String,
    emailError: String?,
    canRequestEmailVerification: Boolean,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onRequestEmailVerification: () -> Unit
) {
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = !isLoading,
            isError = emailError != null,
            supportingText = if (emailError != null) {
                { ValidationErrorText(emailError) }
            } else null
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedButton(
            onClick = onRequestEmailVerification,
            enabled = canRequestEmailVerification,
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text("인증 요청")
        }
    }
}

@Composable
private fun AuthCodeSection(
    authCode: String,
    isLoading: Boolean,
    isEmailVerified: Boolean,
    canVerifyAuthCode: Boolean,
    onAuthCodeChange: (String) -> Unit,
    onVerifyAuthCode: () -> Unit
) {
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = !isLoading && !isEmailVerified
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedButton(
            onClick = onVerifyAuthCode,
            enabled = canVerifyAuthCode
        ) {
            Text(if (isEmailVerified) "인증 완료" else "인증 확인")
        }
    }
}

@Composable
private fun PasswordSection(
    password: String,
    passwordErrors: List<String>,
    isLoading: Boolean,
    onPasswordChange: (String) -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("비밀번호") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        enabled = !isLoading,
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
}

@Composable
private fun ConfirmPasswordSection(
    confirmPassword: String,
    confirmPasswordError: String?,
    isLoading: Boolean,
    onConfirmPasswordChange: (String) -> Unit
) {
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        label = { Text("비밀번호 재입력") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (isConfirmPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        enabled = !isLoading,
        isError = confirmPasswordError != null,
        trailingIcon = {
            PasswordVisibilityToggle(
                visible = isConfirmPasswordVisible,
                onToggle = { isConfirmPasswordVisible = !isConfirmPasswordVisible }
            )
        },
        supportingText = if (confirmPasswordError != null) {
            { ValidationErrorText(confirmPasswordError) }
        } else null
    )
}

@Composable
private fun SignupSubmitSection(
    canSignup: Boolean,
    onSignupClick: () -> Unit
) {
    Button(
        onClick = onSignupClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = canSignup
    ) {
        Text("회원가입")
    }
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

@Composable
private fun ValidationErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall
    )
}

private data class SignupUiFlags(
    val isLoading: Boolean,
    val emailError: String?,
    val canRequestEmailVerification: Boolean,
    val passwordErrors: List<String>,
    val confirmPasswordError: String?,
    val canVerifyAuthCode: Boolean,
    val canSignup: Boolean
)

@Composable
private fun rememberSignupUiFlags(
    name: String,
    authCode: String,
    confirmPassword: String,
    emailValidation: EmailValidationResult?,
    passwordValidation: PasswordValidationResult?,
    isPasswordMatched: Boolean,
    isEmailVerified: Boolean,
    status: SignupStatus
): SignupUiFlags {
    val isLoading = status is SignupStatus.Loading
    val emailError = emailValidation.toErrorMessage()
    val isEmailValid = emailValidation == EmailValidationResult.Valid
    val passwordErrors = passwordValidation.toErrorMessages()
    val confirmPasswordError =
        if (confirmPassword.isNotBlank() && !isPasswordMatched) "비밀번호가 일치하지 않습니다" else null
    val canRequestEmailVerification = !isLoading && !isEmailVerified && isEmailValid
    val canVerifyAuthCode = !isLoading && !isEmailVerified && authCode.isNotBlank()
    val canSignup = isEmailVerified &&
        !isLoading &&
        name.isNotBlank() &&
        passwordErrors.isEmpty() &&
        confirmPassword.isNotBlank() &&
        isPasswordMatched
    return SignupUiFlags(
        isLoading = isLoading,
        emailError = emailError,
        canRequestEmailVerification = canRequestEmailVerification,
        passwordErrors = passwordErrors,
        confirmPasswordError = confirmPasswordError,
        canVerifyAuthCode = canVerifyAuthCode,
        canSignup = canSignup
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
            confirmPassword = "",
            authCode = "",
            emailValidation = null,
            passwordValidation = null,
            isPasswordMatched = false,
            isEmailRequestSent = false,
            isEmailVerified = false,
            status = SignupStatus.Idle,
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
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
            confirmPassword = "Password1!",
            authCode = "",
            emailValidation = EmailValidationResult.Valid,
            passwordValidation = PasswordValidationResult.Valid,
            isPasswordMatched = true,
            isEmailRequestSent = true,
            isEmailVerified = false,
            status = SignupStatus.Idle,
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onAuthCodeChange = {},
            onRequestEmailVerification = {},
            onVerifyAuthCode = {},
            onSignupClick = {}
        )
    }
}
