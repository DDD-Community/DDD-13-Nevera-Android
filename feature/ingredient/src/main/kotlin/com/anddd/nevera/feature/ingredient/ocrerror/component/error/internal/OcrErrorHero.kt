package com.anddd.nevera.feature.ingredient.ocrerror.component.error.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R

private val HeroSectionHeight = 368.dp
private val HeroImageSize = 200.dp

/**
 * OCR 인식 실패 히어로 섹션 — 캐릭터 이미지 + 제목 + 부제목
 *
 * Figma: 상단 컨텐츠 영역 (AppBar 하단 ~ 팁 카드 상단)
 */
@Composable
internal fun OcrErrorHeader() {
    Column(
        modifier = Modifier.fillMaxWidth()
            .height(HeroSectionHeight),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.img_fail),
            contentDescription = null,
            modifier = Modifier.size(HeroImageSize),
            contentScale = ContentScale.Fit,
        )

        Spacer(Modifier.height(NeveraTheme.spacing.gap24))

        Text(
            text = stringResource(R.string.ocr_error_title),
            modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding16),
            color = NeveraTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = NeveraTheme.typography.headlineSmall,
        )

        Spacer(Modifier.height(NeveraTheme.spacing.gap4))

        Text(
            text = stringResource(R.string.ocr_error_subtitle),
            modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding16),
            color = NeveraTheme.colors.textQuaternary,
            textAlign = TextAlign.Center,
            style = NeveraTheme.typography.bodySmall,
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun OcrErrorHeaderPreview() {
    NeveraTheme {
        OcrErrorHeader()
    }
}
