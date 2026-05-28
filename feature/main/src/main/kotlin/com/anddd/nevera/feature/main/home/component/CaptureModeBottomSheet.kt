package com.anddd.nevera.feature.main.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.component.bottomsheet.NeveraTitleContentBottomSheet
import com.anddd.nevera.core.designsystem.component.button.NeveraButtonColor
import com.anddd.nevera.core.designsystem.component.button.NeveraWeakButton
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.main.R

private val MenuItemHeight = 69.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureModeBottomSheet(
    sheetState: SheetState,
    onReceiptScan: () -> Unit,
    onOnlineCapture: () -> Unit,
    onDismiss: () -> Unit,
) {
    NeveraTitleContentBottomSheet(
        sheetState = sheetState,
        title = stringResource(R.string.home_capture_mode_title),
        onDismissRequest = onDismiss,
    ) { dismiss ->
        CaptureModeMenuItem(
            icon = painterResource(R.drawable.img_receipt),
            label = stringResource(R.string.home_capture_mode_camera),
            subtitle = stringResource(R.string.home_capture_mode_camera_subtitle),
            onClick = onReceiptScan,
        )
        CaptureModeMenuItem(
            icon = painterResource(R.drawable.img_orders),
            label = stringResource(R.string.home_capture_mode_gallery),
            subtitle = stringResource(R.string.home_capture_mode_gallery_subtitle),
            onClick = onOnlineCapture,
        )
        NeveraWeakButton(
            label = stringResource(R.string.home_capture_mode_close),
            color = NeveraButtonColor.Secondary,
            onClick = dismiss,
            modifier = Modifier
                .fillMaxWidth()
                .padding(NeveraTheme.spacing.padding16)
                .navigationBarsPadding(),
        )
    }
}

@Composable
private fun CaptureModeMenuItem(
    icon: Painter,
    label: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(MenuItemHeight)
            .clickable(onClick = onClick)
            .padding(
                horizontal = NeveraTheme.spacing.padding20,
                vertical = NeveraTheme.spacing.padding16,
            ),
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(NeveraTheme.iconSize.large),
        )
        Spacer(modifier = Modifier.width(NeveraTheme.spacing.gap12))
        Column {
            Text(
                text = label,
                style = NeveraTheme.typography.titleSmall,
                color = NeveraTheme.colors.textTertiary,
            )
            Spacer(modifier = Modifier.size(NeveraTheme.spacing.gap2))
            Text(
                text = subtitle,
                style = NeveraTheme.typography.captionLarge,
                color = NeveraTheme.colors.textQuaternary,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun CaptureModeBottomSheetPreview() {
    NeveraTheme {
        CaptureModeBottomSheet(
            sheetState = rememberModalBottomSheetState(),
            onReceiptScan = {},
            onOnlineCapture = {},
            onDismiss = {},
        )
    }
}

@Preview(
    name = "CaptureModeMenuItem",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun CaptureModeMenuItemPreview() {
    NeveraTheme {
        CaptureModeMenuItem(
            icon = painterResource(R.drawable.img_orders),
            label = "영수증 스캔",
            subtitle = "종이 영수증 촬영해요",
            onClick = {},
        )
    }
}
