import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class NeveraNetworkPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            dependencies {
                "implementation"(libs.findLibrary("retrofit").get())
                "implementation"(libs.findLibrary("retrofit-converter-gson").get())
                "implementation"(libs.findLibrary("okhttp").get())
                "implementation"(libs.findLibrary("okhttp-logging").get())
                "implementation"(libs.findLibrary("kotlinx-serialization-json").get())
            }
        }
    }
}
