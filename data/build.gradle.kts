plugins {
    id("nevera.android.library")
    id("nevera.android.hilt")
    id("nevera.network")
    id("nevera.firebase")
}

android {
    namespace = "com.anddd.nevera.data"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":domain"))
    implementation(libs.coroutines.android)
    implementation(libs.datastore.preferences)
    implementation(libs.timber)
    implementation(libs.paging.runtime)
    implementation(libs.room.runtime)
    implementation(libs.room.paging)
}
