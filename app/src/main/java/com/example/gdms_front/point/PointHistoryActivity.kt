package com.example.gdms_front.point

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.adapter.PointHistoryAdapter
import com.example.gdms_front.model.PointDetailResponse
import com.example.gdms_front.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PointHistoryActivity : AppCompatActivity() {

    private var allPoints: List<PointDetailResponse> = emptyList()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PointHistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_history)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        val filterSpinner: Spinner = findViewById(R.id.filterSpinner)
        val filters = arrayOf("전체", "적립", "사용")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filters)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = spinnerAdapter


        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterPoints(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val sharedPreference = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreference?.getString("token", null)

        if (userId != null) {
            getPointDetails(userId)
        }

        // 뒤로 가기
        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            finish()
        }
    }

    private fun getPointDetails(userId: String) {
        RetrofitClient.payApiService.getPointDetails(userId).enqueue(object :
            Callback<List<PointDetailResponse>> {
            override fun onResponse(call: Call<List<PointDetailResponse>>, response: Response<List<PointDetailResponse>>) {
                if (response.isSuccessful) {
                    allPoints = response.body() ?: emptyList()
                    Log.d("PointHistoryAPITest", allPoints.toString())
                    adapter = PointHistoryAdapter(allPoints)
                    recyclerView.adapter = adapter
                } else {

                }
            }

            override fun onFailure(call: Call<List<PointDetailResponse>>, t: Throwable) {

            }
        })
    }

    private fun filterPoints(category: Int) {
        val filteredPoints = when (category) {
            1 -> allPoints.filter { it.pointCategory == 1 || it.pointCategory == 2 } // 적립
            2 -> allPoints.filter { it.pointCategory == 3 } // 사용
            else -> allPoints // 전체
        }
        if (::adapter.isInitialized) {
            adapter.updateData(filteredPoints)
        } else {
            Log.e("PointHistoryActivity", "Adapter is not initialized yet")
        }
    }
}