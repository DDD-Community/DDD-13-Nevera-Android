package com.anddd.nevera.core.designsystem.ui.theme.shadow

import android.graphics.BlurMaskFilter
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

/**
 * [NeveraShadow] 토큰에 정의된 다중 레이어 드롭 섀도우를 컴포저블에 적용하는 Modifier 확장.
 *
 * Compose의 기본 `shadow()`는 단일 레이어만 지원하므로, Canvas에 직접 그리는 방식으로
 * 여러 ShadowLayer를 순서대로 아래에 쌓아 피그마 스펙과 동일한 다중 레이어 효과를 구현한다.
 *
 * @param layers 적용할 [ShadowLayer] 목록. 기본값은 [NeveraShadow.default] (그림자 없음).
 * @param cornerRadius 섀도우 모서리 반경. 컨테이너의 shape와 동일한 값을 전달해야 한다.
 *
 * **주의사항**
 * - Android의 [BlurMaskFilter]는 하드웨어 가속 레이어에서만 정상 동작한다.
 *   `graphicsLayer { clip = false }`로 컨텐츠가 경계 밖으로 그려질 수 있도록 클리핑을 해제한다.
 * - 섀도우는 컨테이너 **뒤**에 그려지므로 컨텐츠를 가리지 않는다.
 */
fun Modifier.neveraShadow(
    layers: List<ShadowLayer> = NeveraTheme.shadow.default,
    cornerRadius: Dp,
): Modifier = composed {
    // composed { } + remember(layers): layers가 바뀔 때만 Paint 객체를 재생성한다.
    // drawBehind는 매 프레임 호출되므로, 블록 내부에서 Paint()를 직접 생성하면 GC 압박이 발생한다.
    // color는 layers 변경 시에만 바뀌므로 여기서 미리 지정하고, blur는 draw 시점에 적용한다.
    val paints = remember(layers) {
        layers.map { layer ->
            Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = layer.color.toArgb()
                }
            }
        }
    }

    // clip = false: 섀도우가 컴포저블 경계 밖으로 번지도록 클리핑을 해제
    this.graphicsLayer { clip = false }
        .drawBehind {
            val radius = cornerRadius.toPx()
            layers.forEachIndexed { index, layer ->
                val blurPx = layer.blur.toPx()
                // BlurMaskFilter는 radius > 0 이어야 하며, 0 이하 전달 시 IllegalArgumentException 발생
                if (blurPx > 0f) {
                    paints[index].asFrameworkPaint().maskFilter = BlurMaskFilter(
                        blurPx,
                        BlurMaskFilter.Blur.NORMAL
                    )
                }
                drawIntoCanvas { canvas ->
                    // top/bottom에 offsetY를 더해 섀도우를 수직으로 이동
                    canvas.drawRoundRect(
                        left = 0f,
                        top = layer.offsetY.toPx(),
                        right = size.width,
                        bottom = size.height + layer.offsetY.toPx(),
                        radiusX = radius,
                        radiusY = radius,
                        paint = paints[index],
                    )
                }
            }
        }
}