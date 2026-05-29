plugins {
    id("nevera.kotlin.jvm")
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.javax.inject)
    implementation(libs.coroutines.core)
    implementation(libs.paging.common)

    testImplementation(libs.coroutines.test)
}
