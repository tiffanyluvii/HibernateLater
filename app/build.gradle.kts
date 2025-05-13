plugins {
    id("com.google.gms.google-services")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) version "2.1.0"
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.hibernatelater"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.hibernatelater"
        minSdk = 24
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.firebase.auth.ktx)
    val room_version = "2.7.1"  // Use the stable version of Room
    implementation("androidx.room:room-runtime:$room_version")  // Room Runtime
    implementation("androidx.room:room-ktx:$room_version")     // Room KTX for Kotlin
    ksp("androidx.room:room-compiler:2.5.0")


    implementation(platform("com.google.firebase:firebase-bom:33.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database.ktx)
    //implementation(libs.androidx.room.common.jvm)
    //implementation(libs.androidx.room.runtime.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // added by RE
    implementation("com.google.android.gms:play-services-ads:23.5.0")
}