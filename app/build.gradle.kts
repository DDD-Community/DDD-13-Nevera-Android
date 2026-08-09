plugins {
    id("nevera.android.application")
}

android {
    namespace = "com.anddd.nevera"

    defaultConfig {
        applicationId = "com.anddd.nevera"
        versionCode = 8
        versionName = "1.0.4"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = rootProject.file("keystore/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                // 네이티브 크래시/ANR 분석을 위해 디버그 심볼을 App Bundle에 포함
                debugSymbolLevel = "FULL"
            }
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:network"))
    implementation(project(":infra:notification"))

    implementation(project(":feature:splash:impl"))
    implementation(project(":feature:auth:api"))
    implementation(project(":feature:auth:impl"))
    implementation(project(":feature:main:api"))
    implementation(project(":feature:main:impl"))
    implementation(project(":feature:mypage:api"))
    implementation(project(":feature:mypage:impl"))
    implementation(project(":feature:notification:api"))
    implementation(project(":feature:notification:impl"))
    implementation(project(":feature:ingredient:api"))
    implementation(project(":feature:ingredient:impl"))
    implementation(project(":feature:fridge:api"))
    implementation(project(":feature:fridge:impl"))

    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.work)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.timber)
    testImplementation(libs.junit.jupiter.params)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
}
