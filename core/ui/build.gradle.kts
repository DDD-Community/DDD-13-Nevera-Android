plugins {
    id("nevera.android.compose")
}

android {
    namespace = "com.anddd.nevera.core.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
}
