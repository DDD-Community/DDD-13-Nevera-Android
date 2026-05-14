plugins {
    id("nevera.android.library")
}

android {
    namespace = "com.anddd.nevera.core.mvi"
}

dependencies {
    api(libs.bundles.orbit)
    implementation(libs.timber)
}
