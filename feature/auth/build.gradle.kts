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

fun Properties.getOrEnv(key: String): String =
    getProperty(key)?.trim()?.takeIf { it.isNotEmpty() }
        ?: System.getenv(key)?.trim()?.takeIf { it.isNotEmpty() }
        ?: error("$key is not set. Add it to local.properties or set it as an environment variable.")

android {
    namespace = "com.anddd.nevera.feature.auth"

    defaultConfig {
        val googleClientId = localProperties.getOrEnv("GOOGLE_WEB_CLIENT_ID")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleClientId\"")
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coroutines.android)
    implementation(libs.bundles.google.login)
}
