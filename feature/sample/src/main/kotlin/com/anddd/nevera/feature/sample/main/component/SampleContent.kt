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

@Composable
internal fun SampleContent(
    count: Int,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "count: $count")
        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap16))
        Button(onClick = onButtonClick) {
            Text(text = "클릭")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SampleContentPreview() {
    NeveraTheme {
        SampleContent(
            count = 0,
            onButtonClick = {},
        )
    }
}
