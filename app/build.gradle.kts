plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services) // Pastikan plugin Firebase ada
}

android {
    namespace = "com.example.admin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.admin"
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
}

dependencies {
    // Firebase BoM (Bill of Materials) untuk sinkronisasi versi
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

    // Dependensi Firebase
    implementation ("com.google.firebase:firebase-database")
    implementation ("com.google.firebase:firebase-storage")
    implementation ("com.google.firebase:firebase-appcheck") // ✅ Solusi error NoClassDefFoundError

    // Library lain
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.5.0")
    implementation ("com.firebaseui:firebase-ui-database:8.0.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}