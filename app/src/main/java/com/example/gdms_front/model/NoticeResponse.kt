package com.example.gdms_front.model

data class NoticeResponse (
    val boardId: Int,
    val subId: Int,
    val boardDate: String,
    val title: String,
    val content: String,
    val deleteYn: Boolean
)