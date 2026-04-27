package com.anddd.nevera.core.designsystem.component.appbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

@Composable
fun NeveraDisplayAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    action: AppBarAction = AppBarAction.None,
    showBackground: Boolean = true,
) {
    AppBarContainer(
        modifier = modifier,
        showBackground = showBackground,
        startPadding = AppBarDefault.horizontalSpacingLarge,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Title(title)
            AppBarActionSlot(action = action)
        }
    }
}

@Composable
private fun RowScope.Title(title: String?) {
    if (title != null) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = NeveraTheme.typography.headlineSmall,
            color = NeveraTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    } else {
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(
    name = "NeveraDisplayAppBar - Default",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraDisplayAppBarPreview() {
    NeveraTheme {
        NeveraDisplayAppBar(
            title = "타이틀",
        )
    }
}

@Preview(
    name = "NeveraDisplayAppBar - Title None Text Primary",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraDisplayAppBarTitleNoneTextPrimaryPreview() {
    NeveraTheme {
        NeveraDisplayAppBar(
            action = AppBarAction.Text(
                label = "완료",
                onClick = {},
                tone = AppBarAction.Text.Tone.Primary,
            ),
        )
    }
}

@Preview(
    name = "NeveraDisplayAppBar - Action Text Primary",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraDisplayAppBarActionTextPrimaryPreview() {
    NeveraTheme {
        NeveraDisplayAppBar(
            title = "타이틀",
            action = AppBarAction.Text(
                label = "완료",
                onClick = {},
                tone = AppBarAction.Text.Tone.Primary,
            ),
        )
    }
}

@Preview(
    name = "NeveraDisplayAppBar - Action Text Tertiary",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraDisplayAppBarActionTextTertiaryPreview() {
    NeveraTheme {
        NeveraDisplayAppBar(
            title = "타이틀",
            action = AppBarAction.Text(
                label = "건너뛰기",
                onClick = {},
                tone = AppBarAction.Text.Tone.Tertiary,
            ),
        )
    }
}

@Preview(
    name = "NeveraDisplayAppBar - Action Icons",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraDisplayAppBarActionIconsPreview() {
    NeveraTheme {
        NeveraDisplayAppBar(
            title = "타이틀",
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
    name = "NeveraDisplayAppBar - Action Icons 2",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun NeveraDisplayAppBarActionIcons2Preview() {
    NeveraTheme {
        NeveraDisplayAppBar(
            title = "타이틀",
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
    name = "NeveraDisplayAppBar - No Background",
    showBackground = false,
    widthDp = 360,
)
@Composable
private fun NeveraDisplayAppBarNoBackgroundPreview() {
    NeveraTheme {
        NeveraDisplayAppBar(
            title = "타이틀",
            showBackground = false,
        )
    }
}
