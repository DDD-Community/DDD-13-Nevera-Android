package com.anddd.nevera.feature.ingredient.main

import android.os.SystemClock
import kotlinx.coroutines.delay
import javax.inject.Inject

class OcrScanPresentationTimer @Inject constructor() {

    fun startedAt(): Long = SystemClock.elapsedRealtime()

    suspend fun delayUntilMinimumDuration(startedAt: Long) {
        val remaining = MIN_SCAN_DURATION_MS - (SystemClock.elapsedRealtime() - startedAt)
        if (remaining > 0) delay(remaining)
    }

    companion object {
        private const val MIN_SCAN_DURATION_MS = 2_000L
    }
}
