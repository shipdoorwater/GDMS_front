package com.example.gdms_front.navigation_frag

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.example.gdms_front.R
import com.example.gdms_front.account.AccountActivity
import com.example.gdms_front.auth.LoginActivity
import com.example.gdms_front.board.EventPageActivity
import com.example.gdms_front.board.NoticePageActivity

import com.example.gdms_front.lucky.LuckyActivity
import com.example.gdms_front.model.MemberInfoResponse
import com.example.gdms_front.network.RetrofitClient.myPageApiService
import com.example.gdms_front.news.NewsActivity
import com.example.gdms_front.point.PointMainActivity
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllMenuFragment : Fragment() {

    private lateinit var view: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        view = inflater.inflate(R.layout.fragment_all_menu, container, false)

        val sharedPreference = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreference?.getString("token", null)

        if (userId != null) {
            getMemberInfo(userId)
        }


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

        // 공지사항 텍스트 클릭 시
        view.findViewById<TextView>(R.id.noticeBtn).setOnClickListener {
            val intent = Intent(activity, NoticePageActivity::class.java)
            startActivity(intent)
        }

        // 이벤트 텍스트 클릭 시
        view.findViewById<TextView>(R.id.eventBtn).setOnClickListener {
            val intent = Intent(activity, EventPageActivity::class.java)
            startActivity(intent)
        }

        // 뉴스 텍스트 클릭 시
        view.findViewById<TextView>(R.id.newsBtn).setOnClickListener {
            val intent = Intent(activity, NewsActivity::class.java)
            startActivity(intent)
        }

        // 가계부 텍스트 클릭 시
        view.findViewById<TextView>(R.id.accountBtn).setOnClickListener {
            val intent = Intent(activity, AccountActivity::class.java)
            startActivity(intent)
        }

        // 오늘의 운세 텍스트 클릭 시
        view.findViewById<TextView>(R.id.goToLuckyBtn).setOnClickListener {
            val intent = Intent(activity, LuckyActivity::class.java)
            startActivity(intent)
        }

        // 구독관리 텍스트 클릭 시
        view.findViewById<TextView>(R.id.subscribeBtn).setOnClickListener {
            it.findNavController().navigate((R.id.action_allMenuFragment_to_profitFragment))
        }

        return view
    }

    private fun getMemberInfo(userId: String) {
        myPageApiService.getMemberInfo(userId).enqueue(object : Callback<MemberInfoResponse> {
            override fun onResponse(
                call: Call<MemberInfoResponse>,
                response: Response<MemberInfoResponse>
            ) {
                if (response.isSuccessful) {
                    val memberInfo = response.body()
                    if (memberInfo != null) {
                        view.findViewById<TextView>(R.id.userName).text = "${memberInfo.userName}님 안녕하세요"
                        Log.d("AllMenuFragment", "Member info retrieved successfully: ${memberInfo.userName}")
                    } else {
                        Log.e("AllMenuFragment", "Member info is null")
                    }
                } else if (response.code() == 404) {
                    Log.e("AllMenuFragment", "404 Error: User not found")
                } else {
                    Log.e("AllMenuFragment", "Error: ${response.code()}, ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MemberInfoResponse>, t: Throwable) {
                Log.e("AllMenuFragment", "Network failure: ${t.message}", t)
            }
        })
    }
}