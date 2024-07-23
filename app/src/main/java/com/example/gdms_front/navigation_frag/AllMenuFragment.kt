package com.example.gdms_front.navigation_frag

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.example.gdms_front.R

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

        return view
    }

}