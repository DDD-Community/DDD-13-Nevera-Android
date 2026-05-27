plugins {
    id("nevera.feature")
}

android {
    namespace = "com.anddd.nevera.feature.receipt"
}

dependencies {
    implementation(libs.coroutines.android)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
}
