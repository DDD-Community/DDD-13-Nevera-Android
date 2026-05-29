package com.anddd.nevera.feature.mypage.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.ui.R as CoreUiR
import com.anddd.nevera.feature.mypage.main.component.MyPageContent
import com.anddd.nevera.feature.mypage.main.model.MyPageSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MyPageScreen(
    onNavigateToAppInfo: () -> Unit,
    onNavigateToAccountSetting: () -> Unit,
    onNavigateToNotification: () -> Unit,
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value
    val networkErrorMessage = stringResource(CoreUiR.string.error_network_message)

    viewModel.collectSideEffect { effect ->
        when (effect) {
            MyPageSideEffect.ShowNetworkErrorToast -> {
                Toast.makeText(context, networkErrorMessage, Toast.LENGTH_SHORT).show()
            }

            MyPageSideEffect.NavigateToAppInfo -> onNavigateToAppInfo()

            MyPageSideEffect.NavigateToAccountSetting -> onNavigateToAccountSetting()

            MyPageSideEffect.NavigateToNotification -> onNavigateToNotification()
        }
    }

    MyPageContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
    )
}
