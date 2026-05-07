package com.anddd.nevera.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class NeveraFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply("nevera.android.compose")
            pluginManager.apply("nevera.android.hilt")

            configure<LibraryExtension> {
                buildFeatures { buildConfig = true }
            }

            dependencies {
                "implementation"(project(":core:common"))
                "implementation"(project(":core:designsystem"))
                "implementation"(project(":core:ui"))
                "implementation"(project(":domain"))

                "implementation"(libs.findLibrary("lifecycle-viewmodel-compose").get())
                "implementation"(libs.findLibrary("hilt-navigation-compose").get())
                "implementation"(libs.findLibrary("coil-compose").get())
                "implementation"(libs.findLibrary("coil-network-okhttp").get())
                "implementation"(libs.findLibrary("timber").get())
            }
        }
    }
}
