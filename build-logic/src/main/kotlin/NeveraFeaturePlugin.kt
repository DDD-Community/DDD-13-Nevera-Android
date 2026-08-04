package com.anddd.nevera.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
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

            // feature impl 모듈끼리의 직접 의존을 금지한다.
            // 허용하면 화면 구현이 남의 모듈로 새어 나가고, 한쪽 UI를 고칠 때
            // 무관한 모듈이 함께 재컴파일된다. 목적지 이름이 필요하면 api를 쓴다.
            afterEvaluate {
                configurations
                    .matching { it.name.endsWith("implementation", ignoreCase = true) }
                    .configureEach {
                        dependencies.withType(ProjectDependency::class.java).configureEach {
                            val target = path
                            val violates = target.startsWith(":feature:") &&
                                target.endsWith(":impl") &&
                                target != this@with.path
                            check(!violates) {
                                """
                                feature impl 모듈은 다른 feature의 impl에 의존할 수 없습니다.
                                  위반: ${this@with.path} → $target
                                  대안: ${target.removeSuffix(":impl")}:api 를 사용하세요.
                                  근거: docs/execplan-nav-b-api-impl.md
                                """.trimIndent()
                            }
                        }
                    }
            }
        }
    }
}
