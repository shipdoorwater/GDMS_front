package com.example.gdms_front.navigation_frag

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.gdms_front.R
import com.example.gdms_front.model.Subscription
import com.example.gdms_front.network.RetrofitClient
import com.example.gdms_front.profit.TierExpActivity
import com.google.android.gms.common.api.Response
import retrofit2.Call
import retrofit2.Callback
import java.util.Date
import java.util.Locale


class ProfitFragment : Fragment() {

   // private lateinit var viewPager: ViewPager2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ProfitFragment", "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("ProfitFragment", "onCreateView: ")

        val view = inflater.inflate(R.layout.fragment_profit, container, false)

        // 뷰 참조 가져오기
        val truePage: LinearLayout? = view.findViewById(R.id.truePage)
        val falsePage: LinearLayout? = view.findViewById(R.id.falsePage)

        // 예제 조건
        val isBenefitAvailable = true // true면 구독 안함 / false 면 구독 중

        // 조건에 따라 뷰를 보여줍니다
        truePage?.visibility = if (isBenefitAvailable) View.VISIBLE else View.GONE
        falsePage?.visibility = if (isBenefitAvailable) View.GONE else View.VISIBLE

        
        // tier1 안내 화면
        view.findViewById<LinearLayout>(R.id.tier1).setOnClickListener {
            val intent = Intent(this@ProfitFragment.requireContext(), TierExpActivity::class.java)
            intent.putExtra("FRAGMENT_INDEX", 0) // 1번 FRAG는 0부터 시작
            startActivity(intent)
        }

        // tier2 안내 화면
        view.findViewById<LinearLayout>(R.id.tier2).setOnClickListener {
            val intent = Intent(this@ProfitFragment.requireContext(), TierExpActivity::class.java)
            intent.putExtra("FRAGMENT_INDEX", 1)
            startActivity(intent)
        }

        // tier3 안내 화면
        view.findViewById<LinearLayout>(R.id.tier3).setOnClickListener {
            val intent = Intent(this@ProfitFragment.requireContext(), TierExpActivity::class.java)
            intent.putExtra("FRAGMENT_INDEX", 2)
            startActivity(intent)
        }

        view.findViewById<ConstraintLayout>(R.id.nav_allMenu).setOnClickListener {
            it.findNavController().navigate((R.id.action_profitFragment_to_allMenuFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_pay).setOnClickListener {
            it.findNavController().navigate((R.id.action_profitFragment_to_payFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_map).setOnClickListener {
            it.findNavController().navigate((R.id.action_profitFragment_to_mapFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_main).setOnClickListener {
            it.findNavController().navigate((R.id.action_profitFragment_to_mainFragment))
        }

//        viewPager = view.findViewById(R.id.tierViewPager)
//        viewPager.adapter = TierExplainAdapter(requireActivity())
//
//        view.findViewById<LinearLayout>(R.id.tier3).setOnClickListener {
//            viewPager.currentItem = 2
//        }
//
//        view.findViewById<LinearLayout>(R.id.tier2).setOnClickListener {
//            viewPager.currentItem = 1
//        }
//
//        view.findViewById<LinearLayout>(R.id.tier1).setOnClickListener {
//            viewPager.currentItem = 0
//        }

        return view
    }


}