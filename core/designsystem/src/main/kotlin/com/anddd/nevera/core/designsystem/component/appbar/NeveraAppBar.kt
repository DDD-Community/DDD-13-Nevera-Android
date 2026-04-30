package com.anddd.nevera.core.designsystem.component.appbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

/**
 * 가장 기본적인 형태의 Nevera AppBar를 표시합니다.
 *
 * 중앙에 제목을 배치하고, 좌측에는 navigation, 우측에는 action 영역을 구성합니다.
 * 일반적인 상세 화면이나 설정 화면의 상단 바에 사용합니다.
 *
 * @param modifier 루트 AppBar에 적용할 [Modifier]
 * @param title 가운데에 표시할 제목. `null`이면 제목을 표시하지 않습니다.
 * @param navigation 좌측에 표시할 내비게이션 요소입니다.
 * @param action 우측에 표시할 액션 요소입니다.
 * @param showBackground AppBar 배경 표시 여부입니다.
 */
@Composable
fun NeveraAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigation: NeveraAppBarNavigation = NeveraAppBarNavigation.None,
    action: NeveraAppBarAction = NeveraAppBarAction.None,
    showBackground: Boolean = true,
) {
    AppBarContainer(
        modifier = modifier,
        showBackground = showBackground,
    ) {
        Layout(
            modifier = Modifier.fillMaxSize(),
            content = {
                Box { AppBarNavigationSlot(navigation = navigation) }
                Box { title?.let { Title(it) } }
                Box { AppBarActionSlot(action = action) }
            },
        ) { measurables, constraints ->
            val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

            val navPlaceable = measurables[0].measure(looseConstraints)
            val actionPlaceable = measurables[2].measure(looseConstraints)

            val titleHorizontalGap = AppBarDefault.titleHorizontalGap.roundToPx()
            val sideWidth = maxOf(navPlaceable.width, actionPlaceable.width) + titleHorizontalGap
            val titleConstraints = looseConstraints.copy(
                maxWidth = (constraints.maxWidth - sideWidth * 2).coerceAtLeast(0),
            )
            val titlePlaceable = measurables[1].measure(titleConstraints)

            val height = constraints.maxHeight
            val centerY = height / 2

            layout(constraints.maxWidth, height) {
                navPlaceable.placeRelative(
                    x = 0,
                    y = centerY - navPlaceable.height / 2
                )
                titlePlaceable.placeRelative(
                    x = (constraints.maxWidth - titlePlaceable.width) / 2,
                    y = centerY - titlePlaceable.height / 2,
                )
                actionPlaceable.placeRelative(
                    x = constraints.maxWidth - actionPlaceable.width,
                    y = centerY - actionPlaceable.height / 2,
                )
            }
        }
    }
}

@Composable
private fun BoxScope.Title(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .align(Alignment.Center),
        style = NeveraTheme.typography.titleSmall,
        color = NeveraTheme.colors.textPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Preview(
    name = "NeveraAppBar - Navigation None",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraAppBarNavigationNonePreview() {
    NeveraTheme {
        NeveraAppBar(
            title = "타이틀",
        )
    }
}

@Preview(
    name = "NeveraAppBar - Navigation Back",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraAppBarNavigationBackPreview() {
    NeveraTheme {
        NeveraAppBar(
            title = "타이틀",
            navigation = NeveraAppBarNavigation.Back(onClick = {}),
        )
    }
}

@Preview(
    name = "NeveraAppBar - Navigation Close",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraAppBarNavigationClosePreview() {
    NeveraTheme {
        NeveraAppBar(
            title = "타이틀",
            navigation = NeveraAppBarNavigation.Close(onClick = {}),
        )
    }
}

@Preview(
    name = "NeveraAppBar - Navigation Menu",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraAppBarNavigationMenuPreview() {
    NeveraTheme {
        NeveraAppBar(
            title = "타이틀",
            navigation = NeveraAppBarNavigation.Menu(onClick = {}),
        )
    }
}

@Preview(
    name = "NeveraAppBar - Action Text Primary",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraAppBarActionTextPrimaryPreview() {
    NeveraTheme {
        NeveraAppBar(
            title = "타이틀",
            navigation = NeveraAppBarNavigation.Back(onClick = {}),
            action = NeveraAppBarAction.Text(
                label = "완료",
                onClick = {},
                tone = NeveraAppBarAction.Text.Tone.Primary,
            ),
        )
    }
}

@Preview(
    name = "NeveraAppBar - Action Text Tertiary",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraAppBarActionTextTertiaryPreview() {
    NeveraTheme {
        NeveraAppBar(
            title = "타이틀",
            navigation = NeveraAppBarNavigation.Back(onClick = {}),
            action = NeveraAppBarAction.Text(
                label = "건너뛰기",
                onClick = {},
                tone = NeveraAppBarAction.Text.Tone.Tertiary,
            ),
        )
    }
}

@Preview(
    name = "NeveraAppBar - Long Title Action Text",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraAppBarLongTitleActionTextPreview() {
    NeveraTheme {
        NeveraAppBar(
            title = "아주 긴 제목 텍스트가 들어왔을 때의 모습 확인",
            navigation = NeveraAppBarNavigation.Back(onClick = {}),
            action = NeveraAppBarAction.Text(
                label = "건너뛰기",
                onClick = {},
                tone = NeveraAppBarAction.Text.Tone.Tertiary,
            ),
        )
    }
}

@Preview(
    name = "NeveraAppBar - Action Icons",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraAppBarActionIconsPreview() {
    NeveraTheme {
        NeveraAppBar(
            title = "타이틀",
            navigation = NeveraAppBarNavigation.Back(onClick = {}),
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
    name = "NeveraAppBar - Action Icons 2",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraAppBarActionIcons2Preview() {
    NeveraTheme {
        NeveraAppBar(
            title = "타이틀",
            navigation = NeveraAppBarNavigation.Back(onClick = {}),
            action = NeveraAppBarAction.Icons.of(
                NeveraAppBarAction.Icons.Item(
                    painter = NeveraIcons.Search,
                    contentDescription = "검색",
                    onClick = {},
                ),
                NeveraAppBarAction.Icons.Item(
                    painter = NeveraIcons.Info,
                    contentDescription = "정보",
                    onClick = {},
                ),
            ),
        )
    }
}

@Preview(
    name = "NeveraAppBar - No Background",
    showBackground = false,
    widthDp = 360,
)
@Composable
private fun NeveraAppBarNoBackgroundPreview() {
    NeveraTheme {
        NeveraAppBar(
            title = "타이틀",
            navigation = NeveraAppBarNavigation.Back(onClick = {}),
            showBackground = false,
        )
    }
}
