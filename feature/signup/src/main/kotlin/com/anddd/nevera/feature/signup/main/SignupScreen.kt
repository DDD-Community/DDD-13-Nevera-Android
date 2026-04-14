package com.anddd.nevera.feature.signup.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
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
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleOwner, viewModel, context, onNavigateToLogin) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.sideEffect.collect { effect ->
                when (effect) {
                    is SignupSideEffect.ShowToast ->
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    is SignupSideEffect.MoveToLoginScreen -> onNavigateToLogin()
                }
            }
        }
    }

    when (uiState.status) {
        is SignupStatus.Loading -> LoadingContent()
        else -> SignupContent(
            name = uiState.name,
            email = uiState.email,
            password = uiState.password,
            confirmPassword = uiState.confirmPassword,
            authCode = uiState.authCode,
            emailValidation = uiState.emailValidation,
            passwordValidation = uiState.passwordValidation,
            isPasswordMatched = uiState.isPasswordMatched,
            isEmailRequestSent = uiState.isEmailRequestSent,
            isEmailVerified = uiState.isEmailVerified,
            status = uiState.status,
            onNameChange = viewModel::onNameChange,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onAuthCodeChange = viewModel::onAuthCodeChange,
            onRequestEmailVerification = viewModel::requestEmailVerification,
            onVerifyAuthCode = viewModel::verifyAuthCode,
            onSignupClick = viewModel::signup
        )
    }
}
