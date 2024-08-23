package com.example.gdms_front.point

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.DecimalFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
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
        findViewById<CardView>(R.id.withDrawBtn).setOnClickListener {
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
            val intent = Intent(this, PointWebViewActivity2::class.java)
            intent.putExtra("url", "https://m.kbinsure.co.kr:8547/dctapp/main.html#/car/gi/0001M?joinMall=Y&recntCal=N&OfactAuthEcpt=Y&pid=1090049&code=1035&redirectYn=Y")
            startActivity(intent)
        }

        // 운전자보험료 결제 웹뷰로 연결
        val constraintLayout9 = findViewById<ConstraintLayout>(R.id.constraintLayout9)
        constraintLayout9.setOnClickListener {
            val intent = Intent(this, PointWebViewActivity2::class.java)
            intent.putExtra("url", "https://m.kbinsure.co.kr:8547/dctapp/main.html#/GLM/DR/LT_CM0101M?pid=5290334&code=0001&redirectYn=Y")
            startActivity(intent)
        }

        // KB Pay 앱 실행
        val constraintLayout10 = findViewById<ConstraintLayout>(R.id.constraintLayout10)
        constraintLayout10.setOnClickListener {
            launchKBPayApp()
        }

        // KB 스타뱅킹 앱 실행
        val constraintLayout7 = findViewById<ConstraintLayout>(R.id.constraintLayout7)
        constraintLayout7.setOnClickListener {
            val intent = Intent(this, PointWebViewActivity::class.java)
            intent.putExtra("url", "https://obank.kbstar.com/quics?page=C041244&scheme=kbbank&pageid=D004895")
            startActivity(intent)
        }

        // 이벤트 페이지 이동
        val goToEventBtn = findViewById<ImageView>(R.id.goToEventBtn)
        goToEventBtn.setOnClickListener {
            val intent = Intent(this, PointWebViewActivity::class.java)
            intent.putExtra("url", "https://obank.kbstar.com/quics?page=C041244&scheme=kbbank&pageid=C052754&urlparam=%EC[…]EB%B2%A4%ED%8A%B8%EC%9D%BC%EB%A0%A8%EB%B2%88%ED%98%B8:324079")
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

    private fun launchKBPayApp() {
        val kbPayPackageName = "com.kbcard.cxh.appcard"

        if (isPackageInstalled(kbPayPackageName)) {
            val launchIntent = packageManager.getLaunchIntentForPackage(kbPayPackageName)
            if (launchIntent != null) {
                startActivity(launchIntent)
            } else {
                openPlayStore(kbPayPackageName)
            }
        } else {
            // KB Pay 앱이 설치되어 있지 않으면 Play Store로 이동
            openPlayStore(kbPayPackageName)
        }

//        if (isPackageInstalled(kbPayPackageName)) {
//            // KB Pay 앱이 설치되어 있으면 실행
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(kbPayScheme))
//            intent.setPackage(kbPayPackageName)
//            try {
//                startActivity(intent)
//            } catch (e: ActivityNotFoundException) {
//                // 스킴으로 실행 실패 시 일반적인 방법으로 앱 실행
//                val launchIntent = packageManager.getLaunchIntentForPackage(kbPayPackageName)
//                if (launchIntent != null) {
//                    startActivity(launchIntent)
//                } else {
//                    openPlayStore(kbPayPackageName)
//                }
//            }
//        } else {
//            // KB Pay 앱이 설치되어 있지 않으면 Play Store로 이동
//            openPlayStore(kbPayPackageName)
//        }
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun openPlayStore(packageName: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

}