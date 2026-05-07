plugins {
    id("nevera.android.library")
    id("nevera.android.hilt")
    id("nevera.network")
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
    implementation(libs.timber)
}
