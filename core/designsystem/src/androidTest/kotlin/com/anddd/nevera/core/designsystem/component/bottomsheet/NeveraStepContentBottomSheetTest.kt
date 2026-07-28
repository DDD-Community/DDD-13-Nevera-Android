package com.anddd.nevera.core.designsystem.component.bottomsheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.anddd.nevera.core.designsystem.test.setNeveraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
class NeveraStepContentBottomSheetTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `단계와 제목과 설명과 CTA를 표시한다`() {
        composeRule.setNeveraContent {
            NeveraStepContentBottomSheet(
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                stepIndicator = "1/2",
                title = "나만의 위시는 무엇인가요?",
                subtitle = "절약해서 이루고 싶은 걸 알려주세요",
                ctaLabel = "다음",
                onCtaClick = {},
            )
        }

        composeRule.onNodeWithText("1/2").assertIsDisplayed()
        composeRule.onNodeWithText("나만의 위시는 무엇인가요?").assertIsDisplayed()
        composeRule.onNodeWithText("절약해서 이루고 싶은 걸 알려주세요").assertIsDisplayed()
        composeRule.onNodeWithText("다음").assertIsDisplayed()
    }

    @Test
    fun `CTA가 비활성 상태이면 클릭할 수 없다`() {
        composeRule.setNeveraContent {
            NeveraStepContentBottomSheet(
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                stepIndicator = "1/2",
                title = "나만의 위시는 무엇인가요?",
                subtitle = "절약해서 이루고 싶은 걸 알려주세요",
                ctaLabel = "다음",
                ctaEnabled = false,
                onCtaClick = {},
            )
        }

        composeRule.onNode(hasText("다음") and hasClickAction()).assertIsNotEnabled()
    }

    @Test
    fun `뒤로가기 버튼을 누르면 back 콜백을 호출한다`() {
        var backCount = 0
        composeRule.setNeveraContent {
            NeveraStepContentBottomSheet(
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                stepIndicator = "2/2",
                title = "얼마나 모을까요?",
                subtitle = "목표 금액을 정해요",
                backLabel = "이전",
                ctaLabel = "완료",
                onBackClick = { backCount++ },
                onCtaClick = {},
            )
        }

        composeRule.onNodeWithText("이전").performClick()

        assertEquals(1, backCount)
    }
}
