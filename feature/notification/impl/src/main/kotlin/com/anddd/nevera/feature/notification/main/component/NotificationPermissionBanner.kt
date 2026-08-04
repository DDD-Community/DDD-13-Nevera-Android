package com.anddd.nevera.feature.notification.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.notification.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height

private val EnableNotificationButtonVerticalPadding = 5.dp

@Composable
internal fun NotificationPermissionBanner(
    onClickEnable: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(NeveraTheme.radius.medium))
            .background(NeveraTheme.colors.surfaceTertiary)
            .padding(NeveraTheme.spacing.padding16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.notification_permission_banner_title),
                style = NeveraTheme.typography.titleSmall,
                color = NeveraTheme.colors.textTertiary,
            )

            Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap2))

            Text(
                text = stringResource(R.string.notification_permission_banner_subtitle),
                style = NeveraTheme.typography.captionLarge,
                color = NeveraTheme.colors.textQuaternary,
            )
        }

        Text(
            text = stringResource(R.string.notification_permission_banner_button),
            style = NeveraTheme.typography.captionStrong,
            color = NeveraTheme.colors.textInverse,
            modifier = Modifier
                .clip(RoundedCornerShape(NeveraTheme.radius.max))
                .background(NeveraTheme.colors.secondaryNormal)
                .clickable(onClick = onClickEnable)
                .padding(
                    horizontal = NeveraTheme.spacing.padding6,
                    vertical = EnableNotificationButtonVerticalPadding,
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationPermissionBannerPreview() {
    NeveraTheme {
        NotificationPermissionBanner(
            onClickEnable = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
