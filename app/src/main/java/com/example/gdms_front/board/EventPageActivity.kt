package com.example.gdms_front.board

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.adapter.NoticeAdapter
import com.example.gdms_front.model.NoticeResponse
import com.example.gdms_front.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventPageActivity : AppCompatActivity() {

    private lateinit var noticeRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_page)

        noticeRecyclerView = findViewById(R.id.noticeRecyclerView)
        noticeRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchNotices()

        }

    private fun fetchNotices() {
        val call = RetrofitClient.noticeApiService.getNotices("event") //
        call.enqueue(object : Callback<List<NoticeResponse>> {
            override fun onResponse(call: Call<List<NoticeResponse>>, response: Response<List<NoticeResponse>>) {
                if (response.isSuccessful) {
                    val notices = response.body() ?: emptyList()
                    noticeRecyclerView.adapter = NoticeAdapter(notices)
                } else {
                    Log.e("EventPageActivity", "Failed to fetch notices: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@EventPageActivity, "Failed to fetch notices", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<NoticeResponse>>, t: Throwable) {
                Log.e("EventPageActivity", "Network error", t)
                Toast.makeText(this@EventPageActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}


