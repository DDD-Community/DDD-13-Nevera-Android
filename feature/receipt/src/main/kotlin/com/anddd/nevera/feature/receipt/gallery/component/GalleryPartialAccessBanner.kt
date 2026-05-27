package com.anddd.nevera.feature.receipt.gallery.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonSize
import com.anddd.nevera.core.designsystem.component.button.NeveraOutlinedButton
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.receipt.R

private val GalleryPartialAccessBannerHeight = 58.dp

// 상단과 하단에 각각 1dp 구분선을 그린다
private fun Modifier.horizontalBorder(color: Color): Modifier = drawBehind {
    val strokeWidth = 1.dp.toPx()
    val halfStroke = strokeWidth / 2
    drawLine(
        color = color,
        start = Offset(0f, halfStroke),
        end = Offset(size.width, halfStroke),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = color,
        start = Offset(0f, size.height - halfStroke),
        end = Offset(size.width, size.height - halfStroke),
        strokeWidth = strokeWidth
    )
}

@Composable
internal fun GalleryPartialAccessBanner(
    onSelectMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .height(GalleryPartialAccessBannerHeight)
            .background(NeveraTheme.colors.surfacePrimary)
            .horizontalBorder(NeveraTheme.colors.borderStrong)
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.receipt_gallery_partial_access),
            style = NeveraTheme.typography.bodyMedium,
            color = NeveraTheme.colors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        NeveraOutlinedButton(
            label = stringResource(R.string.receipt_gallery_partial_access_select),
            onClick = onSelectMore,
            size = NeveraButtonSize.Small,
            shape = RoundedCornerShape(NeveraTheme.radius.max)
        )
    }
}

@Preview(widthDp = 360, showBackground = true)
@Composable
private fun GalleryPartialAccessBannerPreview() {
    NeveraTheme {
        GalleryPartialAccessBanner(onSelectMore = {})
    }
}