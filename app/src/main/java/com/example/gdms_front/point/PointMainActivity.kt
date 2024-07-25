package com.example.gdms_front.point

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gdms_front.R

class PointMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_main)

        findViewById<Button>(R.id.withDrawBtn).setOnClickListener {
            val intent = Intent(this, WithdrawPointActivity::class.java)
            startActivity(intent)
        }

    }
}