package com.example.gdms_front.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gdms_front.MainActivity
import com.example.gdms_front.R

class JoinSucActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("JoinSucActivity", "Notification permission granted")
            } else {
                Log.d("JoinSucActivity", "Notification permission denied")
            }
            // 권한 요청이 완료된 후에 Handler 실행
            startMainActivityWithDelay()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_suc)

        val pushYn = intent.getStringExtra("pushYn").toString()
        Log.d("조인석pushYN", pushYn)

        if (pushYn == "true" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        } else {
            // 권한 요청이 필요 없는 경우 바로 Handler 실행
            startMainActivityWithDelay()
        }
    }

    private fun requestNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 권한이 이미 허용됨
                Log.d("JoinSucActivity", "Notification permission already granted")
                startMainActivityWithDelay()
            }
            else -> {
                // 권한 요청
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun startMainActivityWithDelay() {
        Handler(mainLooper).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 현재 Activity 종료
        }, 3000) // 3000 밀리초 (3초) 후 실행
    }
}