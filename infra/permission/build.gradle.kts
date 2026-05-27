plugins {
    id("nevera.android.compose")
}

android {
    namespace = "com.anddd.nevera.infra.permission"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)

    testImplementation(libs.mockk)
}
