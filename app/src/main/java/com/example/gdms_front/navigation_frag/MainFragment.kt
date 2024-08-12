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
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.gdms_front.R
import com.example.gdms_front.alarm.NotificationViewModel
import com.example.gdms_front.auth.LoginActivity
import com.example.gdms_front.news.NewsActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gdms_front.account.AccountActivity
import com.example.gdms_front.point.PointMainActivity


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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)


        // GIF 이미지 로드
        val imageView1: ImageView = view.findViewById(R.id.imageView1_lucky)
        val imageView2: ImageView = view.findViewById(R.id.imageView2_point)
        val imageView3: ImageView = view.findViewById(R.id.imageView3_map)
        val imageView4: ImageView = view.findViewById(R.id.imageView4_pack)
        val imageView5: ImageView = view.findViewById(R.id.imageView5_news)
        val imageView6: ImageView = view.findViewById(R.id.imageView6_account)

        loadGif(imageView1, R.drawable.wired_flat_1957_maneki_cat)
        loadGif(imageView2, R.drawable.wired_flat_453_savings_pig)
        loadGif(imageView3, R.drawable.wired_flat_1360_grocery_shelf)
        loadGif(imageView4, R.drawable.wired_flat_412_gift)
        loadGif(imageView5, R.drawable.wired_flat_177_envelope_send)
        loadGif(imageView6, R.drawable.wired_flat_153_bar_chart)

        // 오늘의 운세
//        view.findViewById<CardView>(R.id.cardView1).setOnClickListener {
//            startActivity(Intent(activity, ::class.java))
//        }

        // 내 포인트
        view.findViewById<CardView>(R.id.cardView2).setOnClickListener {
            startActivity(Intent(activity, PointMainActivity::class.java))
        }

        // 맛집 추천
        view.findViewById<CardView>(R.id.cardView3).setOnClickListener {
            it.findNavController().navigate((R.id.action_mainFragment_to_mapFragment))
        }

        // 구독혜택 안내
        view.findViewById<CardView>(R.id.cardView4).setOnClickListener {
            it.findNavController().navigate((R.id.action_mainFragment_to_profitFragment))
        }

        // 뉴스레터
        view.findViewById<CardView>(R.id.cardView5).setOnClickListener {
            startActivity(Intent(activity, NewsActivity::class.java))
        }

        // 가계부
        view.findViewById<CardView>(R.id.cardView6).setOnClickListener {
            startActivity(Intent(activity, AccountActivity::class.java))
        }


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

        return view
    }

    private fun loadGif(imageView: ImageView, gifResourceId: Int) {
        Glide.with(this)
            .asGif()
            .load(gifResourceId)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }
}