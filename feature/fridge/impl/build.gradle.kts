plugins {
    id("nevera.feature")
}

android {
    namespace = "com.anddd.nevera.feature.fridge"
}

dependencies {
    implementation(project(":feature:fridge:api"))
    implementation(libs.coroutines.android)
    implementation(project(":feature:notification:api"))
    implementation(project(":feature:ingredient:api"))
    implementation(project(":infra:permission"))
}
