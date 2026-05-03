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

    // TODO :: ColorPalette 정리 후 해당 색상 참조로 변경
    private val shadowBaseColor = Color(0xFF52555B)
    private val shadowLightColor = Color(0xFFE6E8EA)
    private val shadowMidColor = Color(0xFF6D7882)

    val default: List<ShadowLayer> = listOf()

    val small: List<ShadowLayer> = listOf(
        ShadowLayer(offsetY = 6.dp,  blur = 12.dp, color = shadowBaseColor.copy(alpha = 0.02f)),
        ShadowLayer(offsetY = 2.dp,  blur = 6.dp, color = shadowBaseColor.copy(alpha = 0.03f)),
        ShadowLayer(offsetY = 0.dp, blur = 4.dp, color = shadowBaseColor.copy(alpha = 0.04f)),
    )

    val medium: List<ShadowLayer> = listOf(
        ShadowLayer(offsetY = 4.dp,  blur = 8.dp, color = shadowLightColor.copy(alpha = 0.24f)),
        ShadowLayer(offsetY = 2.dp,  blur = 16.dp, color = shadowMidColor.copy(alpha = 0.08f)),
    )

    val large: List<ShadowLayer> = listOf(
        ShadowLayer(offsetY = 2.dp,  blur = 20.dp, color = shadowBaseColor.copy(alpha = 0.08f)),
        ShadowLayer(offsetY = 6.dp,  blur = 50.dp, color = shadowBaseColor.copy(alpha = 0.04f)),
        ShadowLayer(offsetY = 16.dp, blur = 80.dp, color = shadowBaseColor.copy(alpha = 0.02f)),
    )
}
