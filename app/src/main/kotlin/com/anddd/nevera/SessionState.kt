package com.anddd.nevera

/**
 * 앱이 지금 인증 이전인지 이후인지를 나타낸다.
 *
 * 이 타입이 존재하는 이유는 화면 전이 때문이 아니라 **경로를 없애기 위해서**다.
 * 이전에는 스플래시·로그인·홈이 하나의 백스택을 공유했고, 그래서 로그인하지 않은
 * 상태로 홈이나 냉장고에 도달하는 경로가 존재했다(딥링크 콜드 스타트).
 *
 * 지금은 [Authenticated]가 아니면 메인 그래프 자체가 화면에 없다.
 * 인증 벽을 넘는 코드를 짜는 것이 불가능하다.
 */
sealed interface SessionState {

    /** 자동 로그인 확인 중. 스플래시가 보인다. */
    data object Checking : SessionState

    /** 로그인 필요. 인증 그래프만 존재한다. */
    data object Unauthenticated : SessionState

    /** 로그인 완료. 메인 그래프가 존재한다. */
    data object Authenticated : SessionState
}
