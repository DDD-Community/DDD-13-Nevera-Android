package com.anddd.nevera.feature.notification.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.notification.R
import com.anddd.nevera.feature.notification.main.model.NotificationItemUiModel
import com.anddd.nevera.feature.notification.main.model.NotificationType
import java.util.concurrent.TimeUnit


@Composable
internal fun NotificationItemRow(
    item: NotificationItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (item.isRead) {
        Color.Transparent
    } else {
        NeveraTheme.colors.surfaceBrandPrimary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(NeveraTheme.spacing.padding16),
        horizontalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap12),
        verticalAlignment = Alignment.Top,
    ) {
        Image(
            painter = NeveraIcons.IllustExpirationDate,
            contentDescription = null,
            modifier = Modifier.size(NeveraTheme.iconSize.medium),
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(item.type.labelResId()),
                    style = NeveraTheme.typography.captionMedium,
                    color = NeveraTheme.colors.textSecondary,
                )
                Text(
                    text = formatRelativeTime(item.receivedAt),
                    style = NeveraTheme.typography.captionMedium,
                    color = NeveraTheme.colors.textSecondary,
                )
            }

            Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap4))

            Text(
                text = item.title,
                style = NeveraTheme.typography.bodyMedium,
                color = NeveraTheme.colors.textSecondary,
            )

            if (item.subtitle != null) {
                Spacer(modifier = Modifier.height(NeveraTheme.spacing.gap2))

                Text(
                    text = item.subtitle,
                    style = NeveraTheme.typography.captionLarge,
                    color = NeveraTheme.colors.textQuaternary,
                )
            }
        }
    }
}

@Composable
private fun NotificationType.labelResId(): Int = when (this) {
    NotificationType.EXPIRY_DATE -> R.string.notification_type_expiry_date
}

private fun formatRelativeTime(epochMillis: Long): String {
    val diffMillis = maxOf(0L, System.currentTimeMillis() - epochMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
    val days = TimeUnit.MILLISECONDS.toDays(diffMillis)
    return when {
        minutes < 60 -> "${minutes}분 전"
        hours < 24 -> "${hours}시간 전"
        else -> "${days}일 전"
    }
}

@Preview(name = "Unread", showBackground = true)
@Composable
private fun NotificationItemRowUnreadPreview() {
    NeveraTheme {
        NotificationItemRow(
            item = NotificationItemUiModel(
                id = "1",
                type = NotificationType.EXPIRY_DATE,
                title = "삼겹살(12,000)이 내일까지예요",
                subtitle = "오늘 저녁은 [제육볶음] 어떠세요?",
                receivedAt = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(59),
                isRead = false,
                deeplink = "nevera://detail/101",
            ),
            onClick = {},
        )
    }
}

@Preview(name = "Read", showBackground = true)
@Composable
private fun NotificationItemRowReadPreview() {
    NeveraTheme {
        NotificationItemRow(
            item = NotificationItemUiModel(
                id = "2",
                type = NotificationType.EXPIRY_DATE,
                title = "삼겹살(12,000)이 내일까지예요",
                subtitle = "오늘 저녁은 [제육볶음] 어떠세요?",
                receivedAt = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(23),
                isRead = true,
                deeplink = "nevera://detail/102",
            ),
            onClick = {},
        )
    }
}
