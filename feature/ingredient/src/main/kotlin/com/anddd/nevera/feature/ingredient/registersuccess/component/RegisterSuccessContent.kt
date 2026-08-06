package com.anddd.nevera.feature.ingredient.registersuccess.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R
import com.anddd.nevera.feature.ingredient.registersuccess.component.internal.RegisterSuccessTitle
import com.anddd.nevera.feature.ingredient.registersuccess.model.RegisterSuccessUiState
import java.text.NumberFormat
import java.util.Locale

/**
 * 식재료 등록 완료 콘텐츠
 *
 * @param uiState          등록 완료 화면 상태 (절약 금액)
 * @param onViewFridgeClick "나의 냉장고 보기" 탭 콜백
 * @param onCloseClick      X 버튼 탭 콜백
 */
@Composable
internal fun RegisterSuccessContent(
    uiState: RegisterSuccessUiState,
    onViewFridgeClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val formattedAmount = remember(uiState.totalSavedAmount) {
        NumberFormat.getNumberInstance(Locale.KOREA).format(uiState.totalSavedAmount) + "원"
    }

    Box(
        modifier = modifier
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
                onClick = onCloseClick,
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
            RegisterSuccessTitle(
                formattedAmount = formattedAmount,
                modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding20),
            )

            Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap20))

            Image(
                painter = painterResource(R.drawable.img_success),
                contentDescription = null,
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit,
            )
        }

        // 하단 고정 버튼
        NeveraFilledButton(
            label = stringResource(R.string.register_success_button),
            onClick = onViewFridgeClick,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(NeveraTheme.spacing.padding16),
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
private fun RegisterSuccessContentPreview() {
    NeveraTheme {
        RegisterSuccessContent(
            uiState = RegisterSuccessUiState(totalSavedAmount = 9_000),
            onViewFridgeClick = {},
            onCloseClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
private fun RegisterSuccessContentLargeAmountPreview() {
    NeveraTheme {
        // 긴 금액 텍스트 줄바꿈 확인
        RegisterSuccessContent(
            uiState = RegisterSuccessUiState(totalSavedAmount = 9_999_999),
            onViewFridgeClick = {},
            onCloseClick = {},
        )
    }
}
