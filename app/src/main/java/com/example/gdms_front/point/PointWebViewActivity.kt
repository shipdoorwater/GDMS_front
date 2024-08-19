package com.example.gdms_front.point

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gdms_front.R

class PointWebViewActivity : AppCompatActivity() {

    private val TAG = "PointWebViewActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_web_view)

        val webView = findViewById<WebView>(R.id.pointWebView)
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                request?.url?.let { uri ->
                    when (uri.scheme) {
                        "kbbank" -> {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                if (intent.resolveActivity(packageManager) != null) {
                                    startActivity(intent)
                                    return true
                                } else {
                                    // KB 은행 앱이 설치되어 있지 않은 경우
                                    val playStoreUri =
                                        Uri.parse("market://details?id=com.kbstar.kbbank")
                                    val playStoreIntent = Intent(Intent.ACTION_VIEW, playStoreUri)
                                    if (playStoreIntent.resolveActivity(packageManager) != null) {
                                        startActivity(playStoreIntent)
                                        return true
                                    } else {
                                        // Play Store도 없는 경우, 웹 브라우저로 Play Store 페이지 열기
                                        val webUri =
                                            Uri.parse("https://play.google.com/store/apps/details?id=com.kbstar.kbbank")
                                        val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                                        startActivity(webIntent)
                                        return true
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error handling kbbank scheme", e)
                            }
                        }

                        "intent" -> {
                            try {
                                val intent =
                                    Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME)
                                if (intent.resolveActivity(packageManager) != null) {
                                    startActivity(intent)
                                    return true
                                }
                                // 폴백 URL이 있다면 처리
                                val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                                if (fallbackUrl != null) {
                                    view?.loadUrl(fallbackUrl)
                                    return true
                                } else {

                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing intent URL", e)
                            }
                        }

                        else -> {}
                    }
                }
                return false
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                error?.let {
                    Log.e(TAG, "WebView Error: ${it.errorCode} - ${it.description}")
                }
                super.onReceivedError(view, request, error)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d(TAG, "Page Loaded: $url")
                super.onPageFinished(view, url)
            }
        }

        val url = intent.getStringExtra("url")
        url?.let {
            Log.d(TAG, "Loading URL: $it")
            webView.loadUrl(it)
        } ?: Log.e(TAG, "No URL provided")
    }
}