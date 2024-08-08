package com.example.gdms_front

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.gdms_front.myPage.MyPageActivity
import androidx.navigation.findNavController
import com.example.gdms_front.alarm.FCMTokenManager
import com.example.gdms_front.alarm.NotificationViewModel
import com.example.gdms_front.model.TokenUpdate
import com.example.gdms_front.network.RetrofitClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val notificationViewModel: NotificationViewModel by viewModels()
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate called")

        // 파이어베이스 토큰 전송
        getFCMToken()

        findViewById<ImageView>(R.id.myPageBtn).setOnClickListener {
            Log.d(TAG, "MyPage button clicked")
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        val alarmBtn = findViewById<ImageView>(R.id.alarmBtn)
        notificationViewModel.unreadCount.observe(this, Observer { count ->
            Log.d(TAG, "Unread count observed: $count")
            if (count > 0) {
                alarmBtn.setImageResource(R.drawable.img_notification_2)  // 새로운 아이콘으로 변경
            } else {
                alarmBtn.setImageResource(R.drawable.img_notification_1)  // 기본 아이콘
            }
        })

        alarmBtn.setOnClickListener {
            findNavController(R.id.fragmentContainerView).navigate(R.id.notificationFragment)
        }
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result
                Log.d(TAG, "FCM Token: $token")
                FCMTokenManager.handleFCMToken(this, token)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get FCM token", e)
            }
    }

}