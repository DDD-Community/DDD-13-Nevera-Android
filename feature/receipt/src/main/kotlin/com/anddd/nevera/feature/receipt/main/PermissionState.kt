package com.anddd.nevera.feature.receipt.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

data class PermissionState(
    val hasPermission: Boolean,
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

    LaunchedEffect(Unit) {
        if (!hasPermission) launcher.launch(Manifest.permission.CAMERA)
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
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permission)
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
            requestPermission = { launcher.launch(permission) },
            clearDenied = { isDenied = false },
        )
    }
}