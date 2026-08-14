package com.anddd.nevera.feature.ingredient.ocrerror.component.error.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
internal fun OcrErrorContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OcrErrorHeader()

            Spacer(Modifier.height(NeveraTheme.spacing.gap16))

            OcrErrorTipCard()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OcrErrorContentPreview() {
    NeveraTheme {
        OcrErrorContent()
    }
}
