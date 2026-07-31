package com.anddd.nevera.core.designsystem.component.toggle

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.anddd.nevera.core.designsystem.test.setNeveraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NeveraSwitchTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `꺼진 Switch를 누르면 true를 전달한다`() {
        var changedValue: Boolean? = null
        composeRule.setNeveraContent {
            NeveraSwitch(
                checked = false,
                onCheckedChange = { changedValue = it },
                modifier = Modifier.testTag(SwitchTag),
            )
        }

        composeRule.onNodeWithTag(SwitchTag).performClick()

        assertEquals(true, changedValue)
    }

    @Test
    fun `켜진 Switch를 누르면 false를 전달한다`() {
        var changedValue: Boolean? = null
        composeRule.setNeveraContent {
            NeveraSwitch(
                checked = true,
                onCheckedChange = { changedValue = it },
                modifier = Modifier.testTag(SwitchTag),
            )
        }

        composeRule.onNodeWithTag(SwitchTag).performClick()

        assertEquals(false, changedValue)
    }

    @Test
    fun `Switch checked 상태를 semantics로 노출한다`() {
        composeRule.setNeveraContent {
            NeveraSwitch(
                checked = true,
                onCheckedChange = {},
                modifier = Modifier.testTag(SwitchTag),
            )
        }

        composeRule.onNodeWithTag(SwitchTag).assertIsOn()
    }

    @Test
    fun `Switch unchecked 상태를 semantics로 노출한다`() {
        composeRule.setNeveraContent {
            NeveraSwitch(
                checked = false,
                onCheckedChange = {},
                modifier = Modifier.testTag(SwitchTag),
            )
        }

        composeRule.onNodeWithTag(SwitchTag).assertIsOff()
    }

    @Test
    fun `disabled Switch는 비활성 상태이고 눌러도 콜백을 호출하지 않는다`() {
        var changedValue: Boolean? = null
        composeRule.setNeveraContent {
            NeveraSwitch(
                checked = false,
                onCheckedChange = { changedValue = it },
                modifier = Modifier.testTag(SwitchTag),
                enabled = false,
            )
        }

        composeRule.onNodeWithTag(SwitchTag).assertIsNotEnabled()

        composeRule.onNodeWithTag(SwitchTag).performClick()

        assertEquals(null, changedValue)
        composeRule.onNodeWithTag(SwitchTag).assertIsOff()
    }
}

private const val SwitchTag = "switch"
