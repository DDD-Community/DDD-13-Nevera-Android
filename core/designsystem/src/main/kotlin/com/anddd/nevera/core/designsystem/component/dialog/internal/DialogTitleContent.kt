package com.anddd.nevera.core.designsystem.component.dialog.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
internal fun DialogTitleContent(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth()
                .heightIn(47.dp)
                .padding(
                    start = NeveraTheme.spacing.padding20,
                    top = NeveraTheme.spacing.padding20,
                    end = NeveraTheme.spacing.padding20,
                ),
            color = NeveraTheme.colors.textSecondary,
            style = NeveraTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = subtitle,
            modifier = Modifier.fillMaxWidth()
                .heightIn(37.dp)
                .padding(
                    start = NeveraTheme.spacing.padding20,
                    top = NeveraTheme.spacing.padding16,
                    end = NeveraTheme.spacing.padding20,
                ),
            color = NeveraTheme.colors.textQuaternary,
            style = NeveraTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DialogTitleContentPreview() {
    NeveraTheme {
        DialogTitleContent(
            title = "Title",
            subtitle = "Subtitle",
        )
    }
}