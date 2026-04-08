package com.anddd.nevera.feature.login.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.login.main.component.LoginContent
import com.anddd.nevera.feature.login.main.model.LoginSideEffect
import com.anddd.nevera.feature.login.main.model.LoginStatus

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is LoginSideEffect.ShowToast ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is LoginSideEffect.ShowErrorToast ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is LoginSideEffect.MoveToHomeScreen -> onNavigateToHome()
            }
        }
    }

    when (uiState.status) {
        is LoginStatus.Loading -> LoadingContent()
        else -> LoginContent(
            email = uiState.email,
            password = uiState.password,
            emailValidation = uiState.emailValidation,
            passwordValidation = uiState.passwordValidation,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onLoginClick = { viewModel.login(uiState.email, uiState.password) },
            onSignupClick = { /* move signup screen */ },
            onGoogleLoginClick = onGoogleLoginClick
        )
    }
}
