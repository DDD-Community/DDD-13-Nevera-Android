package com.anddd.nevera.feature.auth.main

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.auth.main.component.LoginContent
import com.anddd.nevera.feature.auth.main.google.GoogleAuthClient
import com.anddd.nevera.feature.auth.main.model.LoginIntent
import com.anddd.nevera.feature.auth.main.model.LoginSideEffect
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    googleAuthClient: GoogleAuthClient,
    onNavigateToHome: () -> Unit,
    onNavigateToSignup: () -> Unit,
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
            try {
                val idToken = googleAuthClient.getIdToken(activity)
                viewModel.handleIntent(LoginIntent.GoogleLoginSucceeded(idToken))
            } catch (ce: CancellationException) {
                throw ce
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Google 토큰 취득 실패")
                viewModel.handleIntent(LoginIntent.GoogleLoginFailed)
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
            LoginSideEffect.MoveToSignupScreen -> onNavigateToSignup()
            LoginSideEffect.StartGoogleLogin -> googleLogin()
        }
    }

    Box {
        LoginContent(
            email = uiState.email,
            password = uiState.password,
            emailValidation = uiState.emailValidation,
            passwordValidation = uiState.passwordValidation,
            onIntent = viewModel::handleIntent,
        )
        if (uiState.isLoading) {
            LoadingContent()
        }
    }
}
