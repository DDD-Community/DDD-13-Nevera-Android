plugins {
    id("nevera.android.library")
    id("nevera.android.hilt")
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
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.datastore.preferences)
    implementation(libs.timber)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
}
