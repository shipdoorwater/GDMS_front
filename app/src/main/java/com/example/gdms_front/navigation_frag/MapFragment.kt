package com.example.gdms_front.navigation_frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.gdms_front.R
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource

class MapFragment : Fragment() {

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view  = inflater.inflate(R.layout.fragment_map, container, false)

        view.findViewById<ImageView>(R.id.nav_allMenu_img).setOnClickListener {
            it.findNavController().navigate((R.id.action_mapFragment_to_allMenuFragment))
        }

        view.findViewById<ImageView>(R.id.nav_profit_img).setOnClickListener {
            it.findNavController().navigate((R.id.action_mapFragment_to_profitFragment))
        }

        view.findViewById<ImageView>(R.id.nav_pay_img).setOnClickListener {
            it.findNavController().navigate((R.id.action_mapFragment_to_payFragment))
        }

        view.findViewById<ImageView>(R.id.nav_main_img).setOnClickListener {
            it.findNavController().navigate((R.id.action_mapFragment_to_mainFragment))
        }

        return view
    }
}