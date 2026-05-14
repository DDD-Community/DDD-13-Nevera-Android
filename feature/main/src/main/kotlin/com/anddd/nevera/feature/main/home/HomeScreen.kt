package com.anddd.nevera.feature.main.home

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.ui.component.ErrorContent
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.main.home.component.HomeContent
import com.anddd.nevera.feature.main.home.model.HomeSideEffect
import com.anddd.nevera.feature.main.home.model.HomeUiState

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is HomeSideEffect.NavigateToLogin -> onNavigateToLogin()
                is HomeSideEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    when (val state = uiState) {
        is HomeUiState.Loading -> LoadingContent()
        is HomeUiState.Error -> ErrorContent(message = state.message)
        is HomeUiState.Success -> HomeContent(
            onLogoutClick = viewModel::logout,
            onWithdrawClick = viewModel::withdraw,
            onNavigateToMyPage = onNavigateToMyPage,
        )
    }
}
