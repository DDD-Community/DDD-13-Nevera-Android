package com.anddd.nevera.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

/**
 * JUnit5 공통 의존성만 제공하는 가장 작은 테스트 관심사 plugin.
 */
class NeveraTestUnitPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            tasks.withType<Test>().configureEach {
                useJUnitPlatform()
            }

            dependencies {
                "testImplementation"(libs.findLibrary("junit-jupiter").get())
                "testRuntimeOnly"(libs.findLibrary("junit-jupiter-engine").get())
                "testRuntimeOnly"(libs.findLibrary("junit-platform-launcher").get())
            }
        }
    }
}
