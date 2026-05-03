package com.anddd.nevera.core.designsystem.component.dialog.internal

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

// TODO :: NeveraFilledButton 으로 변경 필요
@Composable
internal fun DialogActionButtons(
    positive: String,
    negative: String,
    onPositive: () -> Unit,
    onNegative: () -> Unit,
) {
    Row(modifier = Modifier.padding(NeveraTheme.spacing.padding6)) {
        Button(
            onClick = onNegative,
            modifier = Modifier.height(48.dp)
                .weight(1f),
            colors = buttonColors(
                containerColor = NeveraTheme.colors.surfaceBrandPrimary,
                contentColor = NeveraTheme.colors.primaryNormal,
                disabledContainerColor = NeveraTheme.colors.surfaceBrandPrimary,
                disabledContentColor = NeveraTheme.colors.primaryNormal,
            ),
            shape = RoundedCornerShape(size = NeveraTheme.radius.small),
        ) {
            Text(text = negative)
        }

        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap5))

        Button(
            onClick = onPositive,
            modifier = Modifier.height(48.dp)
                .weight(1f),
            colors = buttonColors(
                containerColor = NeveraTheme.colors.primaryNormal,
                contentColor = NeveraTheme.colors.textInverse,
                disabledContainerColor = NeveraTheme.colors.primaryNormal,
                disabledContentColor = NeveraTheme.colors.textInverse,
            ),
            shape = RoundedCornerShape(size = NeveraTheme.radius.small),
        ) {
            Text(text = positive)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DialogActionButtonsPreview() {
    NeveraTheme {
        DialogActionButtons(
            positive = "CTA",
            negative = "Sub",
            onPositive = {},
            onNegative = {},
        )
    }
}