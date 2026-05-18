package com.anddd.nevera.feature.login.main

import android.app.Activity
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.login.main.component.LoginContent
import com.anddd.nevera.feature.login.main.google.GoogleAuthClient
import com.anddd.nevera.feature.login.main.model.LoginIntent
import com.anddd.nevera.feature.login.main.model.LoginSideEffect
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber

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
        Timber.i("Google 로그인은 Activity context가 필요합니다")
        return
    }
    val scope = rememberCoroutineScope()
    val uiState = viewModel.collectAsState().value

    fun googleLogin() {
        scope.launch {
            runCatching {
                googleAuthClient.getIdToken(activity)
            }.onSuccess { idToken ->
                viewModel.handleIntent(LoginIntent.LoginWithGoogleClicked(idToken))
            }.onFailure { throwable ->
                viewModel.handleIntent(LoginIntent.GoogleLoginFailed(throwable))
            }
        }
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is LoginSideEffect.EmailLoginFailed ->
                Toast.makeText(context, effect.error.message, Toast.LENGTH_SHORT).show()
            is LoginSideEffect.GoogleLoginFailed ->
                Toast.makeText(context, effect.error.message, Toast.LENGTH_SHORT).show()
            LoginSideEffect.MoveToHomeScreen -> onNavigateToHome()
        }
    }

    if (uiState.isLoading) {
        LoadingContent()
    } else {
        LoginContent(
            email = uiState.email,
            password = uiState.password,
            emailValidation = uiState.emailValidation,
            passwordValidation = uiState.passwordValidation,
            onEmailChange = { viewModel.handleIntent(LoginIntent.EmailChanged(it)) },
            onPasswordChange = { viewModel.handleIntent(LoginIntent.PasswordChanged(it)) },
            onLoginClick = { viewModel.handleIntent(LoginIntent.LoginWithEmailClicked) },
            onSignupClick = onNavigateToSignup,
            onGoogleLoginClick = ::googleLogin,
        )
    }
}
