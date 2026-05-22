package com.anddd.nevera.feature.ingredient.main.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.LoadingContent
import com.anddd.nevera.feature.ingredient.main.model.IngredientIntent
import com.anddd.nevera.feature.ingredient.main.model.IngredientUiState

@Composable
internal fun IngredientContent(
    uiState: IngredientUiState,
    onIntent: (IngredientIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TODO: UI 구현
            Text(text = "Ingredient")
        }
        if (uiState.isLoading) {
            LoadingContent()
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun IngredientContentPreview() {
    NeveraTheme {
        IngredientContent(
            uiState = IngredientUiState(),
            onIntent = {},
        )
    }
}
