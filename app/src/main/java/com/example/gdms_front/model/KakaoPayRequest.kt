package com.example.gdms_front.model

data class KakaoPayRequest (
    val pg_token : String,
    val tid : String,
    val userId : String,
    val packId : Int,
    val amountPaid : Int
)