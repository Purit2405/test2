plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize") // <--- เพิ่มบรรทัดนี้เพื่อให้ @Parcelize ทำงานได้

}

android {
    namespace = "com.purit.apptest"
    compileSdk = 36 // แนะนำให้ใช้ 35 สำหรับเสถียรภาพปัจจุบัน หรือ 36 ตามที่คุณตั้งไว้

    defaultConfig {
        applicationId = "com.purit.apptest"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // --- เพิ่มส่วนนี้เพื่อให้ LoginActivity ใช้ Binding ได้ ---
    buildFeatures {
        viewBinding = true
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // --- เพิ่ม Libraries สำหรับเชื่อมต่อ Laravel API ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // --- เพิ่ม Library สำหรับโหลดรูปภาพ ---
    implementation("com.github.bumptech.glide:glide:4.15.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("io.coil-kt:coil:2.5.0")
    implementation("io.coil-kt:coil-svg:2.5.0")
}