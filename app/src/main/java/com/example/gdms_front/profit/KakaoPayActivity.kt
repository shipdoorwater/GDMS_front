package com.example.gdms_front.profit

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gdms_front.MainActivity
import com.example.gdms_front.R
import com.example.gdms_front.model.KakaoPayRequest
import com.example.gdms_front.model.KakaoPayResponse
import com.example.gdms_front.network.RetrofitClient.apiService
import retrofit2.Call

class KakaoPayActivity : AppCompatActivity() {


    private lateinit var successDialog: Dialog


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

        // 결제 성공 다이얼로그 표시
        showSuccessDialog()

        // 3초 후에 다음 액티비티로 전환
        window.decorView.postDelayed({
            dismissSuccessDialog() // 다이얼로그 닫기
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent) // 다음 액티비티로 전환
        }, 5000) // 3초 지연

//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        startActivity(intent)
    }

    private fun handlePaymentCancel(url: String) {
        // 취소 URL에서 필요한 정보를 추출하고 처리
        Log.d("Payment", "Payment cancelled: $url")
        // 필요한 작업 수행

    }

    private fun showSuccessDialog() {
        successDialog = Dialog(this)
        successDialog.apply {
            setContentView(R.layout.dialog_subscription_success) // 커스텀 레이아웃 적용
            setCancelable(false) // 다이얼로그 수동으로 닫을 수 없도록 설정
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명하게 설정

            val imageView1: ImageView = findViewById(R.id.firecracker)
            loadGif(imageView1, R.drawable.wired_flat_1103_confetti)

            show() // 다이얼로그 표시
        }
    }

    // 다이얼로그 닫기 메소드
    private fun dismissSuccessDialog() {
        if (::successDialog.isInitialized && successDialog.isShowing) {
            successDialog.dismiss() // 다이얼로그 닫기
        }
    }

    // GIF 로드 메소드
    private fun loadGif(imageView: ImageView, gifResourceId: Int) {
        Glide.with(this)
            .asGif()
            .load(gifResourceId)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)
    }

}



