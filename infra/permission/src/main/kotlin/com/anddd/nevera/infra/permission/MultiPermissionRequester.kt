package com.anddd.nevera.infra.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun MultiPermissionRequester(
    permissions: List<AppPermission>,
    onAllGranted: () -> Unit,
    onAnyDenied: () -> Unit,
    content: @Composable (onConfirm: () -> Unit, onDismiss: () -> Unit) -> Unit,
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var anyDenied by remember { mutableStateOf(false) }

    if (currentIndex >= permissions.size) {
        LaunchedEffect(Unit) {
            if (anyDenied) onAnyDenied() else onAllGranted()
        }
        return
    }

    key(currentIndex) {
        PermissionRequester(
            permission = permissions[currentIndex],
            onGranted = { currentIndex++ },
            onDenied = { anyDenied = true; currentIndex++ },
            content = content,
        )
    }
}
