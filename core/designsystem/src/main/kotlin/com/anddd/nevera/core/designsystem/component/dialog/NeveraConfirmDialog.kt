package com.anddd.nevera.core.designsystem.component.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.dialog.internal.DialogActionButtons
import com.anddd.nevera.core.designsystem.component.dialog.internal.DialogTitleContent
import com.anddd.nevera.core.designsystem.component.dialog.internal.NeveraDialog
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
fun NeveraConfirmDialog(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    positive: String,
    negative: String,
    onPositive: () -> Unit,
    onNegative: () -> Unit,
) {
    NeveraDialog(
        modifier = modifier,
        onDismissRequest = onNegative
    ) {
        DialogTitleContent(title = title, subtitle = subtitle)
        DialogActionButtons(
            positive = positive,
            negative = negative,
            onPositive = onPositive,
            onNegative = onNegative,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NeveraConfirmDialogPreview() {
    NeveraTheme {
        NeveraConfirmDialog(
            title = "Title",
            subtitle = "Subtitle",
            positive = "CTA",
            negative = "Sub",
            onPositive = {},
            onNegative = {}
        )
    }
}
