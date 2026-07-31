package com.anddd.nevera.core.designsystem.component.bottomsheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.anddd.nevera.core.designsystem.test.setNeveraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
class NeveraIllustrationBottomSheetTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `일러스트와 제목과 버튼을 표시한다`() {
        composeRule.setNeveraContent {
            NeveraIllustrationBottomSheet(
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                illustration = { Text("일러스트") },
                title = "환영해요",
                subtitle = "식재료를 구조해요",
                primaryLabel = "시작",
                onPrimaryClick = {},
                onDismissRequest = {},
            )
        }

        composeRule.onNodeWithText("일러스트").assertIsDisplayed()
        composeRule.onNodeWithText("환영해요").assertIsDisplayed()
        composeRule.onNodeWithText("식재료를 구조해요").assertIsDisplayed()
        composeRule.onNodeWithText("시작").assertIsDisplayed()
    }

    @Test
    fun `primary 버튼을 누르면 primary 콜백을 호출한다`() {
        var primaryCount = 0
        composeRule.setNeveraContent {
            NeveraIllustrationBottomSheet(
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                illustration = { Text("일러스트") },
                title = "환영해요",
                subtitle = "식재료를 구조해요",
                primaryLabel = "시작",
                onPrimaryClick = { primaryCount++ },
                onDismissRequest = {},
            )
        }

        composeRule.onNodeWithText("시작").performClick()

        assertEquals(1, primaryCount)
    }

    @Test
    fun `ghost 버튼 정보가 모두 있으면 ghost 버튼을 표시하고 클릭한다`() {
        var ghostCount = 0
        composeRule.setNeveraContent {
            NeveraIllustrationBottomSheet(
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                illustration = { Text("일러스트") },
                title = "환영해요",
                subtitle = "식재료를 구조해요",
                primaryLabel = "시작",
                onPrimaryClick = {},
                ghostLabel = "건너뛰기",
                onGhostClick = { ghostCount++ },
                onDismissRequest = {},
            )
        }

        composeRule.onNodeWithText("건너뛰기").performClick()

        assertEquals(1, ghostCount)
    }

    @Test
    fun `Row 레이아웃이어도 ghost 정보가 없으면 primary 버튼만 표시한다`() {
        composeRule.setNeveraContent {
            NeveraIllustrationBottomSheet(
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                illustration = { Text("일러스트") },
                title = "환영해요",
                subtitle = "식재료를 구조해요",
                primaryLabel = "시작",
                onPrimaryClick = {},
                actionLayout = NeveraIllustrationActionLayout.Row,
                onDismissRequest = {},
            )
        }

        composeRule.onNodeWithText("시작").assertIsDisplayed()
        composeRule.onNodeWithText("건너뛰기").assertDoesNotExist()
    }
}
