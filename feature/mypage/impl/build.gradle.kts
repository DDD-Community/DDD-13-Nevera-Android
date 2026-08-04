plugins {
    id("nevera.feature")
}

android {
    namespace = "com.anddd.nevera.feature.mypage"
}

dependencies {
    implementation(project(":feature:mypage:api"))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coroutines.android)
    implementation(project(":feature:notification:api"))
    implementation(project(":infra:permission"))
}
