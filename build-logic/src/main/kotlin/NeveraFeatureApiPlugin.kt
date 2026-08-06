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
 * Route는 Navigation 3의 NavKey를 구현한다. navigation3-runtime은 KMP라
 * 순수 Kotlin(JVM) 모듈에서도 참조할 수 있다.
 * Compose·Hilt·Android 의존성을 의도적으로 넣지 않는다.
 * 순수 Kotlin 모듈이라 @Composable을 쓰면 컴파일되지 않으므로,
 * 이 모듈이 UI 잡동사니 저장소가 되는 것을 컴파일러가 막는다.
 */
class NeveraFeatureApiPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply("java-library")
            pluginManager.apply("nevera.kotlin.jvm")
            pluginManager.apply("nevera.quality")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            dependencies {
                // NavKey는 Route의 공개 상위 타입이므로 소비자에게도 보여야 한다.
                "api"(libs.findLibrary("navigation3-runtime").get())
                "implementation"(libs.findLibrary("kotlinx-serialization-json").get())
            }
        }
    }
}
