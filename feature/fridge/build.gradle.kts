plugins {
    id("nevera.feature")
}

android {
    namespace = "com.anddd.nevera.feature.fridge"
}

dependencies {
    implementation(libs.coroutines.android)
    implementation(project(":infra:permission"))
}
