package com.anddd.nevera.core.designsystem.component.appbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
fun NeveraAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigation: AppBarNavigation = AppBarNavigation.None,
    action: AppBarAction = AppBarAction.None,
    showBackground: Boolean = true,
) {
    AppBarContainer(
        modifier = modifier,
        showBackground = showBackground,
    ) {
        AppBarNavigationSlot(navigation = navigation)

        title?.let { Title(it) }

        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            AppBarActionSlot(action = action)
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
            navigation = AppBarNavigation.Back(onClick = {}),
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
            navigation = AppBarNavigation.Close(onClick = {}),
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
            navigation = AppBarNavigation.Menu(onClick = {}),
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
            navigation = AppBarNavigation.Back(onClick = {}),
            action = AppBarAction.Text(
                label = "완료",
                onClick = {},
                tone = AppBarAction.Text.Tone.Primary,
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
            navigation = AppBarNavigation.Back(onClick = {}),
            action = AppBarAction.Text(
                label = "건너뛰기",
                onClick = {},
                tone = AppBarAction.Text.Tone.Tertiary,
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
            navigation = AppBarNavigation.Back(onClick = {}),
            action = AppBarAction.Icons(
                AppBarAction.Icons.Item(
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
            navigation = AppBarNavigation.Back(onClick = {}),
            action = AppBarAction.Icons(
                AppBarAction.Icons.Item(
                    painter = NeveraIcons.Search,
                    contentDescription = "검색",
                    onClick = {},
                ),
                AppBarAction.Icons.Item(
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
            navigation = AppBarNavigation.Back(onClick = {}),
            showBackground = false,
        )
    }
}
