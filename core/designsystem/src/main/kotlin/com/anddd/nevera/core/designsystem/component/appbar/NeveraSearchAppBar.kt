package com.anddd.nevera.core.designsystem.component.appbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

/**
 * 검색 UI를 AppBar 내부에 배치하는 검색형 AppBar를 표시합니다.
 *
 * 좌측에는 navigation, 중앙에는 호출자가 전달한 검색 영역, 우측에는 action을 배치합니다.
 * 검색 입력 필드나 검색 상태 UI를 상단 바에 직접 노출해야 하는 화면에 사용합니다.
 *
 * @param modifier 루트 AppBar에 적용할 [Modifier]
 * @param navigation 좌측에 표시할 내비게이션 요소입니다.
 * @param action 우측에 표시할 액션 요소입니다.
 * @param showBackground AppBar 배경 표시 여부입니다.
 * @param searchBar AppBar 중앙 영역에 표시할 검색 UI 슬롯입니다.
 */
@Composable
fun NeveraSearchAppBar(
    modifier: Modifier = Modifier,
    navigation: AppBarNavigation = AppBarNavigation.None,
    action: AppBarAction = AppBarAction.None,
    showBackground: Boolean = true,
    searchBar: @Composable () -> Unit,
) {
    AppBarContainer(
        modifier = modifier,
        showBackground = showBackground,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppBarNavigationSlot(navigation = navigation)
            Box(modifier = Modifier.weight(1f)) {
                searchBar()
            }
            AppBarActionSlot(action = action)
        }
    }
}

@Preview(
    name = "NeveraSearchAppBar - Default",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraSearchAppBarPreview() {
    NeveraTheme {
        NeveraSearchAppBar(
            searchBar = { SearchBarPreviewContent() },
        )
    }
}

@Preview(
    name = "NeveraSearchAppBar - Navigation Back",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraSearchAppBarNavigationBackPreview() {
    NeveraTheme {
        NeveraSearchAppBar(
            navigation = AppBarNavigation.Back(onClick = {}),
            searchBar = { SearchBarPreviewContent() },
        )
    }
}

@Preview(
    name = "NeveraSearchAppBar - Action Icons",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraSearchAppBarActionIconsPreview() {
    NeveraTheme {
        NeveraSearchAppBar(
            navigation = AppBarNavigation.Back(onClick = {}),
            action = AppBarAction.Icons(
                AppBarAction.Icons.Item(
                    painter = NeveraIcons.Close,
                    contentDescription = "닫기",
                    onClick = {},
                ),
            ),
            searchBar = { SearchBarPreviewContent() },
        )
    }
}

@Preview(
    name = "NeveraSearchAppBar - No Background",
    showBackground = false,
    widthDp = 360,
)
@Composable
private fun NeveraSearchAppBarNoBackgroundPreview() {
    NeveraTheme {
        NeveraSearchAppBar(
            navigation = AppBarNavigation.Back(onClick = {}),
            showBackground = false,
            searchBar = { SearchBarPreviewContent() },
        )
    }
}

@Composable
private fun SearchBarPreviewContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(
                color = NeveraTheme.colors.backgroundSecondary,
                shape = RoundedCornerShape(NeveraTheme.radius.medium),
            ),
    )
}
