import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class NeveraAndroidComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply("nevera.android.library")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }
            }

            dependencies {
                val bom = libs.findLibrary("androidx-compose-bom").get()
                "implementation"(platform(bom))
                "implementation"(libs.findLibrary("androidx-compose-ui").get())
                "implementation"(libs.findLibrary("androidx-compose-material3").get())
                "debugImplementation"(libs.findLibrary("androidx-compose-ui-tooling").get())
                "debugImplementation"(libs.findLibrary("androidx-compose-ui-test-manifest").get())
                "androidTestImplementation"(platform(bom))
                "androidTestImplementation"(libs.findLibrary("androidx-compose-ui-test-junit4").get())
            }
        }
    }
}
