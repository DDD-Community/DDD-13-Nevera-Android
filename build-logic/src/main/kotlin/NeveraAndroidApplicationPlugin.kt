import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class NeveraAndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply("com.android.application")
            pluginManager.apply("com.google.gms.google-services")
            pluginManager.apply("com.google.firebase.crashlytics")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("nevera.android.hilt")
            pluginManager.apply("nevera.test.android")

            configure<ApplicationExtension> {
                defaultConfig {
                    minSdk = 30
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                buildFeatures {
                    compose = true
                    buildConfig = true
                }
            }

            configure<KotlinAndroidProjectExtension> {
                jvmToolchain(17)
            }

            dependencies {
                val bom = libs.findLibrary("androidx-compose-bom").get()
                "implementation"(platform(bom))
                "debugImplementation"(libs.findLibrary("androidx-compose-ui-tooling").get())
                "debugImplementation"(libs.findLibrary("androidx-compose-ui-test-manifest").get())
                "androidTestImplementation"(platform(bom))
                "androidTestImplementation"(libs.findLibrary("androidx-compose-ui-test-junit4").get())
            }
        }
    }
}
