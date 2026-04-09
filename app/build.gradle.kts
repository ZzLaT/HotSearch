import java.util.Properties
import java.io.FileInputStream

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

        // 读取 local.properties 文件
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            val localProperties = Properties()
            localProperties.load(FileInputStream(localPropertiesFile))
            
            // 从 local.properties 读取 API_KEY
            val apiKey = localProperties.getProperty("API_KEY", "")
            buildConfigField("String", "API_KEY", "\"$apiKey\"")
            
            // 从 local.properties 读取 WECHAT_APP_ID
            val wechatAppId = localProperties.getProperty("WECHAT_APP_ID", "")
            buildConfigField("String", "WECHAT_APP_ID", "\"$wechatAppId\"")
            
            // 从 local.properties 读取 QQ_APP_ID
            val qqAppId = localProperties.getProperty("QQ_APP_ID", "")
            buildConfigField("String", "QQ_APP_ID", "\"$qqAppId\"")
            
            // 配置 manifestPlaceholders，用于在 AndroidManifest.xml 中替换占位符
            manifestPlaceholders["WECHAT_APP_ID"] = wechatAppId
            manifestPlaceholders["QQ_APP_ID"] = qqAppId
        } else {
            // 如果 local.properties 不存在，使用空字符串
            buildConfigField("String", "API_KEY", "\"\"")
            buildConfigField("String", "WECHAT_APP_ID", "\"\"")
            buildConfigField("String", "QQ_APP_ID", "\"\"")
            manifestPlaceholders["WECHAT_APP_ID"] = ""
            manifestPlaceholders["QQ_APP_ID"] = ""
        }
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

    // Glide - 图片加载库
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // Share SDKs
    implementation(libs.wechat.sdk)
    // implementation(libs.qq.sdk)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}