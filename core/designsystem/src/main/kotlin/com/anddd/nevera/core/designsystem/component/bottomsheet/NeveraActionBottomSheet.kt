package com.anddd.nevera.core.designsystem.component.bottomsheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.bottomsheet.internal.NeveraBottomSheet
import com.anddd.nevera.core.designsystem.component.button.NeveraFilledButton
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

/**
 * 제목 + 커스텀 content 슬롯 + 단일 확인 버튼을 가진 BottomSheet 조합 컴포넌트입니다.
 *
 * 카테고리 선택, 보관 방법 선택 등 목록 기반 단일 선택 시트에 사용합니다.
 * content 내부 아이템은 좌우 여백(padding20)을 직접 지정합니다.
 * 상태 소유권은 호출부에 둡니다.
 *
 * @param sheetState      호출부에서 관리하는 시트 상태
 * @param title           시트 상단 제목
 * @param confirmLabel    확인 버튼 레이블
 * @param onConfirm       확인 버튼 탭 콜백
 * @param onDismissRequest 외부 탭/스와이프 닫기 콜백
 * @param content         제목과 확인 버튼 사이에 배치되는 커스텀 콘텐츠 슬롯
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeveraActionBottomSheet(
    sheetState: SheetState,
    title: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
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
                .heightIn(min = 27.dp)
                .padding(
                    start = NeveraTheme.spacing.padding20,
                    top = NeveraTheme.spacing.padding20,
                    end = NeveraTheme.spacing.padding20,
                    bottom = NeveraTheme.spacing.padding16,
                ),
        )

        content()

        NeveraFilledButton(
            label = confirmLabel,
            onClick = onConfirm,
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .padding(NeveraTheme.spacing.padding16),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360)
@Composable
private fun NeveraActionBottomSheetPreview() {
    NeveraTheme {
        NeveraActionBottomSheet(
            sheetState = rememberModalBottomSheetState(),
            title = "카테고리",
            confirmLabel = "확인",
            onConfirm = {},
            onDismissRequest = {},
        ) {}
    }
}
