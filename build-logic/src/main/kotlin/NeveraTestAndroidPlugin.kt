import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class NeveraTestAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply("nevera.test.unit")

            pluginManager.withPlugin("com.android.library") {
                configure<LibraryExtension> {
                    testOptions {
                        unitTests.all { it.useJUnitPlatform() }
                    }
                }
            }
            pluginManager.withPlugin("com.android.application") {
                configure<ApplicationExtension> {
                    testOptions {
                        unitTests.all { it.useJUnitPlatform() }
                    }
                }
            }

            dependencies {
                "androidTestImplementation"(libs.findLibrary("androidx-junit").get())
                "androidTestImplementation"(libs.findLibrary("androidx-espresso-core").get())
            }
        }
    }
}
