package com.anddd.nevera.feature.ingredient.registersuccess

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R
import java.text.NumberFormat
import java.util.Locale

/**
 * 식재료 등록 완료 화면
 *
 * @param totalSavedAmount 등록된 식재료 금액 합계 (절약 가능 금액으로 표시)
 * @param onViewFridge     "나의 냉장고 보기" 탭 콜백
 * @param onClose          X 버튼 탭 콜백
 */
@Composable
fun RegisterSuccessScreen(
    totalSavedAmount: Int,
    onViewFridge: () -> Unit,
    onClose: () -> Unit,
) {
    val formattedAmount = remember(totalSavedAmount) {
        NumberFormat.getNumberInstance(Locale.KOREA).format(totalSavedAmount) + "원"
    }

    val savingText = buildAnnotatedString {
        append(stringResource(R.string.register_success_saving_prefix))
        withStyle(
            SpanStyle(
                color = NeveraTheme.colors.primaryNormal,
                fontWeight = FontWeight.Bold,
            )
        ) {
            append(formattedAmount)
        }
        append(stringResource(R.string.register_success_saving_suffix))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeveraTheme.colors.backgroundPrimary),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            // 닫기 버튼
            IconButton(
                onClick = onClose,
                modifier = Modifier.padding(NeveraTheme.spacing.padding4),
            ) {
                Icon(
                    painter = NeveraIcons.Close,
                    contentDescription = stringResource(R.string.register_success_close_description),
                    tint = NeveraTheme.colors.iconPrimary,
                )
            }

            Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap16))

            // 타이틀
            Column(
                modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding20),
            ) {
                Text(
                    text = stringResource(R.string.register_success_title_line1),
                    style = NeveraTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = NeveraTheme.colors.textPrimary,
                )
                Text(
                    text = stringResource(R.string.register_success_title_line2),
                    style = NeveraTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = NeveraTheme.colors.textPrimary,
                )
                Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap8))
                Text(
                    text = savingText,
                    style = NeveraTheme.typography.bodyMedium,
                    color = NeveraTheme.colors.textSecondary,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.img_success),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit,
            )

            Spacer(modifier = Modifier.weight(1f))

            // 버튼 영역 확보 spacer
            Spacer(modifier = Modifier.height(80.dp))
        }

        // 하단 고정 버튼
        NeveraFilledButton(
            label = stringResource(R.string.register_success_button),
            onClick = onViewFridge,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(
                    horizontal = NeveraTheme.spacing.padding16,
                    vertical = NeveraTheme.spacing.padding16,
                ),
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
private fun RegisterSuccessScreenPreview() {
    NeveraTheme {
        RegisterSuccessScreen(
            totalSavedAmount = 9_000,
            onViewFridge = {},
            onClose = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
private fun RegisterSuccessScreenLargeAmountPreview() {
    NeveraTheme {
        // 긴 금액 텍스트 줄바꿈 확인
        RegisterSuccessScreen(
            totalSavedAmount = 9_999_999,
            onViewFridge = {},
            onClose = {},
        )
    }
}
