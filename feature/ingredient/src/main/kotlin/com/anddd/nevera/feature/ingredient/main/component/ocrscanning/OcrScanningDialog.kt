package com.anddd.nevera.feature.ingredient.main.component.ocrscanning

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.annotation.StringRes
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme
import com.anddd.nevera.feature.ingredient.R
import com.anddd.nevera.feature.ingredient.main.OcrScanPresentationTimer
import com.anddd.nevera.feature.ingredient.main.component.ocrscanning.internal.OcrScanningRepeatMode
import com.anddd.nevera.feature.ingredient.main.component.ocrscanning.internal.OcrScanningVideo
import com.anddd.nevera.feature.ingredient.main.model.OcrScanMessageStep
import kotlinx.coroutines.delay

// ──────────────────────────────────────────────────────────────
// 내부 상수
// ──────────────────────────────────────────────────────────────

/** OCR 스캔 일러스트 영상 표시 크기 (Figma 스펙) */
private val OcrScanningVideoSize = 160.dp

/**
 * OCR 스캔 진행 오버레이 다이얼로그
 *
 * 화면 전체에 scrim/dim 처리 후 중앙에 Card 노출합니다.
 * Back 버튼으로는 닫히지 않습니다.
 *
 * @param videoResId `res/raw/`의 mp4 리소스 ID — 반복 재생
 * @param progress OCR 진행률 (0f..1f) — 외부에서 주입
 * @param onDismiss 닫기(X) 버튼 탭 시 호출
 * @param dismissOnClickOutside Dialog 바깥 터치로 닫힘 여부 (기본값: false)
 */
@Composable
fun OcrScanningDialog(
    videoResId: Int,
    progress: Float,
    onDismiss: () -> Unit,
    dismissOnClickOutside: Boolean = false,
) {
    var messageStep by remember { mutableStateOf(OcrScanMessageStep.ReadingReceipt) }

    LaunchedEffect(Unit) {
        val steps = listOf(
            OcrScanMessageStep.RecognizingIngredients,
            OcrScanMessageStep.AnalyzingText,
            OcrScanMessageStep.ScanningCarefully,
        )
        steps.forEach { step ->
            delay(OcrScanPresentationTimer.MESSAGE_INTERVAL_MS)
            messageStep = step
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = dismissOnClickOutside,
        ),
    ) {
        Card(
            shape = RoundedCornerShape(NeveraTheme.radius.xLarge),
            colors = CardDefaults.cardColors(
                containerColor = NeveraTheme.colors.backgroundPrimary,
            ),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(
                            start = NeveraTheme.spacing.padding20,
                            top = NeveraTheme.spacing.padding20,
                            end = NeveraTheme.spacing.padding20,
                            bottom = NeveraTheme.spacing.padding32
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier.padding(
                            start = NeveraTheme.spacing.padding32,
                            top = NeveraTheme.spacing.padding32,
                            end = NeveraTheme.spacing.padding32
                        )
                    ) {

                        // mp4 반복 재생 영상
                        OcrScanningVideo(
                            videoResId = videoResId,
                            modifier = Modifier.size(OcrScanningVideoSize),
                            repeatMode = OcrScanningRepeatMode.ONE,
                        )
                    }

                    Spacer(Modifier.height(NeveraTheme.spacing.gap12))

                    Text(
                        text = stringResource(messageStep.titleResId()),
                        style = NeveraTheme.typography.titleLarge,
                        color = NeveraTheme.colors.textSecondary,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(NeveraTheme.spacing.gap16))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth()
                            .height(NeveraTheme.spacing.gap8)
                            .clip(RoundedCornerShape(NeveraTheme.radius.max)),
                        color = NeveraTheme.colors.primaryWeak,
                        trackColor = NeveraTheme.colors.surfaceSecondary,
                        drawStopIndicator = {}, // 100% 지점 끝 점 제거
                    )
                }

                // 닫기 버튼 (우측 상단) — 아이콘 20dp, top/end 여백 20dp
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd),
                ) {
                    Icon(
                        painter = NeveraIcons.Close,
                        contentDescription = stringResource(R.string.ocr_scanning_close_description),
                        tint = NeveraTheme.colors.iconSecondary,
                        modifier = Modifier.size(NeveraTheme.iconSize.small),
                    )
                }
            }
        }
    }
}

@StringRes
private fun OcrScanMessageStep.titleResId(): Int = when (this) {
    OcrScanMessageStep.ReadingReceipt -> R.string.ocr_scanning_title_reading_receipt
    OcrScanMessageStep.RecognizingIngredients -> R.string.ocr_scanning_title_recognizing_ingredients
    OcrScanMessageStep.AnalyzingText -> R.string.ocr_scanning_title_analyzing_text
    OcrScanMessageStep.ScanningCarefully -> R.string.ocr_scanning_title_scanning_carefully
}

// ──────────────────────────────────────────────────────────────
// Preview
// ──────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF888888)
@Composable
private fun OcrScanningDialogPreview() {
    NeveraTheme {
        OcrScanningDialog(
            videoResId = R.raw.illust_loading,
            progress = 0.4f,
            onDismiss = {},
        )
    }
}
