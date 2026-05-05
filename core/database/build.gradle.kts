plugins {
    id("nevera.android.library")
    id("nevera.android.hilt")
}

android {
    namespace = "com.anddd.nevera.core.database"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.timber)
}
