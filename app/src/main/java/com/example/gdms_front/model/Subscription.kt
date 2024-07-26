package com.example.gdms_front.model

data class Subscription(
    val userId: String,
    val subNum: Int,
    val seq: Int,
    val packId: Int,
    val startDate: String,
    val endDate: String,
    val subStatus: Boolean,
    val amountPaid: Int,
    val paymentDate: String,
    val packName: String
)