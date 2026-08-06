plugins {
    id("nevera.android.library")
}

android {
    namespace = "com.anddd.nevera.core.navigation"
}

dependencies {
    implementation(libs.navigation.compose)
    api(libs.navigation3.runtime)
}
