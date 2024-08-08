package com.example.gdms_front.alarm

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.gdms_front.model.NoticeResponse
import com.example.gdms_front.model.Notification
import com.example.gdms_front.network.NoticeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotificationRepository(context: Context) {
    private val notificationDao = AppDatabase.getDatabase(context).notificationDao()
    private val noticeApiService: NoticeApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.73:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        noticeApiService = retrofit.create(NoticeApiService::class.java)
    }


    suspend fun insert(notification: Notification) {
        notificationDao.insert(notification)
    }

    fun getAllNotifications(userId: String): LiveData<List<Notification>> =
        notificationDao.getAllNotifications(userId)

    fun getUnreadCount(userId: String): LiveData<Int> =
        notificationDao.getUnreadCount(userId)

    suspend fun notificationExists(boardId: Int, userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            notificationDao.notificationExists(boardId, userId) > 0
        }
    }

    suspend fun deleteAllNotifications(userId: String) = withContext(Dispatchers.IO) {
        notificationDao.deleteAllNotifications(userId)
    }

    suspend fun markAsRead(id: Int) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }
}
