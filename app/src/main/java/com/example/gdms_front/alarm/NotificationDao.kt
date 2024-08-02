package com.example.gdms_front.alarm

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gdms_front.model.Notification

@Dao
interface NotificationDao {
    @Insert
    suspend fun insert(notification: Notification)

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    suspend fun getAllNotifications(): List<Notification>
}