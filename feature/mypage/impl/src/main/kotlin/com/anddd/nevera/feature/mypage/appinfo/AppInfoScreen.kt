package com.anddd.nevera.feature.mypage.appinfo

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.feature.mypage.appinfo.component.AppInfoContent
import com.anddd.nevera.feature.mypage.appinfo.model.AppInfoSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber

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

            is AppInfoSideEffect.OpenUrl -> {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, effect.url.toUri()))
                } catch (e: ActivityNotFoundException) {
                    Timber.e(e)
                    Toast.makeText(context, "브라우저 앱을 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    AppInfoContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
    )
}
