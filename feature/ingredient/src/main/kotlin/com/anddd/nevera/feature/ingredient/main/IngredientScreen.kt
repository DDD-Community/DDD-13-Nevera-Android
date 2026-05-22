package com.anddd.nevera.feature.ingredient.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.anddd.nevera.feature.ingredient.main.component.IngredientContent
import com.anddd.nevera.feature.ingredient.main.model.IngredientSideEffect
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun IngredientScreen(
    onNavigateBack: () -> Unit,
    viewModel: IngredientViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.collectAsState().value

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is IngredientSideEffect.ShowToast ->
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            IngredientSideEffect.NavigateBack -> onNavigateBack()
        }
    }

    IngredientContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
    )
}
