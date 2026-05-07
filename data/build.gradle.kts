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
    // 실제 DB 로직을 생성할때 추가
    // implementation(project(":core:database"))
    implementation(project(":domain"))
    implementation(libs.coroutines.android)
    implementation(libs.datastore.preferences)
    implementation(libs.timber)
}
