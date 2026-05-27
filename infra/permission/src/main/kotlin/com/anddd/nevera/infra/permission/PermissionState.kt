package com.anddd.nevera.infra.permission

sealed interface PermissionState {
    data object Granted : PermissionState
    data object Denied : PermissionState
    data object DeniedWithRationale : PermissionState
}
