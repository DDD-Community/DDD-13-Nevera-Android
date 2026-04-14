package com.anddd.nevera.feature.login.main

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.login.main.component.LoginContent
import com.anddd.nevera.feature.login.main.google.GoogleAuthClient
import com.anddd.nevera.feature.login.main.model.LoginSideEffect
import com.anddd.nevera.feature.login.main.model.LoginStatus
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignup: () -> Unit,
    googleAuthClient: GoogleAuthClient,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    if (activity == null) {
        Log.w("LoginScreen", "Google 로그인은 Activity context가 필요합니다")
        return
    }
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    fun googleLogin() {
        scope.launch {
            runCatching {
                googleAuthClient.getIdToken(activity)
            }.onSuccess { idToken ->
                viewModel.loginWithGoogle(idToken)
            }.onFailure { throwable ->
                viewModel.handleGoogleLoginFailure(throwable)
            }
        }
    }

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
            onLoginClick = { viewModel.loginWithEmail(uiState.email, uiState.password) },
            onSignupClick = onNavigateToSignup,
            onGoogleLoginClick = ::googleLogin
        )
    }
}
