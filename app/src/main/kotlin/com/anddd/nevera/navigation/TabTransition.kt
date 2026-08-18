package com.anddd.nevera.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay

/**
 * 탭을 옮길 때는 애니메이션 없이 곧바로 바뀐다.
 *
 * NavDisplay의 기본 전환은 700ms 페이드다. 화면을 쌓고 걷어내는 이동에는 어울리지만,
 * 탭은 나란한 관계라 오갈 때 사이를 채울 것이 없다. 누른 사람에게는 느린 흐림으로만 보인다.
 *
 * 세 방향을 모두 지정한다. 하나라도 비우면 그 방향만 기본 전환으로 남는다.
 * 탭 안에서 화면을 쌓고 걷어내는 이동은 이 값을 타지 않으므로 기본 전환 그대로다.
 */
val TabRootMetadata: Map<String, Any> = metadata {
    put(NavDisplay.TransitionKey) { NoTransition }
    put(NavDisplay.PopTransitionKey) { NoTransition }
    put(NavDisplay.PredictivePopTransitionKey) { _: Int -> NoTransition }
}

private val NoTransition = EnterTransition.None togetherWith ExitTransition.None
