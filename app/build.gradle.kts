// app/build.gradle.kts

android {
    namespace = "com.rendemen.jagung"
    compileSdk = 35

    defaultConfig {
        // ...
    }

    buildFeatures {
        compose = true // Wajib ada
    }

    composeOptions {
        // Ini adalah cara yang benar untuk mengatur versi compiler
        kotlinCompilerExtensionVersion = "1.5.14" 
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
