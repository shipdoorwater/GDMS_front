package com.example.gdms_front.model

data class SubscribeRequest (
    val userId: String,
    val packId: Int,
    val amountPaid: Int
)