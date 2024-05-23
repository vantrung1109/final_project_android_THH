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

    //lombok
    compileOnly ("org.projectlombok:lombok:1.18.32")
    annotationProcessor ("org.projectlombok:lombok:1.18.32")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    //retrofit
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:retrofit:2.5.0")
    implementation("com.squareup.retrofit2:converter-gson:2.5.0")
    implementation ("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.2")

    implementation ("io.reactivex.rxjava3:rxjava:3.1.5")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}