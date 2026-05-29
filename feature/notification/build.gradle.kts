plugins {
    id("nevera.feature")
}

android {
    namespace = "com.anddd.nevera.feature.notification"
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coroutines.android)
    implementation(libs.paging.compose)
}
