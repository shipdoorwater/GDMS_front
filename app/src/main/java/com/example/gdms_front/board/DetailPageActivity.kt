package com.example.gdms_front.board

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gdms_front.R
import com.example.gdms_front.model.NoticeResponse
import com.example.gdms_front.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailPageActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var dateTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_page)

        titleTextView = findViewById(R.id.titleTextView)
        contentTextView = findViewById(R.id.contentTextView)
        dateTextView = findViewById(R.id.dateTextView)

        val subId = intent.getIntExtra("subId", -1)
        val boardId = intent.getIntExtra("boardId", -1)

        if (subId != -1 && boardId != -1) {
            val subIdString = when (subId) {
                1 -> "notice"
                2 -> "event"
                else -> ""
            }
            if (subIdString.isNotEmpty()) {
                fetchNoticeDetail(subIdString, boardId)
            } else {

            }
        } else {

        }

        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            finish()
        }

    }

    private fun fetchNoticeDetail(subId: String, boardId: Int) {
        val call = RetrofitClient.noticeApiService.getNoticeDetail(subId, boardId)
        call.enqueue(object : Callback<NoticeResponse> {
            override fun onResponse(call: Call<NoticeResponse>, response: Response<NoticeResponse>) {
                if (response.isSuccessful) {
                    val notice = response.body()
                    if (notice != null) {
                        titleTextView.text = notice.title
                        contentTextView.text = notice.content
                        dateTextView.text = notice.boardDate
                    } else {

                    }
                } else {
                    Log.e("DetailPageActivity", "Failed to fetch details: ${response.code()} - ${response.message()}")

                }
            }

            override fun onFailure(call: Call<NoticeResponse>, t: Throwable) {
                Log.e("DetailPageActivity", "Network error", t)

            }
        })
    }
}