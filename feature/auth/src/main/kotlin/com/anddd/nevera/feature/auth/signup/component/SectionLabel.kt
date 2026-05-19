package com.anddd.nevera.feature.auth.signup.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
internal fun SectionLabel(text: String) {
    Row {
        Text(
            text = text,
            style = NeveraTheme.typography.titleXSmall,
            color = NeveraTheme.colors.textCaption,
        )
        Text(
            text = "*",
            modifier = Modifier.padding(start = NeveraTheme.spacing.gap2),
            style = NeveraTheme.typography.titleXSmall,
            color = NeveraTheme.colors.notificationRed,
        )
    }
}

@Preview(showBackground = true, name = "SectionLabel")
@Composable
private fun SectionLabelPreview() {
    NeveraTheme {
        SectionLabel(text = "이메일")
    }
}
