package com.anddd.nevera

import timber.log.Timber

object TimberInitializer {

    @Volatile private var initialized = false

    fun init() {
        if (initialized) return
        initialized = true
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
    }
}
