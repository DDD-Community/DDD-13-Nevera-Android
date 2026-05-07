package com.anddd.nevera.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * Firebase Messaging 공통 의존성만 제공한다.
 */
class NeveraFirebasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            // Crashlytics와 google-services는 app plugin 책임으로 분리한다.
            dependencies {
                "implementation"(platform(libs.findLibrary("firebase-bom").get()))
                "implementation"(libs.findLibrary("firebase-messaging").get())
            }
        }
    }
}
