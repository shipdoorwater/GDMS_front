package com.example.gdms_front.profit

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.gdms_front.R
import com.example.gdms_front.adapter.TierExpAdapter

class TierExpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tier_exp)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val adapter = TierExpAdapter(this)
        viewPager.adapter = adapter

        // Intent에서 전달된 데이터 처리
        val fragmentIndex = intent.getIntExtra("FRAGMENT_INDEX", 0)
        viewPager.setCurrentItem(fragmentIndex, false)
    }
}