package com.anddd.nevera.feature.mypage.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.feature.mypage.main.component.MyPageContent
import com.anddd.nevera.feature.mypage.main.model.MyPageSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MyPageScreen(
    onNavigateToAppInfo: () -> Unit,
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is MyPageSideEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()

            MyPageSideEffect.NavigateToAppInfo -> onNavigateToAppInfo()
        }
    }

    MyPageContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
    )
}
