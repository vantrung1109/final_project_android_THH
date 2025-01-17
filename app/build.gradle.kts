plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.projectfinaltth"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.projectfinaltth"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.cardview:cardview:1.0.0")
    implementation ("com.intuit.sdp:sdp-android:1.1.1")
    //FlexibleAdapter
    implementation ("com.github.ddB0515.FlexibleAdapter:flexible-adapter:5.1.1")
    implementation("com.github.ddB0515.FlexibleAdapter:flexible-adapter-databinding:5.1.1")
    implementation("com.github.ddB0515.FlexibleAdapter:flexible-adapter-ui:5.1.1")
    implementation("com.github.ddB0515.FlexibleAdapter:flexible-adapter-livedata:5.1.1")
    implementation ("com.github.davideas:FlipView:1.2.0")

    //Image Picker
    implementation("com.github.dhaval2404:imagepicker:2.1")

    //lombok
    compileOnly ("org.projectlombok:lombok:1.18.32")
    annotationProcessor ("org.projectlombok:lombok:1.18.32")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    //retrofit
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("com.google.code.gson:gson:2.11.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.2")

    //Read more textview
    implementation ("kr.co.prnd:readmore-textview:1.0.0")

    //Media3 for video

    implementation ("androidx.media3:media3-exoplayer:1.3.1")
    implementation ("androidx.media3:media3-ui:1.3.1")
    // Optional dependency for HLS streaming
    implementation ("androidx.media3:media3-exoplayer-hls:1.3.1")
    implementation ("com.google.android.material:material:1.7.0")
    implementation ("io.reactivex.rxjava3:rxjava:3.1.8")
    implementation(libs.appcompat)
//    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    implementation("androidx.camera:camera-core:1.1.0")
    implementation("androidx.camera:camera-camera2:1.1.0")
    implementation("androidx.camera:camera-lifecycle:1.1.0")
    implementation("androidx.camera:camera-view:1.1.0")

    // ML Kit
    implementation("com.google.mlkit:face-detection:16.1.3")

    // TensorFlow Lite
    implementation("org.tensorflow:tensorflow-lite:0.7.0")
    implementation("org.tensorflow:tensorflow-lite:2.8.0")
    implementation("com.google.code.gson:gson:2.8.8")

    // Navigation Component
    val nav_version = "2.7.2"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
}