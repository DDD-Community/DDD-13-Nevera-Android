plugins {
    id("nevera.android.compose")
}

android {
    namespace = "com.anddd.nevera.core.navigation"
}

dependencies {
    implementation(libs.navigation.compose)
    api(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.lifecycle.viewmodel.navigation3)
}
