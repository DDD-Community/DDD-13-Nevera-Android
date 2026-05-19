package com.anddd.nevera.feature.receipt.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.receipt.R

private val ShutterButtonSize = 60.dp
private val ControlButtonBackground = Color(0x66000000)

@Composable
internal fun CameraControls(
    onSwitchToGallery: () -> Unit,
    onTakePicture: () -> Unit,
    onSwapCamera: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .background(Color.Black)
            .navigationBarsPadding()
            .padding(
                top = NeveraTheme.spacing.padding16,
                bottom = NeveraTheme.spacing.padding24
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NeveraTheme.spacing.gap16),
    ) {
        Text(
            text = stringResource(R.string.receipt_hint_line1) + "\n" +
                stringResource(R.string.receipt_hint_line2),
            style = NeveraTheme.typography.bodySmall,
            color = Color.White,
            textAlign = TextAlign.Center,
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(
                    top = NeveraTheme.spacing.padding24,
                    start = NeveraTheme.spacing.padding32,
                    end = NeveraTheme.spacing.padding32
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onSwitchToGallery,
                modifier = Modifier.clip(CircleShape)
                    .background(ControlButtonBackground),
            ) {
                Icon(
                    painter = NeveraIcons.ReceiptGallery,
                    contentDescription = "갤러리",
                    tint = Color.White,
                    modifier = Modifier.size(NeveraTheme.iconSize.xxLarge),
                )
            }

            Box(
                modifier = Modifier
                    .size(ShutterButtonSize)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onTakePicture),
            )

            IconButton(
                onClick = onSwapCamera,
                modifier = Modifier.clip(CircleShape)
                    .background(ControlButtonBackground),
            ) {
                Icon(
                    painter = NeveraIcons.ReceiptCameraSwap,
                    contentDescription = "카메라 전환",
                    tint = Color.White,
                    modifier = Modifier.size(NeveraTheme.iconSize.xxLarge),
                )
            }
        }
    }
}

@Preview(widthDp = 360, showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CameraControlsPreview() {
    NeveraTheme {
        CameraControls(
            onSwitchToGallery = {},
            onTakePicture = {},
            onSwapCamera = {},
        )
    }
}
