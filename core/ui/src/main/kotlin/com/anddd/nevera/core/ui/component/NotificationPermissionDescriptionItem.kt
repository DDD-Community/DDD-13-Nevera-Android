package com.anddd.nevera.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.R

@Composable
fun NotificationPermissionDescriptionItem(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(vertical = NeveraTheme.spacing.padding12),
        horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap12)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_bell),
            contentDescription = "notification_permission",
            modifier = Modifier.size(NeveraTheme.iconSize.medium)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap2)
        ) {
            Text(
                text = stringResource(R.string.notification_permission_title),
                style = NeveraTheme.typography.titleSmall,
                color = NeveraTheme.colors.textTertiary,
            )

            Text(
                text = stringResource(R.string.notification_permission_description),
                style = NeveraTheme.typography.captionLarge,
                color = NeveraTheme.colors.textQuaternary,
            )
        }
    }
}

@Preview(
    name = "NotificationPermissionDescriptionItem",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NotificationPermissionDescriptionItemPreview() {
    NeveraTheme {
        NotificationPermissionDescriptionItem(
            modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding20)
        )
    }
}