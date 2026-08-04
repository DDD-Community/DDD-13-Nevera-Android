package com.anddd.nevera.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * feature api 모듈용 플러그인.
 *
 * api 모듈은 다른 feature가 목적지로 삼을 Route 클래스만 담는다.
 * Compose·Hilt·Android 의존성을 의도적으로 넣지 않는다.
 * 순수 Kotlin 모듈이라 @Composable을 쓰면 컴파일되지 않으므로,
 * 이 모듈이 UI 잡동사니 저장소가 되는 것을 컴파일러가 막는다.
 */
class NeveraFeatureApiPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply("nevera.kotlin.jvm")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            dependencies {
                "implementation"(libs.findLibrary("kotlinx-serialization-json").get())
            }
        }
    }
}
