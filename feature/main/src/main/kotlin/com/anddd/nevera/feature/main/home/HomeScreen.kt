package com.anddd.nevera.feature.main.home

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.core.designsystem.component.dialog.NeveraConfirmDialog
import com.anddd.nevera.feature.main.R
import com.anddd.nevera.feature.main.home.component.HomeContent
import com.anddd.nevera.feature.main.home.model.HomeSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.collectAsState().value
    var showCaptureModeDialog by remember { mutableStateOf(false) }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is HomeSideEffect.ShowError ->
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            HomeSideEffect.ShowCaptureModeDialog ->
                showCaptureModeDialog = true
        }
    }

    if (showCaptureModeDialog) {
        NeveraConfirmDialog(
            title = stringResource(R.string.home_capture_mode_title),
            positive = stringResource(R.string.home_capture_mode_camera),
            negative = stringResource(R.string.home_capture_mode_gallery),
            onPositive = {
                showCaptureModeDialog = false
                onNavigateToCamera()
            },
            onNegative = {
                showCaptureModeDialog = false
                onNavigateToGallery()
            },
        )
    }

    HomeContent(
        uiState = state,
        onIntent = viewModel::handleIntent
    )
}
