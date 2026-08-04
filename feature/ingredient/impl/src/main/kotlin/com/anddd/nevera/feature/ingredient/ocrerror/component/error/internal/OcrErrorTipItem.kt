package com.anddd.nevera.feature.ingredient.ocrerror.component.error.internal

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

private val TipItemMinHeight = 21.dp

/**
 * OCR 팁 단일 항목 — 체크 아이콘 + 팁 텍스트
 */
@Composable
internal fun OcrErrorTipItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .heightIn(TipItemMinHeight)
            .padding(start = NeveraTheme.spacing.gap4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = NeveraIcons.Check,
            contentDescription = null,
            tint = NeveraTheme.colors.iconDisabled,
            modifier = Modifier.size(NeveraTheme.iconSize.xSmall),
        )
        Spacer(Modifier.width(NeveraTheme.spacing.gap8))
        Text(
            text = text,
            style = NeveraTheme.typography.bodySmall,
            color = NeveraTheme.colors.textTertiary,
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun OcrErrorTipItemPreview() {
    NeveraTheme {
        OcrErrorTipItem(text = "밝고 그림자가 없는 곳에서 촬영해주세요")
    }
}
