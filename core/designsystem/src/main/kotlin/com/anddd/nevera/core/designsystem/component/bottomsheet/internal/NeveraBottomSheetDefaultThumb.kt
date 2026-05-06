package com.anddd.nevera.core.designsystem.component.bottomsheet.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
internal fun NeveraBottomSheetDefaultThumb(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(vertical = NeveraBottomSheetDefaults.ThumbVerticalPadding)
            .size(
                width = NeveraBottomSheetDefaults.ThumbWidth,
                height = NeveraBottomSheetDefaults.ThumbHeight,
            )
            .background(
                color = NeveraBottomSheetDefaults.ThumbColor,
                shape = NeveraBottomSheetDefaults.ThumbShape,
            ),
    )
}

@Preview(
    name = "NeveraBottomSheetDefaultThumb",
    showBackground = true,
    widthDp = 80,
    heightDp = 24
)
@Composable
private fun NeveraBottomSheetDefaultThumbPreview() {
    NeveraTheme {
        NeveraBottomSheetDefaultThumb()
    }
}
