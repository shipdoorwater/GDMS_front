package com.example.gdms_front

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val webView = findViewById<WebView>(R.id.webView)


        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                Log.d("WebViewTest", "Attempting to load URL: $url")

                return when {
                    url.startsWith("http://") || url.startsWith("https://") -> {
                        // HTTP 및 HTTPS URL은 WebView에서 로드
                        false
                    }
                    url.startsWith("intent://") -> {
                        Log.d("WebViewTest", "Handling intent URL: $url")
                        handleIntentUrl(url)
                        true
                    }
                    else -> {
                        Log.d("WebViewTest", "URL is not HTTP, HTTPS or intent. Opening in default browser: $url")
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, request.url)
                            startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("WebViewTest", "Error opening URL in browser: $e")
                        }
                        true
                    }
                }
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
                Log.e("WebViewTest", "WebView error: ${error.description}")
            }
        }

        webView.settings.javaScriptEnabled = true

        val naverMapUrl = intent.getStringExtra("naverMapUrl")

        Log.d("WebViewTest", "Received naverMapUrl: $naverMapUrl")
        if (naverMapUrl != null){
            webView.loadUrl(naverMapUrl)
        } else {
            Log.e("WebViewTest",  "No URL provided to WebViewActivity")
        }
    }

    private fun handleIntentUrl(url: String) {
        try {
            val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            val fallbackUrl = intent.getStringExtra("browser_fallback_url")
            if (fallbackUrl != null) {
                // fallback URL이 있는 경우 WebView에서 로드
                val webView = findViewById<WebView>(R.id.webView)
                webView.loadUrl(fallbackUrl)
            } else {
                // fallback URL이 없으면 기본 브라우저에서 처리
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        } catch (e: Exception) {
            Log.e("WebViewTest", "Error handling intent URL: $e")
        }
    }
}