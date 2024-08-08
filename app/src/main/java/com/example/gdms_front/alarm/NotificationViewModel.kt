package com.example.gdms_front.alarm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.gdms_front.model.Notification
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application) : AndroidViewModel(application){
    private val repository : NotificationRepository = NotificationRepository(application)

    // userId를 저장할 변수
    private var userId: String = ""

    // allNotifications와 unreadCount를 Lazy로 초기화
    val allNotifications: LiveData<List<Notification>> by lazy {
        repository.getAllNotifications(userId)
    }
    val unreadCount: LiveData<Int> by lazy {
        repository.getUnreadCount(userId)
    }

    // userId를 설정하는 메서드
    fun setUserId(newUserId: String) {
        userId = newUserId
    }

    fun insert(notification: Notification) = viewModelScope.launch {
        repository.insert(notification)
    }

    // 개발 중 db초기화를 위해 만든 기능
    fun clearAllNotifications() = viewModelScope.launch {
        repository.deleteAllNotifications(userId)
    }

    fun markAsRead(notification: Notification) = viewModelScope.launch {
        repository.markAsRead(notification.id)
    }
}