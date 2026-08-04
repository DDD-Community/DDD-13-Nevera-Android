package com.anddd.nevera.feature.ingredient.ocrerror.component.error.internal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R

/**
 * OCR 팁 카드 — 촬영 가이드 3항목
 *
 * Figma: 328 재우기 (padding20 수평)
 */
@Composable
internal fun OcrErrorTipCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = NeveraTheme.spacing.padding20),
        colors = CardDefaults.cardColors(
            containerColor = NeveraTheme.colors.surfacePrimary,
        ),
        border = BorderStroke(1.dp, NeveraTheme.colors.borderNormal),
        shape = RoundedCornerShape(NeveraTheme.radius.medium),
    ) {
        Column(modifier = Modifier.padding(NeveraTheme.spacing.padding16)) {
            // 팁 헤더
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = NeveraIcons.IllustLighting,
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(NeveraTheme.iconSize.medium),
                )
                Spacer(Modifier.width(NeveraTheme.spacing.gap8))
                Text(
                    text = stringResource(R.string.ocr_error_tip_header),
                    color = NeveraTheme.colors.textTertiary,
                    fontWeight = FontWeight.Bold,
                    style = NeveraTheme.typography.titleXSmall,
                )
            }

            Spacer(Modifier.height(NeveraTheme.spacing.gap16))

            OcrErrorTipItem(text = stringResource(R.string.ocr_error_tip_1))

            Spacer(Modifier.height(NeveraTheme.spacing.gap6))

            OcrErrorTipItem(text = stringResource(R.string.ocr_error_tip_2))

            Spacer(Modifier.height(NeveraTheme.spacing.gap6))

            OcrErrorTipItem(text = stringResource(R.string.ocr_error_tip_3))
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun OcrErrorTipCardPreview() {
    NeveraTheme {
        OcrErrorTipCard()
    }
}
