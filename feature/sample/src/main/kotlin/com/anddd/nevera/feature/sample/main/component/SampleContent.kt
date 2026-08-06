package com.anddd.nevera.feature.sample.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.sample.main.model.SampleIntent
import com.anddd.nevera.feature.sample.main.model.SampleUiState

@Composable
internal fun SampleContent(
    uiState: SampleUiState,
    onIntent: (SampleIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "count: ${uiState.count}")
        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap16))
        Button(onClick = { onIntent(SampleIntent.ClickButton) }) {
            Text(text = "클릭")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SampleContentPreview() {
    NeveraTheme {
        SampleContent(
            uiState = SampleUiState(count = 0),
            onIntent = {},
        )
    }
}
