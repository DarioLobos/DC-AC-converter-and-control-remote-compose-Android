plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)            // This must be UNCOMMENTED
    alias(libs.plugins.androidx.room)   // This must be UNCOMMENTED
}

kotlin{
        }

android {

        namespace = "com.example.dc_acconverterandcontrolremote"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }

    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    defaultConfig {
        applicationId = "com.example.dc_acconverterandcontrolremote"
        minSdk = 29
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    // 1. Core & Compose (Using Catalog)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // 2. Room (Using Catalog)
    implementation(libs.androidx.room.runtime)
    // If Room 2.8.4 shows 'ktx' as red, simply DELETE this line.
    // Coroutines are now built into the main runtime.
    implementation(libs.androidx.room.ktx)
    // This fixes "Unresolved ksp"
    ksp(libs.androidx.room.compiler)

    // 3. Navigation & Lifecycle (Updating to match your Version Catalog)
    implementation(libs.androidx.navigation3.runtime)
    // Use the versions Panda suggested for better compatibility:
    implementation("androidx.navigation:navigation-compose:2.9.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")

    // 4. Coroutines & DataStore (Updated to latest)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("androidx.datastore:datastore-preferences:1.2.0")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")

    // 5. Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}