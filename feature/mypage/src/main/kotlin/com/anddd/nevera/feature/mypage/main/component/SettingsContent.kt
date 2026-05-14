package com.anddd.nevera.feature.mypage.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.mypage.R as MyPageR
import com.anddd.nevera.feature.mypage.main.model.SettingItem
import com.anddd.nevera.feature.mypage.main.model.iconRes
import com.anddd.nevera.feature.mypage.main.model.labelRes
import com.anddd.nevera.core.designsystem.R as DesignSystemR

@Composable
internal fun SettingsContent(
    settingItems: List<SettingItem>,
    modifier: Modifier = Modifier,
    onClick: (SettingItem) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = NeveraTheme.spacing.padding16)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = NeveraTheme.spacing.padding16),
            text = stringResource(MyPageR.string.mypage_settings_header),
            style = NeveraTheme.typography.bodyMedium,
            color = NeveraTheme.colors.textTertiary
        )

        Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap8))

        Column(modifier = Modifier.fillMaxWidth()) {
            settingItems.forEach { item ->
                SettingItem(
                    item = item,
                    onClick = { onClick(item) }
                )
            }
        }
    }
}

@Composable
private fun SettingItem(
    modifier: Modifier = Modifier,
    item: SettingItem,
    onClick: () -> Unit,
) {
    val label = stringResource(item.labelRes())

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                vertical = NeveraTheme.spacing.padding12,
                horizontal = NeveraTheme.spacing.padding16
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(NeveraTheme.iconSize.medium),
            painter = painterResource(item.iconRes()),
            contentDescription = label
        )

        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap12))

        Text(
            text = label,
            style = NeveraTheme.typography.bodyLarge,
            color = NeveraTheme.colors.textSecondary
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
    name = "SettingsContent",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun SettingsContentPreview() {
    NeveraTheme {
        SettingsContent(
            settingItems = listOf(
                SettingItem.Notification,
                SettingItem.Account,
                SettingItem.AppInfo,
            ),
            onClick = {},
        )
    }
}
