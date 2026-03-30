plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.hotsearch"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.hotsearch"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.viewpager2)
    implementation("androidx.core:core:1.9.0")

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Browser
    implementation(libs.browser)

    // Logger
    implementation(libs.logger)

    // LeakCanary - 仅在 debug 模式下检测内存泄漏
    debugImplementation(libs.leakcanary)

    // Share SDKs
    implementation(libs.wechat.sdk)
    // implementation(libs.qq.sdk)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}