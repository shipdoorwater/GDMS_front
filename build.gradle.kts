// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}


// 네이버 지도 SDK 저장소 설정 추가 -< Maven 저장소에서 배포
allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://repository.map.naver.com/archive/maven")
        maven("https://jitpack.io")

        // 카카오 로그인 SDK 추가
        maven ("https://devrepo.kakao.com/nexus/content/groups/public")
    }
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
    }
}