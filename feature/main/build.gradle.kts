plugins {
    id("nevera.feature")
}

android {
    namespace = "com.anddd.nevera.feature.main"
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coroutines.android)
}
