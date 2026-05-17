package com.anddd.nevera.feature.mypage.appinfo

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.mypage.appinfo.component.AppInfoContent
import com.anddd.nevera.feature.mypage.appinfo.model.AppInfoSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun AppInfoScreen(
    onNavigateBack: () -> Unit,
    viewModel: AppInfoViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value

    viewModel.collectSideEffect { effect ->
        when (effect) {
            AppInfoSideEffect.NavigateBack -> onNavigateBack()

            AppInfoSideEffect.ShowNetworkErrorToast -> {
                Toast.makeText(context, "네트워크 연결을 확인해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    NeveraTheme {
        AppInfoContent(
            uiState = uiState,
            onIntent = viewModel::handleIntent,
        )
    }
}
