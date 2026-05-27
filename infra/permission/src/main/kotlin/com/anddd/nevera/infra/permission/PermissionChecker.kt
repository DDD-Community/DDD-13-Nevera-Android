package com.anddd.nevera.infra.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

interface PermissionChecker {
    fun isGranted(context: Context, permission: AppPermission): Boolean
    fun checkState(activity: Activity, permission: AppPermission): PermissionState
}

object DefaultPermissionChecker : PermissionChecker {

    @VisibleForTesting
    internal var sdkVersion: Int = Build.VERSION.SDK_INT

    override fun isGranted(context: Context, permission: AppPermission): Boolean {
        if (sdkVersion < permission.minSdk) return true
        return ContextCompat.checkSelfPermission(
            context,
            permission.manifestPermission,
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun checkState(activity: Activity, permission: AppPermission): PermissionState {
        if (isGranted(activity, permission)) return PermissionState.Granted
        val needsRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            permission.manifestPermission,
        )
        return if (needsRationale) PermissionState.DeniedWithRationale else PermissionState.Denied
    }
}
