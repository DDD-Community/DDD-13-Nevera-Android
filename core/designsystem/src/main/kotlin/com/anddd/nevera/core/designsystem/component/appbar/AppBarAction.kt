package com.anddd.nevera.core.designsystem.component.appbar

import androidx.compose.ui.graphics.painter.Painter

sealed interface AppBarAction {
    data object None : AppBarAction

    class Icons(vararg val items: Item) : AppBarAction {
        init { require(items.size in 1..2) { "AppBarAction.Icons는 최대 2개까지 지원합니다." } }

        data class Item(
            val painter: Painter,
            val contentDescription: String? = null,
            val onClick: () -> Unit,
        )
    }

    data class Text(
        val label: String,
        val onClick: () -> Unit,
    ) : AppBarAction
}
