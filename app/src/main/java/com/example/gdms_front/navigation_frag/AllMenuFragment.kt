package com.example.gdms_front.navigation_frag

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.example.gdms_front.R
import com.example.gdms_front.point.PointMainActivity

class AllMenuFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_all_menu, container, false)

        view.findViewById<ConstraintLayout>(R.id.nav_main).setOnClickListener {
            it.findNavController().navigate((R.id.action_allMenuFragment_to_mainFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_profit).setOnClickListener {
            it.findNavController().navigate((R.id.action_allMenuFragment_to_profitFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_pay).setOnClickListener {
            it.findNavController().navigate((R.id.action_allMenuFragment_to_payFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_map).setOnClickListener {
            it.findNavController().navigate((R.id.action_allMenuFragment_to_mapFragment))
        }

        // 플레이스 추천 텍스트 클릭 시
        view.findViewById<TextView>(R.id.goToMapBtn).setOnClickListener {
            it.findNavController().navigate((R.id.action_allMenuFragment_to_mapFragment))
        }

        // 내 포인트 텍스트 클릭 시
        view.findViewById<TextView>(R.id.goToPointBtn).setOnClickListener {
            val intent = Intent(activity, PointMainActivity::class.java)
            startActivity(intent)
        }


        return view
    }

}