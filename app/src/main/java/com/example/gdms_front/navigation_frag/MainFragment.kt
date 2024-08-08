package com.example.gdms_front.navigation_frag

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.gdms_front.R
import com.example.gdms_front.alarm.NotificationViewModel
import com.example.gdms_front.auth.LoginActivity
import com.example.gdms_front.news.NewsActivity
import androidx.lifecycle.ViewModelProvider


class MainFragment : Fragment() {
    private lateinit var notificationViewModel: NotificationViewModel
    private val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return NotificationViewModel(requireActivity().application) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationViewModel = ViewModelProvider(this, factory).get(NotificationViewModel::class.java)

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

        view.findViewById<Button>(R.id.dbInitBtn).setOnClickListener {
            notificationViewModel.clearAllNotifications()
        }


        return view
    }
}