package com.anddd.nevera.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureCompose() {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

    // library·application 양쪽 컨텍스트에서 호출되므로, 실제 적용된 Android plugin에 해당하는
    // extension만 configure하기 위해 withPlugin 콜백을 사용한다.
    pluginManager.withPlugin("com.android.library") {
        configure<LibraryExtension> { buildFeatures { compose = true } }
    }
    pluginManager.withPlugin("com.android.application") {
        configure<ApplicationExtension> { buildFeatures { compose = true } }
    }

    dependencies {
        val bom = libs.findLibrary("androidx-compose-bom").get()
        "implementation"(platform(bom))
        "implementation"(libs.findLibrary("androidx-compose-ui").get())
        "implementation"(libs.findLibrary("androidx-compose-ui-graphics").get())
        "implementation"(libs.findLibrary("androidx-compose-material3").get())
        "implementation"(libs.findLibrary("androidx-compose-ui-tooling-preview").get())
        "debugImplementation"(libs.findLibrary("androidx-compose-ui-tooling").get())
        "debugImplementation"(libs.findLibrary("androidx-compose-ui-test-manifest").get())
        "androidTestImplementation"(platform(bom))
        "androidTestImplementation"(libs.findLibrary("androidx-compose-ui-test-junit4").get())
    }
}
