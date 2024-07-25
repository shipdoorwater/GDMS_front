package com.example.gdms_front.model

data class WithdrawRequest (
    val userId: String,
    val usePoint: Int,
    val accountNumber: String
)