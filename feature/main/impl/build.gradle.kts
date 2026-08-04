plugins {
    id("nevera.feature")
}

android {
    namespace = "com.anddd.nevera.feature.main"
}

dependencies {
    implementation(project(":feature:main:api"))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coroutines.android)
    implementation(project(":feature:notification:api"))
}
