package com.anddd.nevera.core.designsystem.component.datepicker.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.R
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import java.time.LocalDate

private val DialogButtonsRowHeight = 60.dp
private val DialogButtonHeight = 48.dp

@Composable
internal fun DialogButtons(
    tempSelected: LocalDate?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(DialogButtonsRowHeight)
            .padding(
                start = NeveraTheme.spacing.padding12,
                top = NeveraTheme.spacing.padding4,
                end = NeveraTheme.spacing.padding12,
                bottom = NeveraTheme.spacing.padding8,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.height(DialogButtonHeight)
        ) {
            Text(
                text = stringResource(R.string.nevera_date_picker_dismiss),
                color = NeveraTheme.colors.primaryNormal,
            )
        }
        Spacer(Modifier.width(NeveraTheme.spacing.gap4))
        TextButton(
            onClick = onConfirm,
            enabled = tempSelected != null,
        ) {
            Text(
                text = stringResource(R.string.nevera_date_picker_confirm),
                color = if (tempSelected != null) NeveraTheme.colors.primaryNormal
                else NeveraTheme.colors.textDisabled,
            )
        }
    }
}

@Preview(
    name = "DialogButtons - 날짜 선택됨 (확인 활성)",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun DialogButtonsSelectedPreview() {
    NeveraTheme {
        DialogButtons(
            tempSelected = LocalDate.of(2026, 12, 17),
            onDismiss = {},
            onConfirm = {},
        )
    }
}

@Preview(
    name = "DialogButtons - 미선택 (확인 비활성)",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun DialogButtonsEmptyPreview() {
    NeveraTheme {
        DialogButtons(
            tempSelected = null,
            onDismiss = {},
            onConfirm = {},
        )
    }
}