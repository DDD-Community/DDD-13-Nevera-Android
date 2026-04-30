package com.anddd.nevera.core.designsystem.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.anddd.nevera.core.designsystem.R
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

object NeveraIcons {
    val ArrowBack: Painter
        @Composable get() = painterResource(R.drawable.ic_arrow_back)
    val Close: Painter
        @Composable get() = painterResource(R.drawable.ic_close)
    val Info: Painter
        @Composable get() = painterResource(R.drawable.ic_info)
    val Menu: Painter
        @Composable get() = painterResource(R.drawable.ic_menu)
    val Search: Painter
        @Composable get() = painterResource(R.drawable.ic_search)
    val LogoAppBar: Painter
        @Composable get() = ColorPainter(NeveraTheme.colors.primaryNormal)
}
