plugins {
    id("nevera.feature")
}

android {
    namespace = "com.anddd.nevera.feature.splash"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
}
