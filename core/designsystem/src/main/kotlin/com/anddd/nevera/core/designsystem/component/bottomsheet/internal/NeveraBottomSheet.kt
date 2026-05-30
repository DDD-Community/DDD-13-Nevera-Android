package com.anddd.nevera.core.designsystem.component.bottomsheet.internal

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme


/**
 * BottomSheet의 상태 소유권은 호출부에 둡니다.
 * 기본 상태 생성 편의가 필요해지면 이 컴포넌트 내부에 숨기지 말고
 * rememberNeveraBottomSheetState(...) 같은 별도 helper API로 제공해야 합니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NeveraBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier,
    shape: Shape = NeveraBottomSheetDefaults.ContainerShape,
    dragHandle: @Composable (() -> Unit)? = { NeveraBottomSheetDefaultThumb() },
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = shape,
        containerColor = NeveraTheme.colors.surfacePrimary,
        dragHandle = dragHandle,
        properties = properties,
        content = content,
    )
}
