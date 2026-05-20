package com.anddd.nevera.core.designsystem.component.appbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

/**
 * 브랜드 로고를 제목 대신 표시하는 AppBar를 구성합니다.
 *
 * 좌측에는 로고를 배치하고, 우측에는 필요한 action만 노출합니다.
 * 메인 화면이나 브랜드 식별이 중요한 화면 상단에 사용합니다.
 *
 * @param modifier 루트 AppBar에 적용할 [Modifier]
 * @param action 우측에 표시할 액션 요소입니다.
 * @param showBackground AppBar 배경 표시 여부입니다.
 */
@Composable
fun NeveraLogoAppBar(
    modifier: Modifier = Modifier,
    action: NeveraAppBarAction = NeveraAppBarAction.None,
    showBackground: Boolean = true,
) {
    AppBarContainer(
        modifier = modifier,
        showBackground = showBackground,
        startPadding = AppBarDefault.horizontalSpacingLarge,
    ) {
        Image(
            painter = NeveraIcons.AppBarLogo,
            contentDescription = "로고",
            modifier = Modifier.size(
                width = AppBarDefault.logoWidth,
                height = AppBarDefault.logoHeight,
            ),
            contentScale = ContentScale.Fit,
            alignment = Alignment.CenterStart,
        )
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            AppBarActionSlot(action)
        }
    }
}

@Preview(
    name = "NeveraLogoAppBar - Default",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraLogoAppBarPreview() {
    NeveraTheme {
        NeveraLogoAppBar()
    }
}

@Preview(
    name = "NeveraLogoAppBar - Action Text Primary",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraLogoAppBarActionTextPrimaryPreview() {
    NeveraTheme {
        NeveraLogoAppBar(
            action = NeveraAppBarAction.Text(
                label = "완료",
                onClick = {},
                tone = NeveraAppBarAction.Text.Tone.Primary,
            ),
        )
    }
}

@Preview(
    name = "NeveraLogoAppBar - Action Text Tertiary",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraLogoAppBarActionTextTertiaryPreview() {
    NeveraTheme {
        NeveraLogoAppBar(
            action = NeveraAppBarAction.Text(
                label = "건너뛰기",
                onClick = {},
                tone = NeveraAppBarAction.Text.Tone.Tertiary,
            ),
        )
    }
}

@Preview(
    name = "NeveraLogoAppBar - Action Icons",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraLogoAppBarActionIconsPreview() {
    NeveraTheme {
        NeveraLogoAppBar(
            action = NeveraAppBarAction.Icons.of(
                NeveraAppBarAction.Icons.Item(
                    painter = NeveraIcons.Search,
                    contentDescription = "검색",
                    onClick = {},
                ),
            ),
        )
    }
}

@Preview(
    name = "NeveraLogoAppBar - No Background",
    showBackground = false,
    widthDp = 360,
)
@Composable
private fun NeveraLogoAppBarNoBackgroundPreview() {
    NeveraTheme {
        NeveraLogoAppBar(
            showBackground = false,
        )
    }
}
