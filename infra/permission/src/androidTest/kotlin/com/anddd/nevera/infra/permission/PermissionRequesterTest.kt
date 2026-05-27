package com.anddd.nevera.infra.permission

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.anddd.nevera.infra.permission.testutil.FakePermissionChecker
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PermissionRequesterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `권한이 허용된 상태이면 onGranted가 호출된다`() {
        var grantedCalled = false

        composeTestRule.setContent {
            PermissionRequester(
                permission = AppPermission.Notification,
                checker = FakePermissionChecker(PermissionState.Granted),
                onGranted = { grantedCalled = true },
                onDenied = {},
            ) { _, _ -> }
        }

        composeTestRule.waitForIdle()
        assertTrue(grantedCalled)
    }

    @Test
    fun `권한이 거부되고 rationale이 필요한 상태이면 rationaleContent가 표시된다`() {
        composeTestRule.setContent {
            PermissionRequester(
                permission = AppPermission.Notification,
                checker = FakePermissionChecker(PermissionState.DeniedWithRationale),
                onGranted = {},
                onDenied = {},
            ) { _, _ ->
                Text(
                    text = "rationale dialog",
                    modifier = Modifier.testTag("rationale"),
                )
            }
        }

        composeTestRule.onNodeWithTag("rationale").assertIsDisplayed()
    }

    @Test
    fun `rationale에서 dismiss하면 onDenied가 호출된다`() {
        var deniedCalled = false

        composeTestRule.setContent {
            PermissionRequester(
                permission = AppPermission.Notification,
                checker = FakePermissionChecker(PermissionState.DeniedWithRationale),
                onGranted = {},
                onDenied = { deniedCalled = true },
            ) { _, onDismiss ->
                Button(onClick = onDismiss) {
                    Text("닫기")
                }
            }
        }

        composeTestRule.onNodeWithText("닫기").performClick()
        composeTestRule.waitForIdle()
        assertTrue(deniedCalled)
    }

    @Test
    fun `rationale에서 confirm하면 rationaleContent가 사라진다`() {
        composeTestRule.setContent {
            PermissionRequester(
                permission = AppPermission.Notification,
                checker = FakePermissionChecker(PermissionState.DeniedWithRationale),
                onGranted = {},
                onDenied = {},
            ) { onConfirm, _ ->
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.testTag("confirm_btn"),
                ) {
                    Text(
                        text = "rationale dialog",
                        modifier = Modifier.testTag("rationale"),
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("rationale").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirm_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("rationale").assertIsNotDisplayed()
    }
}
