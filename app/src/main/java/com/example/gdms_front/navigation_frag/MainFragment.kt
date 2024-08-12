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
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.example.gdms_front.account.AccountActivity
import com.example.gdms_front.point.PointMainActivity
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


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
        val imageView1 = view.findViewById<ImageView>(R.id.imageView1_lucky).apply { tag = "cat" }
        val imageView2 = view.findViewById<ImageView>(R.id.imageView2_point).apply { tag = "pig" }
        val imageView3 = view.findViewById<ImageView>(R.id.imageView3_map).apply {tag = "shlef"}
        val imageView4 = view.findViewById<ImageView>(R.id.imageView4_pack).apply {tag = "gift"}
        val imageView5 = view.findViewById<ImageView>(R.id.imageView5_news).apply {tag = "envelope"}
        val imageView6 = view.findViewById<ImageView>(R.id.imageView6_account).apply { tag = "chart" }

//        loadGif(imageView1, R.drawable.wired_flat_1957_maneki_cat, "cat")
//        loadGif(imageView2, R.drawable.wired_flat_453_savings_pig, "pig")
//        loadGif(imageView4, R.drawable.wired_flat_412_gift, "gift")
//        loadGif(imageView3, R.drawable.wired_flat_1360_grocery_shelf, "shlef")
//        loadGif(imageView5, R.drawable.wired_flat_177_envelope_send, "envelope")
//        loadGif(imageView6, R.drawable.wired_flat_153_bar_chart, "chart" )

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

//    private fun loadGif(imageView: ImageView, gifResourceId: Int, tag: String) {
//        Glide.with(this)
//            .asGif()
//            .load(gifResourceId)
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            .into(object : CustomTarget<GifDrawable>() {
//                override fun onResourceReady(resource: GifDrawable, transition: Transition<in GifDrawable>?) {
//                    if (imageView.tag == tag) {
//                        imageView.setImageDrawable(resource)
//                    }
//                }
//                override fun onLoadCleared(placeholder: Drawable?) {
//                    imageView.setImageDrawable(placeholder)
//                }
//            })
//    }

    private fun loadGif(imageView: ImageView, gifResourceId: Int) {
        Glide.with(this)
            .asGif()
            .load(gifResourceId)
            .diskCacheStrategy(DiskCacheStrategy.NONE) // 캐시 비활성화
            .skipMemoryCache(true) // 메모리 캐시 비활성화
            .into(imageView)
    }
}