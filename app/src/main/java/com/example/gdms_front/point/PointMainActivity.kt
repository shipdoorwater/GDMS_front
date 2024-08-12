package com.example.gdms_front.point

import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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

        // 자동차보험료 결제 웹뷰로 연결
        val constraintLayout8 = findViewById<ConstraintLayout>(R.id.constraintLayout8)
        constraintLayout8.setOnClickListener {
            val intent = Intent(this, PointWebViewActivity::class.java)
            intent.putExtra("url", "https://m.kbinsure.co.kr:8547/dctapp/main.html#/car/gi/0001M?joinMall=Y&recntCal=N&OfactAuthEcpt=Y&pid=1090049&code=1035&redirectYn=Y")
            startActivity(intent)
        }

        // 운전자보험료 결제 웹뷰로 연결
        val constraintLayout9 = findViewById<ConstraintLayout>(R.id.constraintLayout9)
        constraintLayout9.setOnClickListener {
            val intent = Intent(this, PointWebViewActivity::class.java)
            intent.putExtra("url", "https://m.kbinsure.co.kr:8547/dctapp/main.html#/GLM/DR/LT_CM0101M?pid=5290334&code=0001&redirectYn=Y")
            startActivity(intent)
        }

        // KB Pay 앱 싫행
        val constraintLayout10 = findViewById<ConstraintLayout>(R.id.constraintLayout10)
        constraintLayout10.setOnClickListener {
            openAppOrPlayStore("com.kbcard.cxh.appcard") // KB Pay 앱의 실제 패키지 이름으로 교체해야 합니다
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

    fun openAppOrPlayStore(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        Log.d("AppLaunch", "before Intent found: $intent")
        if (intent != null) {
            // 앱이 설치되어 있으면 앱을 실행
            Log.d("AppLaunch", "Intent found: $intent")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            // 앱이 설치되어 있지 않으면 Play 스토어로 이동
            try {
//                val customIntent = Intent()
//                customIntent.setClassName("com.kbcard.cxh.appcard", "com.kbcard.cxh.appcard.screen.main.KBPayMainActivity") // 실제 Activity 이름으로 변경
//                customIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(customIntent)
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            } catch (e: android.content.ActivityNotFoundException) {
                Log.d("AppLaunch", "Direct Activity launch failed: ${e.message}")
                // Play 스토어 앱이 없는 경우 웹 브라우저로 Play 스토어 페이지 열기
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
        }
    }

}