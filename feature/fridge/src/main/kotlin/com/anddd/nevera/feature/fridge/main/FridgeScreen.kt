package com.anddd.nevera.feature.fridge.main

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.anddd.nevera.core.ui.component.ReceiptCaptureModeBottomSheet
import com.anddd.nevera.feature.fridge.main.component.FridgeContent
import com.anddd.nevera.feature.fridge.main.model.FridgeSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToNotification: () -> Unit,
    viewModel: FridgeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value
    var showCaptureModeBottomSheet by remember { mutableStateOf(false) }
    val captureModeSheetState = rememberModalBottomSheetState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is FridgeSideEffect.ShowToast ->
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            FridgeSideEffect.ShowCaptureModeBottomSheet ->
                showCaptureModeBottomSheet = true
            FridgeSideEffect.NavigateToNotification -> onNavigateToNotification()
        }
    }

    FridgeContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
    )

    if (showCaptureModeBottomSheet) {
        ReceiptCaptureModeBottomSheet(
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
