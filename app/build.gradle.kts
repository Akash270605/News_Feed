plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.news_feed"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.news_feed"
        minSdk = 24
        targetSdk = 36
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    //Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // WorkManager
    implementation(libs.work.runtime)

    //Glide
    implementation(libs.glide)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}