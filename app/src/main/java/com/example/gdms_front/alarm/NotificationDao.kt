package com.example.gdms_front.alarm

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gdms_front.model.Notification

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notification: Notification)

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllNotifications(userId: String): LiveData<List<Notification>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0 AND userId = :userId")
    fun getUnreadCount(userId: String): LiveData<Int>

    @Query("SELECT COUNT(*) FROM notifications WHERE boardId = :boardId AND userId = :userId")
    fun notificationExists(boardId: Int, userId: String): Int

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteAllNotifications(userId: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)
}