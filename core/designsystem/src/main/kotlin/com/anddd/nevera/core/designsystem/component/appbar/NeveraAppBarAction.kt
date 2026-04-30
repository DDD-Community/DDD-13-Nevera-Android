package com.anddd.nevera.core.designsystem.component.appbar

import androidx.compose.ui.graphics.painter.Painter

/**
 * AppBar 우측 액션 영역에 표시할 요소를 정의합니다.
 *
 * 아이콘 액션, 텍스트 액션, 액션 없음 상태를 타입으로 구분해서 전달할 때 사용합니다.
 */
sealed interface NeveraAppBarAction {
    /** 우측 액션을 표시하지 않습니다. */
    data object None : NeveraAppBarAction

    /**
     * 아이콘 액션을 표시합니다.
     *
     * 최소 1개, 최대 2개의 아이콘을 지원합니다.
     */
    class Icons internal constructor(val items: List<Item>) : NeveraAppBarAction {
        init {
            require(items.size in 1..2) { "NeveraAppBarAction.Icons는 최소 1개, 최대 2개까지 지원합니다." }
        }

        /** 개별 아이콘 액션의 표시 정보와 클릭 동작을 정의합니다. */
        data class Item(
            val painter: Painter,
            val contentDescription: String? = null,
            val onClick: () -> Unit,
        )

        companion object {
            fun of(item: Item): Icons = Icons(listOf(item))
            fun of(first: Item, second: Item): Icons = Icons(listOf(first, second))
        }
    }

    /**
     * 텍스트 버튼 형태의 액션을 표시합니다.
     *
     * 버튼 라벨과 클릭 동작, 강조 정도를 함께 전달할 수 있습니다.
     */
    data class Text(
        val label: String,
        val onClick: () -> Unit,
        val tone: Tone = Tone.Primary,
    ) : NeveraAppBarAction {
        /** 텍스트 액션의 시각적 강조 강도를 정의합니다. */
        enum class Tone { Primary, Tertiary, }
    }
}
