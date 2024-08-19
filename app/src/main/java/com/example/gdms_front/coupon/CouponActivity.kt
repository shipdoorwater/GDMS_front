package com.example.gdms_front.coupon

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.adapter.CouponAdapter
import com.example.gdms_front.model.Coupon
import com.example.gdms_front.network.ApiService
import com.example.gdms_front.network.RetrofitClient
import com.example.gdms_front.network.RetrofitClient.apiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CouponActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var couponAdapter: CouponAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val sharedPreference = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreference?.getString("token", null)

        Log.d("쿠폰확인", "User ID: $userId")

        if (userId != null) {
            fetchCoupons(userId)
        }
    }


    private fun fetchCoupons(userId: String) {

        val call = RetrofitClient.apiService.getCoupons(userId)
        Log.d("쿠폰확인", "API Call: $call")

        call.enqueue(object : Callback<List<Coupon>> {
            override fun onResponse(call: Call<List<Coupon>>, response: Response<List<Coupon>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("쿠폰확인", "Coupon List: $response")
                        couponAdapter = CouponAdapter(it)
                        recyclerView.adapter = couponAdapter
                    }
                }  else {
                    Log.e("쿠폰확인", "Error: ${response.code()}")
                    Log.e("쿠폰확인", "Error Body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Coupon>>, t: Throwable) {
                // 에러 처리
                Log.e("쿠폰확인", "Error: ${t.message}")
            }
        })
    }



}