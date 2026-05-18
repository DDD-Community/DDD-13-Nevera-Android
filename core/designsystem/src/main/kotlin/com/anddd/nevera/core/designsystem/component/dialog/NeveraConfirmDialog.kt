package com.anddd.nevera.core.designsystem.component.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonColor
import com.anddd.nevera.core.designsystem.component.confirm.internal.ConfirmActionButtons
import com.anddd.nevera.core.designsystem.component.confirm.internal.ConfirmTitleContent
import com.anddd.nevera.core.designsystem.component.dialog.internal.NeveraDialog

@Composable
fun NeveraConfirmDialog(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    positive: String,
    negative: String,
    onPositive: () -> Unit,
    onNegative: () -> Unit,
    positiveButtonColor: NeveraButtonColor = NeveraButtonColor.Primary,
    negativeButtonColor: NeveraButtonColor = NeveraButtonColor.Primary,
) {
    NeveraDialog(
        modifier = modifier,
        onDismissRequest = onNegative
    ) {
        ConfirmTitleContent(title = title, subtitle = subtitle)
        ConfirmActionButtons(
            positive = positive,
            negative = negative,
            onPositive = onPositive,
            onNegative = onNegative,
            positiveButtonColor = positiveButtonColor,
            negativeButtonColor = negativeButtonColor
        )
    }
}
