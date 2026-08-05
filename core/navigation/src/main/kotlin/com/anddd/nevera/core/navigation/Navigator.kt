package com.anddd.nevera.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * feature가 화면 이동에 쓰는 좁은 통로.
 *
 * NavController를 그대로 넘기지 않는 이유는 권한 범위 때문이다.
 * NavController를 주면 feature가 popUpTo·clearBackStack 등 전역 백스택을
 * 무엇이든 조작할 수 있고, 그러면 "어떤 화면에서 나가면 스택이 어떻게 되는가"라는
 * 정책이 여덟 개 모듈에 흩어진다.
 *
 * 이 래퍼는 백스택 조작을 **이름 붙은 정책**으로만 노출한다.
 * 새 정책이 필요하면 여기에 이름을 붙여 추가하고, 호출부에서 옵션을 조립하지 않는다.
 *
 * :app의 조립 지점에서 remember(navController) { Navigator(navController) } 로 한 번만 만든다.
 * Compose 의존성을 두지 않으려고 @Composable 팩토리를 제공하지 않는다.
 */
class Navigator(
    @PublishedApi internal val navController: NavController,
) {

    /** 목적지로 이동한다. 같은 화면이 연타로 중복 생성되지 않는다. */
    fun navigate(destination: Any) {
        navController.navigate(destination) { launchSingleTop = true }
    }

    /** 이전 화면으로 돌아간다. 돌아갈 곳이 없으면 아무 일도 하지 않는다. */
    fun goBack() {
        navController.popBackStack()
    }

    /**
     * 되돌아갈 수 없는 전이. [clearUpTo]까지의 흐름 전체를 대체한다.
     *
     * 로그인 완료 후 홈으로 가기처럼, 이전 흐름 자체가 끝난 경우에 쓴다.
     */
    fun replaceFlow(destination: Any, clearUpTo: Any) {
        navController.navigate(destination) {
            popUpTo(clearUpTo) { inclusive = true }
            launchSingleTop = true
        }
    }

    /**
     * :app이 정책을 직접 표현해야 하는 예외 상황용.
     *
     * feature 모듈에서는 쓰지 않는다. 바텀 탭 전환처럼 조립 지점만 아는 정책에 쓴다.
     */
    fun navigateWithOptions(destination: Any, builder: NavOptionsBuilder.() -> Unit) {
        navController.navigate(destination, builder)
    }
}

/**
 * 여러 단계로 이어지는 흐름에서, 현재 단계 [T]를 [destination]으로 **교체**한다.
 *
 * 뒤로가기로 [T]에 돌아갈 수 없게 된다. 촬영 → 인식결과처럼 이전 단계로 되돌아가는 것이
 * 의미 없는 전이에 쓴다.
 *
 * [T]를 인스턴스가 아니라 타입으로 지정하는 이유는, `OcrCaptureRoute(openGallery = true)`처럼
 * 인자가 달라도 같은 단계로 취급해야 하기 때문이다. 인스턴스로 지정하면 인자가 다른 항목이
 * 스택에 남는다.
 */
inline fun <reified T : Any> Navigator.replaceStep(destination: Any) {
    navController.navigate(destination) {
        popUpTo<T> { inclusive = true }
        launchSingleTop = true
    }
}
