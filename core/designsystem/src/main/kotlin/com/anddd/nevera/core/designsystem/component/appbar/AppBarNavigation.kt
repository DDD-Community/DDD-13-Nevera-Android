package com.anddd.nevera.core.designsystem.component.appbar

sealed interface AppBarNavigation {
    data class Back(val onClick: () -> Unit) : AppBarNavigation
    data class Close(val onClick: () -> Unit) : AppBarNavigation
    data class Menu(val onClick: () -> Unit) : AppBarNavigation
    data object None : AppBarNavigation
}
