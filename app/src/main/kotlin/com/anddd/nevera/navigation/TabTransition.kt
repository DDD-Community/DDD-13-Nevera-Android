package com.anddd.nevera.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.navigation3.ui.NavDisplay

/**
 * 탭을 옮길 때는 애니메이션 없이 곧바로 바뀐다.
 *
 * 탭끼리는 나란한 관계라 오가는 데 방향이 없다. NavDisplay의 기본 전환은 방향에 따라
 * 갈리는 데다 700ms가 걸려서, 탭을 누른 사람에게는 느린 페이드로만 보인다.
 *
 * 세 방향을 모두 지정한다. 하나라도 비우면 그 방향만 기본 전환으로 남는다.
 * 탭 안에서 화면을 쌓고 걷어내는 이동은 이 값을 타지 않으므로 기본 전환 그대로다.
 */
val TabRootMetadata: Map<String, Any> =
    NavDisplay.transitionSpec { NoTransition } +
        NavDisplay.popTransitionSpec { NoTransition } +
        NavDisplay.predictivePopTransitionSpec { NoTransition }

private val NoTransition = EnterTransition.None togetherWith ExitTransition.None
