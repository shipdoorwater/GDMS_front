package com.example.gdms_front.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gdms_front.profit.PackNo0Fragment
import com.example.gdms_front.profit.PackNo1Fragment
import com.example.gdms_front.profit.PackNo2Fragment
import com.example.gdms_front.profit.PackNo3Fragment
import com.example.gdms_front.profit.PackNo4Fragment
import com.example.gdms_front.profit.PackNo5Fragment
import com.example.gdms_front.profit.PackNo6Fragment
import com.example.gdms_front.profit.PackNo7Fragment
import com.example.gdms_front.profit.PackNo8Fragment
import com.example.gdms_front.profit.PackNo9Fragment

class SubItemsAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 10 // Fragment의 개수
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PackNo0Fragment()
            1 -> PackNo1Fragment()
            2 -> PackNo2Fragment()
            3 -> PackNo3Fragment()
            4 -> PackNo4Fragment()
            5 -> PackNo5Fragment()
            6 -> PackNo6Fragment()
            7 -> PackNo7Fragment()
            8 -> PackNo8Fragment()
            9 -> PackNo9Fragment()

            // 될때 마다 추가

            else -> PackNo0Fragment()
        }
    }
}

