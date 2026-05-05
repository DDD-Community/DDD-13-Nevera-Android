plugins {
    id("nevera.android.library")
    id("nevera.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.anddd.nevera.core.network"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            // 테스트 서버 분리 시, 도메인 변경 필요
            buildConfigField("String", "BASE_URL", "\"https://api.nevera.n-e.kr/\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://api.nevera.n-e.kr/\"")
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.timber)
}
