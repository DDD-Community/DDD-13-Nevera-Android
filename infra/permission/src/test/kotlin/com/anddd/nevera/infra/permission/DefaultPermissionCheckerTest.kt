package com.anddd.nevera.infra.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DefaultPermissionCheckerTest {

    private val context = mockk<Context>(relaxed = true)
    private val activity = mockk<Activity>(relaxed = true)

    @BeforeEach
    fun setUp() {
        mockkStatic(ContextCompat::class)
        mockkStatic(ActivityCompat::class)
    }

    @AfterEach
    fun tearDown() {
        try {
            unmockkAll()
        } finally {
            DefaultPermissionChecker.sdkVersion = 0
        }
    }

    // region isGranted

    @Test
    fun `SDK 버전이 minSdk 미만이면 권한 체크 없이 true를 반환한다`() {
        DefaultPermissionChecker.sdkVersion = Build.VERSION_CODES.TIRAMISU - 1

        val result = DefaultPermissionChecker.isGranted(context, AppPermission.Notification)

        assertTrue(result)
    }

    @Test
    fun `SDK 버전이 minSdk 이상이고 권한이 허용되면 true를 반환한다`() {
        DefaultPermissionChecker.sdkVersion = Build.VERSION_CODES.TIRAMISU
        every {
            ContextCompat.checkSelfPermission(context, AppPermission.Notification.manifestPermission)
        } returns PackageManager.PERMISSION_GRANTED

        val result = DefaultPermissionChecker.isGranted(context, AppPermission.Notification)

        assertTrue(result)
    }

    @Test
    fun `SDK 버전이 minSdk 이상이고 권한이 거부되면 false를 반환한다`() {
        DefaultPermissionChecker.sdkVersion = Build.VERSION_CODES.TIRAMISU
        every {
            ContextCompat.checkSelfPermission(context, AppPermission.Notification.manifestPermission)
        } returns PackageManager.PERMISSION_DENIED

        val result = DefaultPermissionChecker.isGranted(context, AppPermission.Notification)

        assertFalse(result)
    }

    // endregion

    // region checkState

    @Test
    fun `권한이 허용된 상태이면 Granted를 반환한다`() {
        DefaultPermissionChecker.sdkVersion = Build.VERSION_CODES.TIRAMISU
        every {
            ContextCompat.checkSelfPermission(activity, AppPermission.Notification.manifestPermission)
        } returns PackageManager.PERMISSION_GRANTED

        val result = DefaultPermissionChecker.checkState(activity, AppPermission.Notification)

        assertEquals(PermissionState.Granted, result)
    }

    @Test
    fun `권한이 거부되고 rationale이 필요하면 DeniedWithRationale을 반환한다`() {
        DefaultPermissionChecker.sdkVersion = Build.VERSION_CODES.TIRAMISU
        every {
            ContextCompat.checkSelfPermission(activity, AppPermission.Notification.manifestPermission)
        } returns PackageManager.PERMISSION_DENIED
        every {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, AppPermission.Notification.manifestPermission)
        } returns true

        val result = DefaultPermissionChecker.checkState(activity, AppPermission.Notification)

        assertEquals(PermissionState.DeniedWithRationale, result)
    }

    @Test
    fun `권한이 거부되고 rationale이 불필요하면 Denied를 반환한다`() {
        DefaultPermissionChecker.sdkVersion = Build.VERSION_CODES.TIRAMISU
        every {
            ContextCompat.checkSelfPermission(activity, AppPermission.Notification.manifestPermission)
        } returns PackageManager.PERMISSION_DENIED
        every {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, AppPermission.Notification.manifestPermission)
        } returns false

        val result = DefaultPermissionChecker.checkState(activity, AppPermission.Notification)

        assertEquals(PermissionState.Denied, result)
    }

    // endregion
}
