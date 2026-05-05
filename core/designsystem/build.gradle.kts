plugins {
    id("nevera.android.compose")
}

android {
    namespace = "com.anddd.nevera.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.ui.tooling.preview)
}
