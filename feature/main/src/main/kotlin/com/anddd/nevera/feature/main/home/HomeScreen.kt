package com.anddd.nevera.feature.main.home

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anddd.nevera.feature.main.home.component.CaptureModeBottomSheet
import com.anddd.nevera.feature.main.home.component.HomeContent
import com.anddd.nevera.feature.main.home.model.HomeSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.collectAsState().value
    var showCaptureModeBottomSheet by remember { mutableStateOf(false) }
    val captureModeSheetState = rememberModalBottomSheetState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is HomeSideEffect.ShowError ->
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            HomeSideEffect.ShowCaptureModeDialog ->
                showCaptureModeBottomSheet = true
        }
    }

    HomeContent(
        uiState = state,
        onIntent = viewModel::handleIntent,
    )

    if (showCaptureModeBottomSheet) {
        CaptureModeBottomSheet(
            sheetState = captureModeSheetState,
            onReceiptScan = {
                showCaptureModeBottomSheet = false
                onNavigateToCamera()
            },
            onOnlineCapture = {
                showCaptureModeBottomSheet = false
                onNavigateToGallery()
            },
            onDismiss = { showCaptureModeBottomSheet = false },
        )
    }
}
