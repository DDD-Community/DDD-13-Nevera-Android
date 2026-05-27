package com.anddd.nevera.infra.permission.testutil

import android.app.Activity
import android.content.Context
import com.anddd.nevera.infra.permission.AppPermission
import com.anddd.nevera.infra.permission.PermissionChecker
import com.anddd.nevera.infra.permission.PermissionState

class FakePermissionChecker(private val state: PermissionState) : PermissionChecker {

    override fun isGranted(context: Context, permission: AppPermission): Boolean =
        state == PermissionState.Granted

    override fun checkState(activity: Activity, permission: AppPermission): PermissionState = state
}
