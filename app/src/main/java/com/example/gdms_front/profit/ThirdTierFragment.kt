package com.example.gdms_front.profit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.adapter.SubscriptionAdapter
import com.example.gdms_front.model.ServicePack
import com.example.gdms_front.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ThirdTierFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var subscriptionAdapter: SubscriptionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tier_third, container, false)

        val backBtn = view.findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            requireActivity().finish()
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        loadSubscriptionPacks()

        return view
    }

    private fun loadSubscriptionPacks() {
        // Retrofit 호출로 서비스 팩 데이터 가져오기
        val call = RetrofitClient.apiService.getServicePacks()
        call.enqueue(object : Callback<List<ServicePack>> {
            override fun onResponse(call: Call<List<ServicePack>>, response: Response<List<ServicePack>>) {
                if (response.isSuccessful) {
                    val servicePacks = response.body() ?: emptyList()
                    subscriptionAdapter = SubscriptionAdapter(requireContext(), servicePacks)
                    recyclerView.adapter = subscriptionAdapter
                }
            }

            override fun onFailure(call: Call<List<ServicePack>>, t: Throwable) {
                // 실패 처리
            }
        })
    }
}

