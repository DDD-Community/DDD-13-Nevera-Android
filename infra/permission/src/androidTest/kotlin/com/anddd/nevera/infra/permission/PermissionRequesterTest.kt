package com.anddd.nevera.infra.permission

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PermissionRequesterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `compose되면 즉시 content가 표시된다`() {
        composeTestRule.setContent {
            PermissionRequester(
                permission = AppPermission.Notification,
                onGranted = {},
                onDenied = {},
            ) { _, _ ->
                Text(text = "content", modifier = Modifier.testTag("content"))
            }
        }

        composeTestRule.onNodeWithTag("content").assertIsDisplayed()
    }

    @Test
    fun `content에서 dismiss하면 onDenied가 호출된다`() {
        var deniedCalled = false

        composeTestRule.setContent {
            PermissionRequester(
                permission = AppPermission.Notification,
                onGranted = {},
                onDenied = { deniedCalled = true },
            ) { _, onDismiss ->
                Button(onClick = onDismiss) { Text("닫기") }
            }
        }

        composeTestRule.onNodeWithText("닫기").performClick()
        composeTestRule.waitForIdle()
        assertTrue(deniedCalled)
    }

}
