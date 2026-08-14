package com.anddd.nevera.feature.notification.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonSize
import com.anddd.nevera.core.designsystem.component.button.NeveraOutlinedButton
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.core.ui.component.EmptyContent
import com.anddd.nevera.feature.notification.R

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
        EmptyContent(
            message = stringResource(R.string.notification_error_message),
        )
        // TODO 임시
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
