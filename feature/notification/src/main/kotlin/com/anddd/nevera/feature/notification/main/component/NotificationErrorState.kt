package com.anddd.nevera.feature.notification.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.button.NeveraOutlinedButton
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonSize
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.notification.R

private val ErrorStateIconSize = 64.dp

@Composable
internal fun NotificationErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap16),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_emptystate_warning),
            contentDescription = null,
            modifier = Modifier.size(ErrorStateIconSize),
        )

        Text(
            text = stringResource(R.string.notification_error_message),
            style = NeveraTheme.typography.titleMedium,
            color = NeveraTheme.colors.textCaption,
        )

        NeveraOutlinedButton(
            label = stringResource(R.string.notification_error_retry),
            onClick = onRetry,
            size = NeveraButtonSize.Small,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationErrorStatePreview() {
    NeveraTheme {
        NotificationErrorState(onRetry = {})
    }
}
