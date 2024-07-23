package com.example.gdms_front

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gdms_front.auth.IntroActivity
import kotlin.random.Random

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val imageView: ImageView = findViewById(R.id.backgroundImageView)

        val images = arrayOf(
            R.drawable.background1,
            R.drawable.background2,
            R.drawable.background3
            // 필요한 만큼 추가
        )

        // 랜덤으로 이미지 선택
        val randomImage = images[Random.nextInt(images.size)]

        // ImageView에 랜덤 이미지 설정
        imageView.setImageResource(randomImage)

        Handler().postDelayed({
            startActivity((Intent(this, IntroActivity::class.java))) //인트로엑티비티로 이동
            finish()
        }, 3000)
    }
}