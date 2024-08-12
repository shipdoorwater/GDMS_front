package com.example.gdms_front.profit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gdms_front.MainActivity
import com.example.gdms_front.R
import com.example.gdms_front.model.KakaoPayRequest
import com.example.gdms_front.model.KakaoPayResponse
import com.example.gdms_front.network.RetrofitClient.apiService
import retrofit2.Call

class KakaoPayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kakao_pay)

        val webView = findViewById<WebView>(R.id.web_view)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        val redirectUrl = intent.getStringExtra("redirectUrl")
        if (redirectUrl != null) {
            Log.d("카카오페이엑티비티", "Redirect URL: $redirectUrl")
            webView.loadUrl(redirectUrl)

        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                Log.d("WebView", "Navigating to URL: $url")

                // 특정 URL을 감지하여 처리
                if (url.contains("pg_token")) {
                    // 결제 성공 URL을 감지했을 때 처리
                    val pgToken = extractPgToken(url)
                    Log.d("피지토큰 확인용", "pgToken: $pgToken")
                    
                    //여기에 API로 최종 결제하는거 보내줘야 함
                    val tid = intent.getStringExtra("tid")
                    val userId = intent.getStringExtra("userId")
                    val packId = intent.getIntExtra("packId", 0)
                    val amountPaid = intent.getIntExtra("amountPaid", 0)
                    val packName = intent.getStringExtra("packName")
                    val packBrief = intent.getStringExtra("packBrief")

                    Log.d("카카오페이엑티비티변수", "tid: $tid, userId: $userId, packId: $packId, amountPaid: $amountPaid)")
                    //val kakaoPay = KakaoPayRequest(pgToken.toString(), tid.toString(), userId.toString(), packId, amountPaid)

                    val call = apiService.kakaoPayRequest(
                        pgToken = pgToken.toString(),
                        tid = tid.toString(),
                        userId = userId.toString(),
                        packId = packId,
                        amountPaid=amountPaid
                    )

                    call.enqueue(object : retrofit2.Callback<KakaoPayResponse> {
                        override fun onResponse(call: Call<KakaoPayResponse>, response: retrofit2.Response<KakaoPayResponse>) {
                            if (response.isSuccessful) {
                                val apiResponse = response.body()

                                if (apiResponse != null) {
                                    Log.d("카카오페이완료후", "Server message: ${apiResponse.message}")

                                }
                            } else {
                                Log.d("카카오페이완료후", "Server error: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<KakaoPayResponse>, t: Throwable) {
                            Log.d("카카오페이완료후", "Network error: ${t.message}")
                        }
                    })

                    handlePaymentSuccess(url)
                    return true


                } else if (url.contains("your_cancel_url")) {
                    // 결제 취소 URL을 감지했을 때 처리
                    handlePaymentCancel(url)
                    return true
                }

                return false // 기본 동작을 계속 진행
            }
        }
    }

    private fun extractPgToken(url: String): String? {
        val uri = Uri.parse(url)
        return uri.getQueryParameter("pg_token")
    }


    private fun handlePaymentSuccess(url: String) {
        // 성공 URL에서 필요한 정보를 추출하고 처리
        Log.d("Payment", "Payment successful: $url")

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun handlePaymentCancel(url: String) {
        // 취소 URL에서 필요한 정보를 추출하고 처리
        Log.d("Payment", "Payment cancelled: $url")
        // 필요한 작업 수행

    }
}



