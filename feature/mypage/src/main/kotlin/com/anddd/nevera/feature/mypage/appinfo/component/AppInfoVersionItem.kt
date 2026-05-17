package com.anddd.nevera.feature.mypage.appinfo.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
fun AppInfoVersionItem(
    label: String,
    versionName: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(47.dp)
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = NeveraTheme.typography.titleSmall,
            color = NeveraTheme.colors.textTertiary,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = versionName,
            style = NeveraTheme.typography.bodyLarge,
            color = NeveraTheme.colors.textTertiary,
        )
    }
}

@Preview(
    name = "AppInfoVersionItem",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun AppInfoVersionItemPreview() {
    NeveraTheme {
        AppInfoVersionItem(
            label = "앱 버전",
            versionName = "V1.0",
        )
    }
}
