package com.anddd.nevera.feature.mypage.settingaccount

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.feature.mypage.settingaccount.component.SettingAccountContent
import com.anddd.nevera.feature.mypage.settingaccount.model.SettingAccountSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SettingAccountScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SettingAccountViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SettingAccountSideEffect.NavigateBack -> onNavigateBack()
            SettingAccountSideEffect.NavigateToLogin -> onNavigateToLogin()
            SettingAccountSideEffect.ShowNetworkErrorToast -> {
                Toast.makeText(context, "네트워크 연결을 확인해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    SettingAccountContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
    )
}
