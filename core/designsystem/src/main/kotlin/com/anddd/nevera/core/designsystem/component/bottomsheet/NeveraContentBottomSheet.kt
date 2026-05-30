package com.anddd.nevera.core.designsystem.component.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.bottomsheet.internal.ContentBottomSheetHeader
import com.anddd.nevera.core.designsystem.component.bottomsheet.internal.NeveraBottomSheet
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonColor
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import kotlinx.coroutines.launch

/**
 * 제목, 설명, 커스텀 content, 단일 CTA 액션을 가진 BottomSheet 조합 컴포넌트입니다.
 * 상태 소유권은 NeveraBottomSheet와 동일하게 호출부에 둡니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeveraContentBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    title: String,
    subtitle: String,
    cta: String,
    ctaEnabled: Boolean = true,
    onCtaClick: () -> Unit,
    onDismissRequest: (() -> Unit)? = null,
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(),
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val defaultDismissRequest: () -> Unit = { scope.launch { sheetState.hide() } }
    NeveraBottomSheet(
        onDismissRequest = onDismissRequest ?: defaultDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
        properties = properties,
    ) {
        ContentBottomSheetHeader(
            title = title,
            subtitle = subtitle,
        )
        Column(
            modifier = Modifier.padding(
                horizontal = NeveraTheme.spacing.padding20
            ),
            content = content,
        )
        NeveraFilledButton(
            label = cta,
            onClick = onCtaClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(NeveraTheme.spacing.padding16),
            color = NeveraButtonColor.Primary,
            enabled = ctaEnabled,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun NeveraContentBottomSheetPreview() {
    NeveraTheme {
        NeveraContentBottomSheet(
            sheetState = rememberModalBottomSheetState(),
            title = "Title",
            subtitle = "Subtitle",
            cta = "확인",
            onCtaClick = {},
        ) {
            Text(
                text = "Content",
                style = NeveraTheme.typography.bodyMedium,
                color = NeveraTheme.colors.textSecondary,
            )
        }
    }
}
