package com.anddd.nevera.feature.auth.signup

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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

    // stringResource는 @Composable이라 collectSideEffect 람다 안에서 호출할 수 없다.
    // 컴포저블 본문에서 미리 읽어 둔다.
    val duplicateEmailMessage = stringResource(R.string.signup_toast_duplicate_email)
    val mailSendErrorMessage = stringResource(R.string.signup_toast_mail_send_error)
    val networkErrorMessage = stringResource(R.string.signup_toast_network_error)
    val verifyNotFoundMessage = stringResource(R.string.signup_toast_verify_not_found)
    val emailNotVerifiedMessage = stringResource(R.string.signup_toast_email_not_verified)
    val unverifiedEmailMessage = stringResource(R.string.signup_toast_unverified_email)
    val signupFailedMessage = stringResource(R.string.signup_toast_signup_failed)
    val timerExpiredMessage = stringResource(R.string.signup_toast_timer_expired)

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SignupSideEffect.MoveToLoginScreen -> onNavigateToLogin()
            is SignupSideEffect.EmailRequestDuplicateEmail ->
                showToast(effect.message ?: duplicateEmailMessage)
            is SignupSideEffect.EmailRequestMailSendError ->
                showToast(effect.message ?: mailSendErrorMessage)
            is SignupSideEffect.EmailRequestNetworkError ->
                showToast(effect.message ?: networkErrorMessage)
            is SignupSideEffect.EmailVerifyNotFound ->
                showToast(effect.message ?: verifyNotFoundMessage)
            SignupSideEffect.SignupEmailNotVerified ->
                showToast(emailNotVerifiedMessage)
            is SignupSideEffect.SignupUnverifiedEmail ->
                showToast(effect.message ?: unverifiedEmailMessage)
            is SignupSideEffect.SignupAuthNotFound ->
                showToast(effect.message ?: verifyNotFoundMessage)
            SignupSideEffect.SignupServerError ->
                showToast(signupFailedMessage)
            SignupSideEffect.TimerExpired ->
                showToast(timerExpiredMessage)
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
