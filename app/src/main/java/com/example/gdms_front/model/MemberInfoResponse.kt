package com.example.gdms_front.model

data class MemberInfoResponse (
    val userName: String,
    val birthDate: String,
    val userPhone: String,
    val userEmail: String,
    val userAddress: String,
    val profileTitle: String,
    val profileUrl: String,
    val subscribedServices: List<String>,
    val totalPoints: Int
)