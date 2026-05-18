package com.anddd.nevera.core.designsystem.component.confirm.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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

/**
 * Confirm surface에서 공통으로 쓰는 제목/설명 블록입니다.
 * Dialog와 BottomSheet가 같은 규칙을 쓸 때만 공유합니다.
 */
@Composable
internal fun ConfirmTitleContent(
    title: String,
    subtitle: String? = null,
) {
    Column(
        modifier = Modifier
            .padding(
                start = NeveraTheme.spacing.padding20,
                end = NeveraTheme.spacing.padding20,
                top = NeveraTheme.spacing.padding20,
            )
    ) {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(27.dp),
            color = NeveraTheme.colors.textSecondary,
            style = NeveraTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        if (!subtitle.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(NeveraTheme.spacing.padding16))
            Text(
                text = subtitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(21.dp),
                color = NeveraTheme.colors.textQuaternary,
                style = NeveraTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(
    name = "ConfirmTitleContent - 제목만",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun ConfirmTitleContentTitleOnlyPreview() {
    NeveraTheme {
        ConfirmTitleContent(
            title = "제목",
        )
    }
}

@Preview(
    name = "ConfirmTitleContent - 제목 + 부제목",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun ConfirmTitleContentWithSubtitlePreview() {
    NeveraTheme {
        ConfirmTitleContent(
            title = "제목",
            subtitle = "부제목 설명 텍스트입니다.",
        )
    }
}
