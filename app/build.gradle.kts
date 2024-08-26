plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.mecha.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mecha.app"
        minSdk = 28
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
        dataBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation (libs.drawerlayout)
    implementation(libs.room.runtime)
    implementation(libs.firebase.auth.v2120)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    implementation(libs.annotation)
    implementation(libs.legacy.support.v4)

    implementation(libs.material.v130)
    implementation(libs.firebase.storage)
    annotationProcessor(libs.room.compiler)

    implementation(libs.picasso)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.sdk.full)
    implementation(libs.firebase.appcheck.playintegrity)


    implementation (libs.volley)



    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

apply(plugin = "com.google.gms.google-services")