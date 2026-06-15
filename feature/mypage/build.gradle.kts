plugins {
    id("nevera.feature")
}

android {
    namespace = "com.anddd.nevera.feature.mypage"
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coroutines.android)
    implementation(project(":infra:permission"))
}
