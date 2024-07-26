package com.example.gdms_front.navigation_frag

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.gdms_front.R
import com.example.gdms_front.auth.LoginActivity
import com.example.gdms_front.news.NewsActivity
import com.example.gdms_front.qr_pay.QrPayActivity
import com.google.zxing.integration.android.IntentIntegrator

class MainFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreference = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreference?.getString("token", null)
        Log.d("아이디 들어오나 확인", token.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        view.findViewById<ConstraintLayout>(R.id.nav_allMenu).setOnClickListener {
            it.findNavController().navigate((R.id.action_mainFragment_to_allMenuFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_profit).setOnClickListener {
            it.findNavController().navigate((R.id.action_mainFragment_to_profitFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_pay).setOnClickListener {
            it.findNavController().navigate((R.id.action_mainFragment_to_payFragment))
            //IntentIntegrator.forSupportFragment(this@MainFragment).initiateScan()
        }

        view.findViewById<ConstraintLayout>(R.id.nav_map).setOnClickListener {
            it.findNavController().navigate((R.id.action_mainFragment_to_mapFragment))
        }

        //로그아웃 버튼 기능 임시로 넣어놨음
        val logoutBtn = view.findViewById<Button>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            // SharedPreferences에서 토큰 삭제
            val sharedPreferences = context?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.remove("token")
            editor?.apply()

            // 로그인 화면으로 이동
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        val newsBtn = view.findViewById<Button>(R.id.newsBtn)
        newsBtn.setOnClickListener {
            val intent = Intent(context, NewsActivity::class.java)
            startActivity(intent)
        }


        return view
    }
/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                // QR Code successfully scanned
                val qrData = result.contents
                val intent = Intent(activity, QrPayActivity::class.java)
                intent.putExtra("QR_DATA", qrData)
                startActivity(intent)
            }
        }
    }*/
}