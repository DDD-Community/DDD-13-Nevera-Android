import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureCompose() {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

    pluginManager.withPlugin("com.android.library") {
        configure<LibraryExtension> { buildFeatures { compose = true } }
    }
    pluginManager.withPlugin("com.android.application") {
        configure<ApplicationExtension> { buildFeatures { compose = true } }
    }

    dependencies {
        val bom = libs.findLibrary("androidx-compose-bom").get()
        "implementation"(platform(bom))
        "implementation"(libs.findLibrary("androidx-compose-ui").get())
        "implementation"(libs.findLibrary("androidx-compose-ui-graphics").get())
        "implementation"(libs.findLibrary("androidx-compose-material3").get())
        "implementation"(libs.findLibrary("androidx-compose-ui-tooling-preview").get())
        "debugImplementation"(libs.findLibrary("androidx-compose-ui-tooling").get())
        "debugImplementation"(libs.findLibrary("androidx-compose-ui-test-manifest").get())
        "androidTestImplementation"(platform(bom))
        "androidTestImplementation"(libs.findLibrary("androidx-compose-ui-test-junit4").get())
    }
}
