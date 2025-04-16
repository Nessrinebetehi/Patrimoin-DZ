plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.patrimoin_dz"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.patrimoin_dz"
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // ViewPager2
    implementation ("androidx.viewpager2:viewpager2:1.0.0")

    // CircleIndicator (pour les dots)
    implementation ("me.relex:circleindicator:2.1.6")

    implementation ("androidx.cardview:cardview:1.0.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.google.android.material:material:1.9.0")

    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation(libs.firebase.database)
    implementation ("com.google.firebase:firebase-auth:21.0.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.camera.view)
    implementation(libs.camera.lifecycle)
    implementation(libs.firebase.crashlytics.buildtools) // For image loading
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")


    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation(libs.firebase.firestore)

    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")

    implementation ("androidx.emoji2:emoji2:1.4.0")
    implementation ("androidx.emoji2:emoji2-bundled:1.4.0")

    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.core:core-ktx:1.12.0")


    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0") // Ajout de cette ligne
    implementation ("com.squareup.okhttp3:okhttp:4.9.3") // Nécessaire pour OkHttpClient
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3") // Nécessaire pour HttpLoggingInterceptor
    implementation ("org.json:json:20231013")// Nécessaire pour

    implementation ("com.google.code.gson:gson:2.10.1")
    implementation (platform("com.google.firebase:firebase-bom:33.12.0"))


    implementation ("com.google.firebase:firebase-firestore:24.10.3")
    implementation ("com.google.firebase:firebase-storage:20.3.0") // Pour les images
    implementation ("com.google.android.gms:play-services-base:18.5.0")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")




    implementation ("com.google.android.gms:play-services-base:18.5.0")
    implementation ("androidx.activity:activity:1.8.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("com.google.android.gms:play-services-base:18.5.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}