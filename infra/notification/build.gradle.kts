plugins {
    id("nevera.android.library")
    id("nevera.android.hilt")
    id("nevera.firebase")
}

android {
    namespace = "com.anddd.nevera.infra.notification"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:common"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.timber)
    implementation(libs.work.runtime.ktx)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
}
