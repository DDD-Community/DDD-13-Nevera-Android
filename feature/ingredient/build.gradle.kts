plugins {
    id("nevera.feature")
}

android {
    namespace = "com.anddd.nevera.feature.ingredient"
}

dependencies {
    implementation(libs.coroutines.android)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
}