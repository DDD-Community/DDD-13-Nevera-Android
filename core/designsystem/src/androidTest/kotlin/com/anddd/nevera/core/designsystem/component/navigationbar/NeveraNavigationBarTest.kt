package com.anddd.nevera.core.designsystem.component.navigationbar

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.anddd.nevera.core.designsystem.icon.NeveraIcons
import com.anddd.nevera.core.designsystem.test.setNeveraContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NeveraNavigationBarTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `탭을 누르면 해당 tag를 전달한다`() {
        var selectedTab: Tab? = null
        composeRule.setNeveraContent {
            NeveraNavigationBar(
                items = listOf(
                    NeveraNavigationBarItem(
                        tag = Tab.Home,
                        selectedIcon = NeveraIcons.NavHomeFilled,
                        unselectedIcon = NeveraIcons.NavHome,
                        selected = true,
                        contentDescription = "홈",
                    ),
                    NeveraNavigationBarItem(
                        tag = Tab.Fridge,
                        selectedIcon = NeveraIcons.NavFridgeFilled,
                        unselectedIcon = NeveraIcons.NavFridge,
                        selected = false,
                        contentDescription = "냉장고",
                    ),
                ),
                onItemClick = { selectedTab = it },
            )
        }

        composeRule.onNodeWithContentDescription("냉장고").performClick()

        assertEquals(Tab.Fridge, selectedTab)
    }
}

private enum class Tab {
    Home,
    Fridge,
}
