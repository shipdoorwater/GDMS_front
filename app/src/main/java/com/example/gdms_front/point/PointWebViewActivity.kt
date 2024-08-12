package com.example.gdms_front.point

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gdms_front.R

class PointWebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_web_view)

        val webView = findViewById<WebView>(R.id.pointWebView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        val url = intent.getStringExtra("url")
        url?.let { webView.loadUrl(it) }
    }
}