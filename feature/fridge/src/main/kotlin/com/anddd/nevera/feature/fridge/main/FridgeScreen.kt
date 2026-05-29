package com.anddd.nevera.feature.fridge.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.anddd.nevera.feature.fridge.main.component.FridgeContent
import com.anddd.nevera.feature.fridge.main.model.FridgeSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun FridgeScreen(
    viewModel: FridgeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is FridgeSideEffect.ShowToast ->
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
        }
    }

    FridgeContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
    )
}
