package com.anddd.nevera.core.designsystem.component.button

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.test.setNeveraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NeveraIconButtonTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `아이콘 버튼을 누르면 클릭 콜백을 호출한다`() {
        var filledClickCount = 0
        var outlinedClickCount = 0
        var ghostClickCount = 0
        var weakClickCount = 0
        composeRule.setNeveraContent {
            Column {
                NeveraFilledIconButton(
                    painter = NeveraIcons.Plus,
                    contentDescription = "Filled 추가",
                    onClick = { filledClickCount++ },
                )
                NeveraOutlinedIconButton(
                    painter = NeveraIcons.Plus,
                    contentDescription = "Outlined 추가",
                    onClick = { outlinedClickCount++ },
                )
                NeveraGhostIconButton(
                    painter = NeveraIcons.Plus,
                    contentDescription = "Ghost 추가",
                    onClick = { ghostClickCount++ },
                )
                NeveraWeakIconButton(
                    painter = NeveraIcons.Plus,
                    contentDescription = "Weak 추가",
                    onClick = { weakClickCount++ },
                )
            }
        }

        composeRule.onNodeWithContentDescription("Filled 추가").performClick()
        composeRule.onNodeWithContentDescription("Outlined 추가").performClick()
        composeRule.onNodeWithContentDescription("Ghost 추가").performClick()
        composeRule.onNodeWithContentDescription("Weak 추가").performClick()

        assertEquals("Filled icon click count", 1, filledClickCount)
        assertEquals("Outlined icon click count", 1, outlinedClickCount)
        assertEquals("Ghost icon click count", 1, ghostClickCount)
        assertEquals("Weak icon click count", 1, weakClickCount)
    }

    @Test
    fun `disabled 아이콘 버튼은 비활성 상태다`() {
        composeRule.setNeveraContent {
            NeveraFilledIconButton(
                painter = NeveraIcons.Plus,
                contentDescription = "비활성 추가",
                onClick = {},
                enabled = false,
            )
        }

        composeRule.onNodeWithContentDescription("비활성 추가").assertIsNotEnabled()
    }
}
