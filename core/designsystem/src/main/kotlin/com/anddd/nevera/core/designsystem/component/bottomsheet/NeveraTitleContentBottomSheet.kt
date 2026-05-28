package com.anddd.nevera.core.designsystem.component.bottomsheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.bottomsheet.internal.NeveraBottomSheet
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import kotlinx.coroutines.launch

/**
 * 제목과 커스텀 content 슬롯을 가진 범용 BottomSheet 조합 컴포넌트입니다.
 *
 * 하단 버튼·액션을 포함하지 않으므로 content 슬롯에서 자유롭게 구성합니다.
 * content 람다는 `onDismiss`를 파라미터로 받아 시트를 애니메이션과 함께 닫을 수 있습니다.
 * 상태 소유권은 호출부에 둡니다.
 *
 * @param sheetState       호출부에서 관리하는 시트 상태
 * @param title            시트 상단 제목
 * @param onDismissRequest 외부 탭/스와이프 또는 dismiss 완료 후 호출되는 콜백
 * @param content          제목 아래에 배치되는 커스텀 콘텐츠 슬롯.
 *                         `onDismiss`를 통해 시트를 애니메이션과 함께 닫을 수 있음
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeveraTitleContentBottomSheet(
    sheetState: SheetState,
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(onDismiss: () -> Unit) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val dismiss: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { cause ->
            if (cause == null && !sheetState.isVisible) onDismissRequest()
        }
    }

    NeveraBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Text(
            text = title,
            style = NeveraTheme.typography.titleLarge,
            color = NeveraTheme.colors.textSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = NeveraTheme.spacing.padding20,
                    top = NeveraTheme.spacing.padding24,
                    end = NeveraTheme.spacing.padding20,
                    bottom = NeveraTheme.spacing.padding16,
                ),
        )

        content(dismiss)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360)
@Composable
private fun NeveraTitleContentBottomSheetPreview() {
    NeveraTheme {
        NeveraTitleContentBottomSheet(
            sheetState = rememberModalBottomSheetState(),
            title = "어떻게 식재료를 등록할까요?",
            onDismissRequest = {},
        ) { _ ->
            Text(
                text = "content 슬롯",
                style = NeveraTheme.typography.bodyMedium,
                color = NeveraTheme.colors.textSecondary,
                modifier = Modifier.padding(
                    horizontal = NeveraTheme.spacing.padding20,
                    vertical = NeveraTheme.spacing.padding16,
                ),
            )
        }
    }
}
