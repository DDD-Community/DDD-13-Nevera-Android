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
        ?: ""

android {
    namespace = "com.anddd.nevera.feature.login"

    defaultConfig {
        val googleClientId = localProperties.getOrEnv("GOOGLE_WEB_CLIENT_ID")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleClientId\"")
    }


}

dependencies {
    implementation(libs.coroutines.android)
    implementation(libs.bundles.google.login)
}
