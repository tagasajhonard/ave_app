plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "ordering.app.avenuet_housebongabong"
    compileSdk = 34

    defaultConfig {
        applicationId = "ordering.app.avenuet_housebongabong"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.messaging)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("androidx.core:core:1.7.0")


    implementation ("com.github.Spikeysanju:MotionToast:1.4")

    implementation("com.google.firebase:firebase-bom:32.8.1")

    implementation ("com.firebaseui:firebase-ui-database:8.0.2")

    implementation ("com.google.mlkit:barcode-scanning:17.0.3")

    implementation ("com.google.mlkit:barcode-scanning:17.3.0")
    implementation ("androidx.camera:camera-camera2:1.3.4")
    implementation ("androidx.camera:camera-lifecycle:1.3.4")
    implementation ("androidx.camera:camera-view:1.3.4")

    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")


    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation ("com.google.firebase:firebase-database:20.3.1")
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    implementation ("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")

    implementation ("com.facebook.android:facebook-login:latest.release")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("androidx.appcompat:appcompat:1.4.0")
    implementation ("androidx.activity:activity:1.4.0")
    implementation ("androidx.fragment:fragment:1.4.0")

    implementation ("com.google.firebase:firebase-appcheck:17.1.2")
    implementation ("com.google.android.play:integrity:1.3.0")

    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation ("com.google.firebase:firebase-appcheck-debug:18.0.0")

    implementation ("com.google.android.gms:play-services-location:20.0.0")


    implementation ("dev.shreyaspatil.MaterialDialog:MaterialDialog:2.2.3")

    implementation ("com.airbnb.android:lottie:5.2.0")

    implementation ("com.google.firebase:firebase-messaging:23.0.0")








}

