package com.anddd.nevera.core.designsystem.component.appbar

/**
 * AppBar 좌측 내비게이션 영역에 표시할 요소를 정의합니다.
 *
 * 화면의 이동 맥락에 따라 뒤로가기, 닫기, 메뉴 또는 미표시 상태를 선택할 때 사용합니다.
 */
sealed interface NeveraAppBarNavigation {
    /** 이전 화면으로 이동하는 뒤로가기 버튼을 표시합니다. */
    data class Back(val onClick: () -> Unit) : NeveraAppBarNavigation

    /** 현재 화면이나 플로우를 종료하는 닫기 버튼을 표시합니다. */
    data class Close(val onClick: () -> Unit) : NeveraAppBarNavigation

    /** 좌측에 메뉴 버튼을 표시합니다. */
    data class Menu(val onClick: () -> Unit) : NeveraAppBarNavigation

    /** 좌측 내비게이션 요소를 표시하지 않습니다. */
    data object None : NeveraAppBarNavigation
}
