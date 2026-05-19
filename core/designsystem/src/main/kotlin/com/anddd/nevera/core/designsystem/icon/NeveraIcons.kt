package com.anddd.nevera.core.designsystem.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.anddd.nevera.core.designsystem.R
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

object NeveraIcons {
    val Logo100: Painter
        @Composable get() = painterResource(R.drawable.logo_100)
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
    val Bell: Painter
        @Composable get() = painterResource(R.drawable.ic_bell)
    val LogoAppBar: Painter
        @Composable get() = ColorPainter(NeveraTheme.colors.primaryNormal)
    val Check: Painter
        @Composable get() = painterResource(R.drawable.ic_textfield_check)
    val Warning: Painter
        @Composable get() = painterResource(R.drawable.ic_textfield_warning)
    val Eye: Painter
        @Composable get() = painterResource(R.drawable.ic_textfield_eyes)
    val EyeOff: Painter
        @Composable get() = painterResource(R.drawable.ic_textfield_eyes_off)
    val ReceiptCameraWhite: Painter
        @Composable get() = painterResource(R.drawable.ic_receipt_camera_white)
    val ReceiptCloseWhite: Painter
        @Composable get() = painterResource(R.drawable.ic_receipt_close_white)
    val ReceiptCameraSwap: Painter
        @Composable get() = painterResource(R.drawable.ic_receipt_cameraswap_white)
    val ReceiptGallery: Painter
        @Composable get() = painterResource(R.drawable.ic_receipt_gallery)
}
