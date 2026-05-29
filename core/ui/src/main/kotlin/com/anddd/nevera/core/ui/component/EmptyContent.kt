package com.anddd.nevera.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

private val EmptyIconSize = 64.dp

@Composable
fun EmptyContent(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap16, Alignment.CenterVertically),
    ) {
        Image(
            painter = NeveraIcons.EmptyStateWarning,
            contentDescription = null,
            modifier = Modifier.size(EmptyIconSize),
        )
        Text(
            text = message,
            style = NeveraTheme.typography.titleSmall,
            color = NeveraTheme.colors.textCaption,
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun EmptyContentPreview() {
    NeveraTheme {
        EmptyContent(
            message = "아직 등록한 항목이 없어요",
            modifier = Modifier.padding(vertical = 40.dp),
        )
    }
}
