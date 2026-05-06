package com.anddd.nevera.core.designsystem.component.bottomsheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anddd.nevera.core.designsystem.component.bottomsheet.internal.NeveraBottomSheet
import com.anddd.nevera.core.designsystem.component.confirm.internal.ConfirmActionButtons
import com.anddd.nevera.core.designsystem.component.confirm.internal.ConfirmTitleContent

/**
 * 제목, 설명, 확인/취소 액션을 가진 BottomSheet 조합 컴포넌트입니다.
 * 상태 소유권은 NeveraBottomSheet와 동일하게 호출부에 둡니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeveraConfirmBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    title: String,
    subtitle: String,
    positive: String,
    negative: String,
    onPositive: () -> Unit,
    onNegative: () -> Unit,
) {
    NeveraBottomSheet(
        onDismissRequest = onNegative,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        ConfirmTitleContent(title = title, subtitle = subtitle)
        ConfirmActionButtons(
            positive = positive,
            negative = negative,
            onPositive = onPositive,
            onNegative = onNegative,
        )
    }
}
