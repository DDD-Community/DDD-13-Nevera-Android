package com.anddd.nevera.feature.signup.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.signup.main.component.SignupContent
import com.anddd.nevera.feature.signup.main.model.SignupSideEffect
import com.anddd.nevera.feature.signup.main.model.SignupStatus

@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SignupSideEffect.ShowToast ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is SignupSideEffect.ShowErrorToast ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is SignupSideEffect.MoveToLoginScreen -> onNavigateToLogin()
            }
        }
    }

    when (uiState.status) {
        is SignupStatus.Loading -> LoadingContent()
        else -> SignupContent(
            name = uiState.name,
            email = uiState.email,
            password = uiState.password,
            authCode = uiState.authCode,
            emailValidation = uiState.emailValidation,
            passwordValidation = uiState.passwordValidation,
            isEmailRequestSent = uiState.isEmailRequestSent,
            isEmailVerified = uiState.isEmailVerified,
            status = uiState.status,
            onNameChange = viewModel::onNameChange,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onAuthCodeChange = viewModel::onAuthCodeChange,
            onRequestEmailVerification = viewModel::requestEmailVerification,
            onVerifyAuthCode = viewModel::verifyAuthCode,
            onSignupClick = viewModel::signup
        )
    }
}
