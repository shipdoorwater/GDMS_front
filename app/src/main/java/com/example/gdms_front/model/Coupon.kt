package com.example.gdms_front.model

data class Coupon(
    val cpnSendId: Int,
    val cpnId: Int,
    val userId: String,
    val sendDate: String,
    val expDate: String,
    val usedYn: Boolean
)