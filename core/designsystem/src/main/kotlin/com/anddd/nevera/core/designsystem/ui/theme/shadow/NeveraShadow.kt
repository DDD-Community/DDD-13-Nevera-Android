package com.anddd.nevera.core.designsystem.ui.theme.shadow

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ShadowLayer(
    val offsetY: Dp,
    val blur: Dp,
    val color: Color,
)

// TODO :: shadow color 중 ColorPalette에 포함 안된 색상을 사용하고 있음, 문의 이후 수정 필요
object NeveraShadow {

    val default: List<ShadowLayer> = listOf()

    val small: List<ShadowLayer> = listOf(
        ShadowLayer(offsetY = 6.dp,  blur = 12.dp, color = Color(0xFF52555B).copy(alpha = 0.02f)),
        ShadowLayer(offsetY = 2.dp,  blur = 6.dp, color = Color(0xFF52555B).copy(alpha = 0.03f)),
        ShadowLayer(offsetY = 0.dp, blur = 4.dp, color = Color(0xFF52555B).copy(alpha = 0.04f)),
    )

    val medium: List<ShadowLayer> = listOf(
        ShadowLayer(offsetY = 4.dp,  blur = 8.dp, color = Color(0xFFE6E8EA).copy(alpha = 0.24f)),
        ShadowLayer(offsetY = 2.dp,  blur = 16.dp, color = Color(0xFF6D7882).copy(alpha = 0.08f)),
    )

    val large: List<ShadowLayer> = listOf(
        ShadowLayer(offsetY = 2.dp,  blur = 20.dp, color = Color(0xFF52555B).copy(alpha = 0.08f)),
        ShadowLayer(offsetY = 6.dp,  blur = 50.dp, color = Color(0xFF52555B).copy(alpha = 0.04f)),
        ShadowLayer(offsetY = 16.dp, blur = 80.dp, color = Color(0xFF52555B).copy(alpha = 0.02f)),
    )
}
