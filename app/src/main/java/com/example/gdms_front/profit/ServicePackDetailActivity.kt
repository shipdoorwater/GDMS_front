package com.example.gdms_front.profit

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.gdms_front.R
import com.example.gdms_front.adapter.SubItemsAdapter

class ServicePackDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_pack_detail)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val adapter = SubItemsAdapter(this)
        viewPager.adapter = adapter

        // Intent에서 전달된 데이터 처리
        val fragmentIndex = intent.getIntExtra("FRAGMENT_INDEX", 0)
        val packId = intent.getIntExtra("packId", -1)

        if (packId == -1) {
            viewPager.setCurrentItem(fragmentIndex-1, false)
        } else {
            viewPager.setCurrentItem(packId-1, false)
        }

        intent.removeExtra("FRAGMENT_INDEX")
        intent.removeExtra("packId")

    }

    }


