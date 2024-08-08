package com.example.gdms_front.model

data class JoinRequest (
    val userId: String,
    val userPw: String,
    val userName: String,
    val gender: Int,
    val userPhone: String,
    val userAdrs: String,
    val userEmail: String,
    val userBirth: String,
    val pushYn: Boolean,
    val marketingYn: Boolean,
    val userStatus: Boolean,
    val adminYn: Boolean,
    val payType: Int,
    val fcmToken: String  // 토큰
)

