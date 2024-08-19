plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    kotlin("kapt") // kapt 플러그인 추가
}

android {
    namespace = "com.example.gdms_front"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gdms_front"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["KAKAO_APP_KEY"] = "@string/kakao_app_key"
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

    dataBinding{
        enable = true
    }

    buildFeatures {
        buildConfig = true
    }


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.android.gms:play-services-location:21.0.1")
    // 네이버 지도 SDK
    implementation("com.naver.maps:map-sdk:3.19.0")
    
    //레트로핏
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")


    //글라이드 라이브러리
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //카메라
    implementation("androidx.camera:camera-core:1.1.0")
    implementation("androidx.camera:camera-camera2:1.1.0")
    implementation("androidx.camera:camera-lifecycle:1.1.0")
    implementation("androidx.camera:camera-view:1.1.0")
    implementation("androidx.camera:camera-extensions:1.1.0")
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.3.3")

    // ViewModel, LiveData, Lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.squareup.picasso:picasso:2.71828")
    
    //손가락으로 페이지 넘기는거 해보게
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // ShapeableImageView 의존성 추가
    implementation("com.google.android.material:material:1.4.0")

    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.2")


    // 파이어베이스
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    implementation("androidx.work:work-runtime-ktx:2.7.1")

    //달력
    implementation ("com.github.prolificinteractive:material-calendarview:2.0.1")

    //차트 라이브러리 추가
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    
    //인디케이터
    implementation("com.tbuonomo:dotsindicator:5.0")

    implementation("androidx.constraintlayout:constraintlayout:2.2.0-alpha14")

    // Kakao SDK
    implementation("com.kakao.sdk:v2-user:2.20.3") // 카카오 로그인

}


kapt {
    correctErrorTypes = true
}
