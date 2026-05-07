package com.anddd.nevera.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * Android 테스트 공통 설정과 androidTest 의존성을 모은 plugin.
 */
class NeveraTestAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            // Android 테스트는 JUnit5 단위 테스트 의존성을 함께 사용한다.
            pluginManager.apply("nevera.test.unit")

            // Compose 테스트는 별도 plugin에서 책임진다.
            dependencies {
                "androidTestImplementation"(libs.findLibrary("androidx-junit").get())
                "androidTestImplementation"(libs.findLibrary("androidx-espresso-core").get())
            }
        }
    }
}
