package com.example.gdms_front.model

data class PayHistory (
    val payId: Int,
    val userId: String,
    val amount: Int,
    val payDate: String,
    val storeName: String,
    val storeCode: Int,
    val bizNo: String
)

data class PayHistoryResponse(
    val message: List<PayHistory>
)