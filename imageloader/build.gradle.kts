plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.imageloader"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines)
    implementation(libs.androidx.annotation)
}
