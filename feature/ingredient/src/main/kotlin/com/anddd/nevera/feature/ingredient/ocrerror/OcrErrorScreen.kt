package com.anddd.nevera.feature.ingredient.ocrerror

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBar
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarNavigation
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonColor
import com.anddd.nevera.core.designsystem.component.button.NeveraOutlinedButton
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R
import com.anddd.nevera.feature.ingredient.ocrerror.component.error.internal.OcrErrorContent

/**
 * OCR 인식 실패 화면
 *
 * @param onRetry "다시 시도하기" 탭 → 이전 화면으로 pop (이미지 재선택)
 * @param onClose X 버튼 탭 → 화면 종료
 */
@Composable
fun OcrErrorScreen(
    onRetry: () -> Unit,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(NeveraTheme.colors.backgroundPrimary),
    ) {
        // AppBar — AppBarContainer 내부에서 statusBarsPadding 처리
        NeveraAppBar(
            navigation = NeveraAppBarNavigation.Close(
                onClick = onClose
            ),
            showBackground = false,
        )

        // 스크롤 콘텐츠
        OcrErrorContent(
            modifier = Modifier.fillMaxWidth().weight(1f),
        )

        // 하단 고정 버튼 — Figma 버튼 영역 152dp: gap32(top) + 버튼52 + padding16(bottom) + navBar
        NeveraOutlinedButton(
            label = stringResource(R.string.ocr_error_retry),
            onClick = onRetry,
            color = NeveraButtonColor.Primary,
            shape = RoundedCornerShape(NeveraTheme.radius.medium),
            modifier = Modifier.fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    start = NeveraTheme.spacing.padding16,
                    end = NeveraTheme.spacing.padding16,
                    top = NeveraTheme.spacing.gap32,
                    bottom = NeveraTheme.spacing.padding16,
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OcrErrorScreenPreview() {
    NeveraTheme {
        OcrErrorScreen(onRetry = {}, onClose = {})
    }
}
