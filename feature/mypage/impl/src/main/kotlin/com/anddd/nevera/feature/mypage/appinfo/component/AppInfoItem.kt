package com.anddd.nevera.feature.mypage.appinfo.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.designsystem.R as DesignSystemR

@Composable
fun AppInfoItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(47.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = NeveraTheme.typography.titleSmall,
            color = NeveraTheme.colors.textTertiary,
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            modifier = Modifier.size(NeveraTheme.iconSize.xSmall),
            painter = painterResource(DesignSystemR.drawable.ic_chevron_right),
            contentDescription = null,
        )
    }
}

@Preview(
    name = "AppInfoItem",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun AppInfoItemPreview() {
    NeveraTheme {
        AppInfoItem(
            label = "식구 이용약관",
            onClick = {},
        )
    }
}
