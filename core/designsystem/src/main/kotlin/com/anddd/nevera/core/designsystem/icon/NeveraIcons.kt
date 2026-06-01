package com.anddd.nevera.core.designsystem.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.anddd.nevera.core.designsystem.R
import com.anddd.nevera.core.designsystem.ui.theme.NeveraTheme

object NeveraIcons {
    val AppBarLogo: Painter
        @Composable get() = painterResource(R.drawable.ic_logo_appbar)
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
    val BellOn: Painter
        @Composable get() = painterResource(R.drawable.ic_bell_on)
    val LogoAppBar: Painter
        @Composable get() = ColorPainter(NeveraTheme.colors.primaryNormal)
    val Check: Painter
        @Composable get() = painterResource(R.drawable.ic_check)
    val Warning: Painter
        @Composable get() = painterResource(R.drawable.ic_warning)
    val CircleWarning: Painter
        @Composable get() = painterResource(R.drawable.ic_circle_warning)
    val Eye: Painter
        @Composable get() = painterResource(R.drawable.ic_eyes)
    val EyeOff: Painter
        @Composable get() = painterResource(R.drawable.ic_eyes_off)
    val CirclePlus: Painter
        @Composable get() = painterResource(R.drawable.ic_circle_plus)
    val NavHome: Painter
        @Composable get() = painterResource(R.drawable.ic_house)
    val NavHomeFilled: Painter
        @Composable get() = painterResource(R.drawable.ic_house_filled)
    val NavFridge: Painter
        @Composable get() = painterResource(R.drawable.ic_fridge)
    val NavFridgeFilled: Painter
        @Composable get() = painterResource(R.drawable.ic_fridge_filled)
    val NavMy: Painter
        @Composable get() = painterResource(R.drawable.ic_user)
    val NavMyFilled: Painter
        @Composable get() = painterResource(R.drawable.ic_user_filled)
    val OcrCaptureCameraWhite: Painter
        @Composable get() = painterResource(R.drawable.ic_ocr_capture_camera_white)
    val OcrCaptureCloseWhite: Painter
        @Composable get() = painterResource(R.drawable.ic_ocr_capture_close_white)
    val OcrCaptureCameraSwap: Painter
        @Composable get() = painterResource(R.drawable.ic_ocr_capture_cameraswap_white)
    val OcrCaptureGallery: Painter
        @Composable get() = painterResource(R.drawable.ic_ocr_capture_gallery)
    val ChevronSmallLeft: Painter
        @Composable get() = painterResource(R.drawable.ic_chevron_left_small)
    val ChevronSmallUp: Painter
        @Composable get() = painterResource(R.drawable.ic_chevron_up_small)
    val ChevronSmallRight: Painter
        @Composable get() = painterResource(R.drawable.ic_chevron_right_small)
    val ChevronSmallDown: Painter
        @Composable get() = painterResource(R.drawable.ic_chevron_down_small)
    val Edit: Painter
        @Composable get() = painterResource(R.drawable.ic_edit_pencil)
    val CircleHelp: Painter
        @Composable get() = painterResource(R.drawable.ic_circle_help)
    val EmptyStateWarning: Painter
        @Composable get() = painterResource(R.drawable.ic_empty_state_warning)
    val IllustLighting: Painter
        @Composable get() = painterResource(R.drawable.ic_illust_lighting)
    val IllustExpirationDate: Painter
        @Composable get() = painterResource(R.drawable.ic_illust_expiration_date)
}
