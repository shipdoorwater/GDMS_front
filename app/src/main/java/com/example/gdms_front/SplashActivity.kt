package com.example.gdms_front

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.gdms_front.auth.AspectRatioImageView
import com.example.gdms_front.auth.IntroActivity
import kotlin.random.Random

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val imageView = findViewById<AspectRatioImageView>(R.id.backgroundImageView)
        imageView.setImageResource(R.drawable.splash_img)

        Handler().postDelayed({
            startActivity((Intent(this, IntroActivity::class.java))) //인트로엑티비티로 이동
            finish()
        }, 3000)
    }
}