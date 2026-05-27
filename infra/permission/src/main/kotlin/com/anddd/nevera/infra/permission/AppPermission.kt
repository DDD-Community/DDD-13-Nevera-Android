package com.anddd.nevera.infra.permission

import android.Manifest
import android.os.Build

sealed class AppPermission(
    val manifestPermission: String,
    val minSdk: Int = Build.VERSION_CODES.BASE,
) {
    data object Notification : AppPermission(
        manifestPermission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU,
    )

    data object Camera : AppPermission(
        manifestPermission = Manifest.permission.CAMERA,
    )
}
