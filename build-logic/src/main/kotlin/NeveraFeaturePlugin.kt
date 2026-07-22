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
            pluginManager.apply("nevera.quality")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            configure<LibraryExtension> {
                buildFeatures { buildConfig = true }
            }

            dependencies {
                "implementation"(project(":core:common"))
                "implementation"(project(":core:designsystem"))
                "implementation"(project(":core:ui"))
                "implementation"(project(":core:mvi"))
                "implementation"(project(":domain"))

                "implementation"(libs.findLibrary("lifecycle-viewmodel-compose").get())
                "implementation"(libs.findLibrary("hilt-navigation-compose").get())
                "implementation"(libs.findLibrary("coil-compose").get())
                "implementation"(libs.findLibrary("coil-network-okhttp").get())
                "implementation"(libs.findLibrary("timber").get())
                "implementation"(libs.findLibrary("kotlinx-collections-immutable").get())

                // ViewModel 단위 테스트용. orbit-test는 Turbine과 coroutines-test를 전이로
                // 가져오지만, 테스트가 runTest를 직접 호출하므로 명시적으로도 선언한다.
                "testImplementation"(libs.findLibrary("orbit-test").get())
                "testImplementation"(libs.findLibrary("coroutines-test").get())
                // ViewModel이 의존하는 UseCase는 인터페이스가 아닌 클래스라 손으로 대역을
                // 만들 수 없다. 그 아래 저장소 계약은 domain 테스트가 이미 검증한다.
                "testImplementation"(libs.findLibrary("mockk").get())
            }
        }
    }
}
