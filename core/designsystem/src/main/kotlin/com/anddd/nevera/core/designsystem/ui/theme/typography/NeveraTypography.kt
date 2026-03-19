package com.anddd.nevera.core.designsystem.ui.theme.typography

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.anddd.nevera.core.designsystem.R

private val PretendardFamily = FontFamily(
    Font(R.font.pretendard_variable, weight = FontWeight.Light),
    Font(R.font.pretendard_variable, weight = FontWeight.Normal),
    Font(R.font.pretendard_variable, weight = FontWeight.Medium),
    Font(R.font.pretendard_variable, weight = FontWeight.SemiBold),
    Font(R.font.pretendard_variable, weight = FontWeight.Bold),
)

object NeveraTypography {
    // Display — 최상위 타이틀, 히어로 영역
    val display: TextStyle = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp,
    )

    // Heading — 화면/섹션 제목
    val heading1: TextStyle = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    )

    val heading2: TextStyle = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    )

    val heading3: TextStyle = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
    )

    // Body — 본문 텍스트
    val bodyLarge: TextStyle = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    )

    val bodyMedium: TextStyle = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
    )

    val bodySmall: TextStyle = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    )

    // Label — 버튼, 탭, 입력 레이블
    val labelLarge: TextStyle = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
    )

    val labelMedium: TextStyle = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    )

    val labelSmall: TextStyle = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    )

    // Caption — 보조 설명, 타임스탬프
    val captionLarge: TextStyle = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    )

    val captionMedium: TextStyle = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    )

    val captionSmall: TextStyle = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    )
}
