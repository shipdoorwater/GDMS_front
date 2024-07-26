package com.example.gdms_front.point

import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gdms_front.R
import com.example.gdms_front.model.MyPointResponse
import com.example.gdms_front.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PointMainActivity : AppCompatActivity() {

    private lateinit var myPointText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_main)

        myPointText = findViewById(R.id.myPointText)

        val sharedPreference = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreference?.getString("token", null)

        if (userId != null) {
            getMyPoint(userId)
        }

        // 출금 버튼
        findViewById<Button>(R.id.withDrawBtn).setOnClickListener {
            val intent = Intent(this, WithdrawPointActivity::class.java)
            startActivity(intent)
        }

        // 뒤로 가기 버튼
        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            finish()
        }

        // 포인트 버튼
        myPointText.setOnClickListener {
            val intent = Intent(this, PointHistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getMyPoint(userId: String) {
        RetrofitClient.payApiService.getMyPoint(userId).enqueue(object : Callback<MyPointResponse> {
            override fun onResponse(
                call: Call<MyPointResponse>,
                response: Response<MyPointResponse>
            ) {
                if (response.isSuccessful) {
                    val totalPoints = response.body()?.totalPoints ?: 0
                    myPointText.text =formatNumberWithComma(totalPoints)
                } else {
                    Log.d("PointTest", "실패")
                }
            }

            override fun onFailure(call: Call<MyPointResponse>, t: Throwable) {
                Log.d("PointTest", "에러: ${t.message}")
            }
        })
    }

    private fun formatNumberWithComma(number: Int): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(number)
    }

}