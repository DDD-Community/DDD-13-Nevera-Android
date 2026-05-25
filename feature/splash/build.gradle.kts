plugins {
    id("nevera.feature")
}

android {
    namespace = "com.anddd.nevera.feature.splash"
}

dependencies {
    implementation(project(":infra:permission"))
}
