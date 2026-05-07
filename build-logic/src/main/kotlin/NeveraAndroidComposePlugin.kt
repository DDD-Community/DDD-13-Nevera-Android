package com.anddd.nevera.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Compose UI를 쓰는 Android library 모듈용 확장 plugin.
 * application 모듈은 NeveraAndroidApplicationPlugin에서 configureCompose()를 직접 호출.
 */
class NeveraAndroidComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("nevera.android.library")
            configureCompose()
        }
    }
}
