package com.example.gdms_front.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gdms_front.profit.FirstTierFrgment
import com.example.gdms_front.profit.SecondTierFragment
import com.example.gdms_front.profit.ThirdTierFragment

class TierExpAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3 // Fragment의 개수
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FirstTierFrgment()
            1 -> SecondTierFragment()
            2 -> ThirdTierFragment()

            else -> FirstTierFrgment()
        }
    }
}