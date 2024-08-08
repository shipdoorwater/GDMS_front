package com.example.gdms_front.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.gdms_front.R
import com.example.gdms_front.model.Notification
import com.example.gdms_front.model.TokenUpdate
import com.example.gdms_front.network.RetrofitClient
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "Message received")
        Log.d("FCM", "From: ${remoteMessage.from}")
        Log.d("FCM", "Notification Message Body: ${remoteMessage.notification?.title}")
        Log.d("FCM", "Notification Message Body: ${remoteMessage.notification?.body}")
        Log.d("FCM", "Data payload: ${remoteMessage.data}")
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: ""
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""

        // 로컬 데이터베이스에 알림 저장
        saveNotificationToLocalDatabase(title, body)

        // 사용자에게 알림 표시
        showNotification(title, body)
    }

    private fun saveNotificationToLocalDatabase(title: String, body: String) {
        val sharedPreference = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreference.getString("token", null)

        if (userId != null) {
            val notification = Notification(
                userId = userId,
                title = title,
                content = body,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )

            // 코루틴 스코프에서 데이터베이스 작업 수행
            coroutineScope.launch {
                AppDatabase.getDatabase(applicationContext).notificationDao().insert(notification)
            }
        } else {
            Log.e("MyFirebaseMessagingService", "User ID is null. Unable to save notification.")
        }
    }

    private fun showNotification(title: String, body: String) {
        Log.d("FCM", "Showing notification - Title: $title, Body: $body")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = getString(R.string.default_notification_channel_id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "FCM Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.img_notification_1)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
        Log.d("FCM", "Showing notification finished - Title: $title, Body: $body")
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "New token generated: $token")
        // 새 토큰을 서버에 전송
        FCMTokenManager.handleFCMToken(this, token)
    }

}