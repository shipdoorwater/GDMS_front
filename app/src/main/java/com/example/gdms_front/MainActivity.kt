package com.example.gdms_front

import CheckWorker
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.gdms_front.myPage.MyPageActivity
import java.util.concurrent.TimeUnit
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.gdms_front.navigation_frag.PayFragment
import com.example.gdms_front.navigation_frag.ProfitFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageView>(R.id.myPageBtn).setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        val workRequest = PeriodicWorkRequest.Builder(CheckWorker::class.java, 15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "BoardCheckWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

        findViewById<ImageView>(R.id.alarmBtn).setOnClickListener {
            findNavController(R.id.fragmentContainerView).navigate(R.id.notificationFragment)
        }
    }
}