package com.anddd.nevera.feature.main.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.ui.component.ErrorContent
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.main.home.component.HomeContent
import com.anddd.nevera.feature.main.home.model.HomeUiState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Loading -> LoadingContent()
        is HomeUiState.Error -> ErrorContent(message = state.message)
        is HomeUiState.Success -> HomeContent()
    }
}

