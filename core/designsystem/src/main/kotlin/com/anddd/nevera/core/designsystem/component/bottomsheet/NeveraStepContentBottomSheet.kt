package com.anddd.nevera.core.designsystem.component.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.bottomsheet.internal.NeveraBottomSheet
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonColor
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.component.button.NeveraWeakButton
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import kotlinx.coroutines.launch

/**
 * 단계 진행 표시(stepIndicator), 제목, 설명, 커스텀 content,
 * 선택적 뒤로가기 + CTA 액션을 가진 BottomSheet 조합 컴포넌트입니다.
 * backLabel이 null이면 CTA 버튼만 단독으로 표시됩니다.
 * 다단계 입력 플로우(예: 위시 생성)에 사용합니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeveraStepContentBottomSheet(
    sheetState: SheetState,
    stepIndicator: String,
    title: String,
    subtitle: String,
    ctaLabel: String,
    onCtaClick: () -> Unit,
    backLabel: String? = null,
    ctaEnabled: Boolean = true,
    onBackClick: () -> Unit = {},
    onDismissRequest: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val defaultDismissRequest: () -> Unit = { scope.launch { sheetState.hide() } }

    NeveraBottomSheet(
        onDismissRequest = onDismissRequest ?: defaultDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        NeveraStepContentBottomSheetContent(
            stepIndicator = stepIndicator,
            title = title,
            subtitle = subtitle,
            backLabel = backLabel,
            ctaLabel = ctaLabel,
            ctaEnabled = ctaEnabled,
            onBackClick = onBackClick,
            onCtaClick = onCtaClick,
            content = content,
        )
    }
}

@Composable
private fun NeveraStepContentBottomSheetContent(
    stepIndicator: String,
    title: String,
    subtitle: String,
    ctaLabel: String,
    onCtaClick: () -> Unit,
    backLabel: String? = null,
    ctaEnabled: Boolean = true,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = NeveraTheme.spacing.padding20,
                    top = NeveraTheme.spacing.padding20,
                    end = NeveraTheme.spacing.padding20,
                ),
        ) {
            Text(
                text = stepIndicator,
                color = NeveraTheme.colors.primaryNormal,
                style = NeveraTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap4))
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 27.dp),
                color = NeveraTheme.colors.textSecondary,
                style = NeveraTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap4))
            Text(
                text = subtitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 21.dp),
                color = NeveraTheme.colors.textQuaternary,
                style = NeveraTheme.typography.bodySmall,
            )
        }
        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap24))
        Column(
            modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding20),
            content = content,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NeveraTheme.spacing.padding16),
            horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap8),
        ) {
            if (backLabel != null) {
                NeveraWeakButton(
                    label = backLabel,
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f),
                    color = NeveraButtonColor.Secondary,
                )
            }
            NeveraFilledButton(
                label = ctaLabel,
                onClick = onCtaClick,
                modifier = if (backLabel != null) Modifier.weight(1f) else Modifier.fillMaxWidth(),
                color = NeveraButtonColor.Primary,
                enabled = ctaEnabled,
            )
        }
    }
}

@Preview(name = "Step1 - cta 비활성", showBackground = true, widthDp = 360)
@Composable
private fun NeveraStepContentBottomSheetStep1DisabledPreview() {
    NeveraTheme {
        NeveraStepContentBottomSheetContent(
            stepIndicator = "1/2",
            title = "나만의 위시는 무엇인가요?",
            subtitle = "절약해서 이루고 싶은 걸 알려주세요",
            ctaLabel = "다음",
            ctaEnabled = false,
            onCtaClick = {},
        )
    }
}

@Preview(name = "Step1 - cta 활성", showBackground = true, widthDp = 360)
@Composable
private fun NeveraStepContentBottomSheetStep1EnabledPreview() {
    NeveraTheme {
        NeveraStepContentBottomSheetContent(
            stepIndicator = "1/2",
            title = "나만의 위시는 무엇인가요?",
            subtitle = "절약해서 이루고 싶은 걸 알려주세요",
            ctaLabel = "다음",
            ctaEnabled = true,
            onCtaClick = {},
        )
    }
}

@Preview(name = "Step2 - cta 비활성", showBackground = true, widthDp = 360)
@Composable
private fun NeveraStepContentBottomSheetStep2DisabledPreview() {
    NeveraTheme {
        NeveraStepContentBottomSheetContent(
            stepIndicator = "2/2",
            title = "얼마나 모을까요?",
            subtitle = "위시 달성을 위한 목표 절약 금액을 정해요",
            backLabel = "이전",
            ctaLabel = "위시 만들기",
            ctaEnabled = false,
            onBackClick = {},
            onCtaClick = {},
        )
    }
}

@Preview(name = "Step2 - cta 활성", showBackground = true, widthDp = 360)
@Composable
private fun NeveraStepContentBottomSheetStep2EnabledPreview() {
    NeveraTheme {
        NeveraStepContentBottomSheetContent(
            stepIndicator = "2/2",
            title = "얼마나 모을까요?",
            subtitle = "위시 달성을 위한 목표 절약 금액을 정해요",
            backLabel = "이전",
            ctaLabel = "위시 만들기",
            ctaEnabled = true,
            onBackClick = {},
            onCtaClick = {},
        )
    }
}
