package com.example.gdms_front.profit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.adapter.SubscriptionAdapter
import com.example.gdms_front.model.ServicePack
import com.example.gdms_front.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubActivity1 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var subscriptionAdapter: SubscriptionAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub1)

        Log.d("SubActivity1", "onCreate")

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadSubscriptionPacks()
    }

    private fun loadSubscriptionPacks() {
        // Retrofit 호출로 서비스 팩 데이터 가져오기
        val call = RetrofitClient.apiService.getServicePacks()
        call.enqueue(object : Callback<List<ServicePack>> {
            override fun onResponse(call: Call<List<ServicePack>>, response: Response<List<ServicePack>>) {
                if (response.isSuccessful) {

                    val servicePacks = response.body() ?: emptyList()
                    subscriptionAdapter = SubscriptionAdapter(this@SubActivity1, servicePacks)
                    recyclerView.adapter = subscriptionAdapter
//                    val servicePacks = response.body() ?: emptyList()
//                    subscriptionAdapter = SubscriptionAdapter(servicePacks) { servicePack ->
//                        // 상세 페이지로 이동하는 코드 추가
//                        val intent = Intent(this@SubActivity1, ServicePackDetailActivity::class.java).apply {
//                            putExtra("packId", servicePack.packId)
//                        }
//                        startActivity(intent)
//                    }
//                    recyclerView.adapter = subscriptionAdapter
                }
            }

            override fun onFailure(call: Call<List<ServicePack>>, t: Throwable) {
                // 실패 처리
            }
        })
    }
}