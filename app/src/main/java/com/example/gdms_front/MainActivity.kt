package com.example.gdms_front

import android.content.Context
import android.content.Intent
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
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {

    private val notificationViewModel: NotificationViewModel by viewModels()
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




//        Log.d(TAG, "onCreate called")
//        try {
//            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
//            for (signature in info.signatures) {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
//                Log.d("KeyHash", keyHash)
//            }
//        } catch (e: Exception) {
//            Log.e("KeyHash", "Error getting key hash", e)
//        }

        // 파이어베이스 토큰 전송
        getFCMToken()

        findViewById<ImageView>(R.id.logo).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.myPageBtn).setOnClickListener {
            Log.d(TAG, "MyPage button clicked")
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        // SharedPreferences에서 userId 가져오기
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("token", "") ?: ""
        notificationViewModel.setUserId(userId)


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