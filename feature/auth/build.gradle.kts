import java.util.Properties

plugins {
    id("nevera.feature")
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

fun Properties.getOrEnv(key: String, required: Boolean = true): String =
    getProperty(key)?.trim()?.takeIf { it.isNotEmpty() }
        ?: System.getenv(key)?.trim()?.takeIf { it.isNotEmpty() }
        ?: if (required) error("$key is not set. Add it to local.properties or set it as an environment variable.")
        else { logger.warn("⚠️ $key is not set — using empty string for non-release build."); "" }

android {
    namespace = "com.anddd.nevera.feature.auth"

    defaultConfig {
        val isReleaseBuild = gradle.startParameter.taskNames.any { it.contains("release", ignoreCase = true) }
        val googleClientId = localProperties.getOrEnv("GOOGLE_WEB_CLIENT_ID", required = isReleaseBuild)
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleClientId\"")
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coroutines.android)
    implementation(libs.bundles.google.login)
}
