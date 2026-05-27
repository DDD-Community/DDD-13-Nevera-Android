plugins {
    id("nevera.android.application")
}

android {
    namespace = "com.anddd.nevera"

    defaultConfig {
        applicationId = "com.anddd.nevera"
        versionCode = 1
        versionName = "1.0"
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
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:network"))
    implementation(project(":infra:notification"))

    implementation(project(":feature:splash"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:main"))
    implementation(project(":feature:mypage"))
    implementation(project(":feature:ingredient"))

    implementation(project(":data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.work)
    implementation(libs.timber)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
}
