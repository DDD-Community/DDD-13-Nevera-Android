package com.anddd.nevera.feature.ingredient.main.component.ocrscanning.internal

import android.net.Uri
import timber.log.Timber
import android.view.TextureView
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

// ──────────────────────────────────────────────────────────────
// 반복 모드
// ──────────────────────────────────────────────────────────────

internal enum class OcrScanningRepeatMode {
    /** 반복 없음 */
    OFF,

    /** 현재 항목 반복 재생 */
    ONE,

    /** 전체 반복 재생 */
    ALL,
}

// ──────────────────────────────────────────────────────────────
// OcrScanningVideo — URI 오버로드 (primary)
// ──────────────────────────────────────────────────────────────

/**
 * ExoPlayer 기반 동영상 반복 재생 컴포넌트 (URI 버전)
 *
 * - 내부적으로 [TextureView]를 사용해 Dialog 내에서도 정상 렌더링됩니다.
 *   (SurfaceView는 Dialog의 별도 Window 레이어 문제로 검정 화면이 발생함)
 * - Preview 환경에서는 ExoPlayer를 초기화하지 않고 placeholder를 표시합니다.
 *
 * @param uri 재생할 영상 URI
 * @param modifier 크기·위치 외부 주입
 * @param repeatMode 반복 모드 (기본값: [OcrScanningRepeatMode.ONE])
 * @param playWhenReady 준비 즉시 자동 재생 여부 (기본값: true)
 */
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
internal fun OcrScanningVideo(
    uri: Uri,
    modifier: Modifier = Modifier,
    repeatMode: OcrScanningRepeatMode = OcrScanningRepeatMode.ONE,
    playWhenReady: Boolean = true,
) {
    val isInspectionMode = LocalInspectionMode.current

    if (isInspectionMode) {
        // Preview 환경 — placeholder 표시
        Box(
            modifier = modifier.background(
                color = NeveraTheme.colors.surfaceTertiary,
                shape = RoundedCornerShape(NeveraTheme.radius.medium),
            ),
        )
    } else {
        val context = LocalContext.current

        val player = remember(uri, repeatMode, playWhenReady) {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(uri))
                // playWhenReady·repeatMode를 prepare() 전에 설정해야
                // 버퍼링 완료 즉시 재생이 시작됨
                this.repeatMode = repeatMode.toExoRepeatMode()
                this.playWhenReady = playWhenReady
                prepare()
                // 재생 실패 시 Logcat에 원인 출력 (디버깅용)
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        Timber.e(error, "재생 실패: uri=$uri")
                    }
                })
            }
        }

        DisposableEffect(player) {
            onDispose {
                // clearVideoSurface() 제거 — release()가 내부적으로 surface 정리를 포함하며
                // 별도 호출 시 비동기 MediaCodec 이벤트가 dead thread로 전달되는 경고 발생
                player.release()
            }
        }

        // SurfaceView → TextureView: Dialog 내부 검정 화면 방지
        // key(player): URI 변경으로 player 교체 시 AndroidView를 재생성해
        //              factory에서 setVideoTextureView를 단 한 번만 호출하도록 보장
        key(player) {
            AndroidView(
                factory = { ctx ->
                    TextureView(ctx).also { textureView ->
                        player.setVideoTextureView(textureView)
                    }
                },
                modifier = modifier,
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────
// OcrScanningVideo — Raw 리소스 오버로드
// ──────────────────────────────────────────────────────────────

/**
 * ExoPlayer 기반 동영상 반복 재생 컴포넌트 (Raw 리소스 버전)
 *
 * 내부적으로 `android.resource://` URI를 생성한 뒤 URI 오버로드에 위임합니다.
 *
 * @param videoResId `res/raw/`의 mp4 리소스 ID (예: `R.raw.illust_loading`)
 * @param modifier 크기·위치 외부 주입
 * @param repeatMode 반복 모드 (기본값: [OcrScanningRepeatMode.ONE])
 * @param playWhenReady 준비 즉시 자동 재생 여부 (기본값: true)
 */
@Composable
internal fun OcrScanningVideo(
    @RawRes videoResId: Int,
    modifier: Modifier = Modifier,
    repeatMode: OcrScanningRepeatMode = OcrScanningRepeatMode.ONE,
    playWhenReady: Boolean = true,
) {
    val context = LocalContext.current
    val uri = remember(videoResId) {
        "android.resource://${context.packageName}/$videoResId".toUri()
    }
    OcrScanningVideo(
        uri = uri,
        modifier = modifier,
        repeatMode = repeatMode,
        playWhenReady = playWhenReady,
    )
}

// ──────────────────────────────────────────────────────────────
// Internal — enum → ExoPlayer 상수 매핑
// ──────────────────────────────────────────────────────────────

private fun OcrScanningRepeatMode.toExoRepeatMode(): Int = when (this) {
    OcrScanningRepeatMode.OFF -> Player.REPEAT_MODE_OFF
    OcrScanningRepeatMode.ONE -> Player.REPEAT_MODE_ONE
    OcrScanningRepeatMode.ALL -> Player.REPEAT_MODE_ALL
}

// ──────────────────────────────────────────────────────────────
// Preview
// ──────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun OcrScanningVideoPreview() {
    NeveraTheme {
        // Preview에서는 placeholder가 표시됩니다
        OcrScanningVideo(
            uri = Uri.EMPTY,
            modifier = Modifier.size(160.dp),
        )
    }
}
