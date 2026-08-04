package com.anddd.nevera.feature.ingredient.registersuccess.component.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R

/**
 * 등록 완료 화면 타이틀 영역
 *
 * @param formattedAmount 절약 금액 포맷된 문자열 (예: "9,000원")
 * @param modifier        외부 Modifier
 */
@Composable
internal fun RegisterSuccessTitle(
    formattedAmount: String,
    modifier: Modifier = Modifier,
) {
    val savingText = buildAnnotatedString {
        append(stringResource(R.string.register_success_saving_prefix))
        withStyle(
            SpanStyle(
                color = NeveraTheme.colors.primaryNormal,
                fontWeight = FontWeight.Normal,
            )
        ) {
            append(formattedAmount)
        }
        append(stringResource(R.string.register_success_saving_suffix))
    }

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.register_success_title),
            style = NeveraTheme.typography.headlineMedium,
            color = NeveraTheme.colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap6))
        Text(
            text = savingText,
            style = NeveraTheme.typography.bodyMedium,
            color = NeveraTheme.colors.textQuaternary,
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
private fun RegisterSuccessTitlePreview() {
    NeveraTheme {
        RegisterSuccessTitle(
            formattedAmount = "9,000원",
        )
    }
}

@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
private fun RegisterSuccessTitleLargeAmountPreview() {
    NeveraTheme {
        RegisterSuccessTitle(
            formattedAmount = "9,999,999원",
        )
    }
}
