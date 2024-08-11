package com.example.gdms_front.auth

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gdms_front.R

class SearchLocationActivity : AppCompatActivity() {

    private lateinit var browser: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_location)

        browser = findViewById<WebView>(R.id.webView)
        browser.settings.javaScriptEnabled = true

        browser.addJavascriptInterface(MyJavaScriptInterface(), "Android")

        browser.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
        //page loading을 끝냈을 때 호출되는 콜백 메서드
        //안드로이드에서 자바스크립트 메서드 호출
                        browser.loadUrl("javascript:sample2_execDaumPostcode();")
                    }
                })
        //최초로 웹뷰 로딩
        browser.loadUrl("https://gdms-afb47.web.app")
    }

    inner class MyJavaScriptInterface {
        @JavascriptInterface
        fun processDATA(data: String?) {
            //자바 스크립트로 부터 다음 카카오 주소 검색 API 결과를 전달 받는다.
            val extra = Bundle()
            val intent = Intent()

            extra.putString("data", data)
            intent.putExtras(extra)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}