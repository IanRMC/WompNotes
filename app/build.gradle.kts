plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.wompnotes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.wompnotes"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Dependencias de Android y Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation ("io.coil-kt:coil-compose:2.2.2")

    implementation ("com.google.android.exoplayer:exoplayer:2.19.0")

    implementation ("com.google.android.material:material:1.8.0")

    // Dependencia para la navegación en Jetpack Compose
    implementation("androidx.navigation:navigation-compose:2.8.3") // para cambiar pantallas

    implementation ("com.google.android.exoplayer:exoplayer:2.15.1")



    // Dependencias de Compose BOM y Material3
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation ("androidx.room:room-runtime:2.5.2")// versión de room que ya tienes
    implementation ("androidx.room:room-ktx:2.5.2")    // versión de room-ktx compatible
    kapt ("androidx.room:room-compiler:2.5.2")

    // Dependencias para Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1") // Necesario si usas KAPT para el procesamiento de anotaciones

    // Dependencia para media3 (ajusta la versión según lo que encuentres disponible)
    implementation("androidx.media3:media3-common:1.0.0")

    // Dependencias para pruebas
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    implementation("androidx.navigation:navigation-compose:2.5.3")

    // Material icons extended
    implementation("androidx.compose.material:material-icons-extended:1.5.0")

    // Debugging tools
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
