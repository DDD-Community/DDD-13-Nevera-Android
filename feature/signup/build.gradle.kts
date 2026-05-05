plugins {
    id("nevera.feature")
}

android {
    namespace = "com.anddd.nevera.feature.signup"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coroutines.android)
}
