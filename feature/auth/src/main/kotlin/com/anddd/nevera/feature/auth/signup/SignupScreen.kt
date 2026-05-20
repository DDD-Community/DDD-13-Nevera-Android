package com.anddd.nevera.feature.auth.signup

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.auth.R
import com.anddd.nevera.feature.auth.signup.component.SignupContent
import com.anddd.nevera.feature.auth.signup.model.SignupSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SignupSideEffect.MoveToLoginScreen -> onNavigateToLogin()
            is SignupSideEffect.EmailRequestDuplicateEmail ->
                context.showToast(effect.message ?: context.getString(R.string.signup_toast_duplicate_email))
            is SignupSideEffect.EmailRequestMailSendError ->
                context.showToast(effect.message ?: context.getString(R.string.signup_toast_mail_send_error))
            is SignupSideEffect.EmailRequestNetworkError ->
                context.showToast(effect.message ?: context.getString(R.string.signup_toast_network_error))
            is SignupSideEffect.EmailVerifyNotFound ->
                context.showToast(effect.message ?: context.getString(R.string.signup_toast_verify_not_found))
            SignupSideEffect.SignupEmailNotVerified ->
                context.showToast(context.getString(R.string.signup_toast_email_not_verified))
            is SignupSideEffect.SignupUnverifiedEmail ->
                context.showToast(effect.message ?: context.getString(R.string.signup_toast_unverified_email))
            is SignupSideEffect.SignupAuthNotFound ->
                context.showToast(effect.message ?: context.getString(R.string.signup_toast_verify_not_found))
            SignupSideEffect.SignupServerError ->
                context.showToast(context.getString(R.string.signup_toast_signup_failed))
            SignupSideEffect.TimerExpired ->
                context.showToast(context.getString(R.string.signup_toast_timer_expired))
        }
    }

    Box {
        SignupContent(
            uiState = uiState,
            onIntent = viewModel::handleIntent,
        )
        if (uiState.isLoading) {
            LoadingContent()
        }
    }
}

private fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
