package com.example.gdms_front.profit

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.gdms_front.R
import com.example.gdms_front.adapter.SubItemsAdapter
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class ServicePackDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_pack_detail)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val adapter = SubItemsAdapter(this)
        val dotsIndicator: DotsIndicator = findViewById(R.id.dots_indicator)

        viewPager.adapter = adapter

        dotsIndicator.setViewPager2(viewPager)

        // Intent에서 전달된 데이터 처리
        val fragmentIndex = intent.getIntExtra("FRAGMENT_INDEX", 0)
        val packId = intent.getIntExtra("packId", -1)

        //구독상태에서 올때랑 안올 때 구분
        if (packId == -1) {
            viewPager.setCurrentItem(fragmentIndex-1, false)
        } else {
            viewPager.setCurrentItem(packId-1, false)
        }

        intent.removeExtra("FRAGMENT_INDEX")
        intent.removeExtra("packId")

    }

    }


