package com.anddd.nevera.feature.receipt.gallery.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.receipt.main.component.PermissionDeniedDialog
import com.anddd.nevera.feature.receipt.main.model.ReceiptIntent

@Composable
internal fun ReceiptGalleryContent(
    images: List<Uri>,
    hasGalleryPermission: Boolean,
    showPermissionDialog: Boolean,
    onIntent: (ReceiptIntent) -> Unit,
    onDismissPermissionDialog: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayItems: List<Uri?> = images.ifEmpty {
        List(GALLERY_PLACEHOLDER_COUNT) { null }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier.fillMaxSize()
                .background(Color.White),
        ) {
            items(displayItems) { uri ->
                GalleryImageCell(
                    uri = uri,
                    onClick = { onIntent(ReceiptIntent.SelectImage(it)) },
                )
            }
        }

        if (!hasGalleryPermission) {
            // 터치 이벤트 소비: 오버레이 없이 background만 적용하면 탭이 하단 그리드로 통과됨
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {},
                    ),
            )
        }

        if (showPermissionDialog) {
            PermissionDeniedDialog(
                onDismiss = onDismissPermissionDialog,
                onOpenSettings = onOpenSettings,
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 720)
@Composable
private fun ReceiptGalleryContentPreview() {
    NeveraTheme {
        ReceiptGalleryContent(
            images = emptyList(),
            hasGalleryPermission = true,
            showPermissionDialog = false,
            onIntent = {},
            onDismissPermissionDialog = {},
            onOpenSettings = {},
        )
    }
}