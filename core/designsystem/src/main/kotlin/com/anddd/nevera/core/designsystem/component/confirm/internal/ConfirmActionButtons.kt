package com.anddd.nevera.core.designsystem.component.confirm.internal

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonColor
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.component.button.NeveraWeakButton
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

/**
 * Confirm surface에서 공통으로 쓰는 negative/positive 액션 행입니다.
 * 버튼 순서와 간격 규칙을 한 곳에서 유지하기 위해 분리했습니다.
 */
@Composable
internal fun ConfirmActionButtons(
    positive: String,
    negative: String,
    onPositive: () -> Unit,
    onNegative: () -> Unit,
) {
    Row(modifier = Modifier.padding(NeveraTheme.spacing.padding16)) {
        NeveraWeakButton(
            label = negative,
            onClick = onNegative,
            modifier = Modifier.weight(1f),
            color = NeveraButtonColor.Primary,
        )

        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap12))

        NeveraFilledButton(
            label = positive,
            onClick = onPositive,
            modifier = Modifier.weight(1f),
            color = NeveraButtonColor.Primary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfirmActionButtonsPreview() {
    NeveraTheme {
        ConfirmActionButtons(
            positive = "CTA",
            negative = "Sub",
            onPositive = {},
            onNegative = {},
        )
    }
}
