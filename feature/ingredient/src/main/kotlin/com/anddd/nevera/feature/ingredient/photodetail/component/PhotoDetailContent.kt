package com.anddd.nevera.feature.ingredient.photodetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBar
import com.anddd.nevera.core.designsystem.component.appbar.NeveraAppBarNavigation
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.photodetail.model.PhotoDetailUiState

// AppBar 높이: AppBarDefault.height = 56.dp (design system internal 상수와 동기화)
private val AppBarHeight = 56.dp

/**
 * 영수증 스캔 이미지 전체화면 상세 콘텐츠
 *
 * - 이미지가 edge-to-edge로 전체화면을 채웁니다.
 * - 상단에 상태바 + AppBar 높이만큼 검정→투명 그라디언트가 오버레이됩니다.
 * - [NeveraAppBar]를 투명 배경으로 이미지 위에 오버레이합니다.
 */
@Composable
internal fun PhotoDetailContent(
    uiState: PhotoDetailUiState,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 확대/이동 제스처 상태 — 리셋되어도 비즈니스 영향이 없는 순수 UI 상태
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        // 레이어 1: 전체화면 이미지 (edge-to-edge, ContentScale.Fit)
        // 이미지 잘림 없이 화면 내 최대 크기로 표시
        if (uiState.imageUri != null) {
            AsyncImage(
                model = uiState.imageUri.toUri(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y,
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(1f, 5f)
                            val scaleDelta = newScale / scale
                            offset = Offset(
                                x = offset.x * scaleDelta + pan.x,
                                y = offset.y * scaleDelta + pan.y,
                            )
                            scale = newScale
                            if (scale == 1f) offset = Offset.Zero
                        }
                    },
                contentScale = ContentScale.Fit,
            )
        }

        // 레이어 2: 상단 그라디언트 오버레이
        TopGradientOverlay()

        // 레이어 3: NeveraAppBar — 투명 배경으로 이미지 위 오버레이
        // AppBarContainer 내부에서 statusBarsPadding() 처리
        NeveraAppBar(
            navigation = NeveraAppBarNavigation.Close(onClick = onCloseClick),
            showBackground = false,
        )
    }
}

// ─── Private Components ───────────────────────────────────────────────────────

/**
 * 상태바 + AppBar 높이만큼 검정→투명 수직 그라디언트를 렌더링합니다.
 * Figma: 선형 그라디언트 alpha/black80(#000000 80%) → #000000 0%, 위→아래
 */
@Composable
private fun TopGradientOverlay() {
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(statusBarTop + AppBarHeight)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.8f),
                        Color.Transparent,
                    ),
                ),
            ),
    )
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(
    name = "PhotoDetailContent - 이미지 없음",
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 360,
    heightDp = 800,
)
@Composable
private fun PhotoDetailContentNoImagePreview() {
    NeveraTheme {
        PhotoDetailContent(
            uiState = PhotoDetailUiState(imageUri = null),
            onCloseClick = {},
        )
    }
}

@Preview(
    name = "TopGradientOverlay",
    showBackground = true,
    widthDp = 360,
    heightDp = 120,
)
@Composable
private fun TopGradientOverlayPreview() {
    NeveraTheme {
        TopGradientOverlay()
    }
}
