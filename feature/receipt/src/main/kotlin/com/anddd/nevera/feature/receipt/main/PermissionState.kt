package com.anddd.nevera.feature.receipt.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

data class PermissionState(
    val hasPermission: Boolean,
    val isPartialAccess: Boolean = false,
    val isDenied: Boolean,
    val requestPermission: () -> Unit,
    val clearDenied: () -> Unit,
)

@Composable
fun rememberCameraPermissionState(): PermissionState {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
        )
    }
    var isDenied by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) isDenied = true
    }

    return remember(hasPermission, isDenied) {
        PermissionState(
            hasPermission = hasPermission,
            isDenied = isDenied,
            requestPermission = { launcher.launch(Manifest.permission.CAMERA) },
            clearDenied = { isDenied = false },
        )
    }
}

@Composable
fun rememberGalleryPermissionState(): PermissionState {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(checkGalleryPermission(context)) }
    var isPartialAccess by remember { mutableStateOf(checkGalleryPartialAccess(context)) }
    var isDenied by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                val fullGranted = result[Manifest.permission.READ_MEDIA_IMAGES] == true
                val partialGranted = result[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] == true
                isPartialAccess = !fullGranted && partialGranted
                hasPermission = fullGranted || partialGranted
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                isPartialAccess = false
                hasPermission = result[Manifest.permission.READ_MEDIA_IMAGES] == true
            }
            else -> {
                isPartialAccess = false
                hasPermission = result[Manifest.permission.READ_EXTERNAL_STORAGE] == true
            }
        }
        if (!hasPermission) isDenied = true
    }

    return remember(hasPermission, isPartialAccess, isDenied) {
        PermissionState(
            hasPermission = hasPermission,
            isPartialAccess = isPartialAccess,
            isDenied = isDenied,
            requestPermission = { launcher.launch(galleryPermissions()) },
            clearDenied = { isDenied = false },
        )
    }
}

private fun checkGalleryPartialAccess(context: Context): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED

private fun checkGalleryPermission(context: Context): Boolean = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ->
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
    else ->
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
}

private fun galleryPermissions(): Array<String> = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ->
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    else ->
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
}