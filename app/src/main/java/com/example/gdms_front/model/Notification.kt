package com.example.gdms_front.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, // 사용자 식별을 위해 유지
    val title: String, // 서버에서 전송된 title
    val content: String, // 서버에서 전송된 body
    val timestamp: Long, // 알림 수신 시간
    val isRead: Boolean = false, // 읽음 상태 추적을 위해 유지
    val boardId: Int? = null, // 추후 확장을 위해 nullable로 유지
    val subId: String? = null, // Int에서 String으로 변경, nullable
    val boardDate: String? = null // 추후 확장을 위해 nullable로 유지
)