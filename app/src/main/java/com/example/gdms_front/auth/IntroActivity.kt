package com.example.gdms_front.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gdms_front.MainActivity
import com.example.gdms_front.R

//사용자가 이미 로그인한 경우 메인 화면으로 이동, 아니면 경우 로그인 또는 회원가입 화면으로 이동


class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


        // 사용자 인증 상태 확인 (예: SharedPreferences 사용)
        val isLoggedIn = checkUserLoggedIn()
        Log.d("IntroActivity", "isLoggedIn: $isLoggedIn")

        if (isLoggedIn) {
            // 메인 화면으로 이동
            Log.d("IntroActivity", "User is already logged in")
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Log.d("IntroActivity", "User is not logged in")
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish() // Intro Activity 종료
    }


    private fun checkUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        return token != null
    }



    }
