package com.example.gdms_front.profit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gdms_front.R

class SecondTierFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_tier_second, container, false)

        val backBtn = view.findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            requireActivity().finish()
        }

        val tier2Subscription = view.findViewById<TextView>(R.id.tier2_Subscription)
        tier2Subscription.setOnClickListener {
            Log.d("SecondTierFragment", "tier2_Subscription clicked")

            val intent = Intent(context, SubActivity1::class.java)
            startActivity(intent)
        }

        return view
    }

}