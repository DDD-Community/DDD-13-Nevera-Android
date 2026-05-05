package com.anddd.nevera.core.designsystem.component.appbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

/**
 * AppBar의 공통 레이아웃, 높이, 배경, 가로 패딩을 적용하는 컨테이너입니다.
 *
 * 개별 AppBar 구현은 이 컨테이너 안에 navigation, title, action 같은 내용을 배치합니다.
 *
 * @param modifier 루트 컨테이너에 적용할 [Modifier]
 * @param showBackground 배경 표시 여부입니다.
 * @param startPadding 시작 지점에 적용할 가로 패딩입니다.
 * @param endPadding 끝 지점에 적용할 가로 패딩입니다.
 * @param content AppBar 내부에 배치할 콘텐츠입니다.
 */
@Composable
internal fun AppBarContainer(
    modifier: Modifier = Modifier,
    showBackground: Boolean = true,
    startPadding: Dp = AppBarDefault.horizontalSpacingMedium,
    endPadding: Dp = AppBarDefault.horizontalSpacingMedium,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(AppBarDefault.height)
            .then(
                if (showBackground) Modifier.background(NeveraTheme.colors.backgroundPrimary)
                else Modifier
            )
            .statusBarsPadding()
            .padding(start = startPadding, end = endPadding),
        contentAlignment = Alignment.CenterStart,
        content = content
    )
}
