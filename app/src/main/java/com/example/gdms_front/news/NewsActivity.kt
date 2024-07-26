package com.example.gdms_front.news

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.MainActivity
import com.example.gdms_front.R
import com.example.gdms_front.adapter.NewsAdapter
import com.example.gdms_front.auth.JoinActivity1
import com.example.gdms_front.model.NewsArticle

class NewsActivity : AppCompatActivity(), OnNewsClickListener {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        newsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        newsViewModel.newsList.observe(this, { news ->
            newsAdapter = NewsAdapter(news,this)
            recyclerView.adapter = newsAdapter
        })

        // Fetch all news initially
        newsViewModel.fetchAllNews()

        // Set up category buttons
        findViewById<Button>(R.id.btn_all).setOnClickListener { newsViewModel.fetchAllNews() }
        findViewById<Button>(R.id.btn_politics).setOnClickListener {
            newsViewModel.fetchNewsByCategory(
                100
            )
        }
        findViewById<Button>(R.id.btn_economy).setOnClickListener {
            newsViewModel.fetchNewsByCategory(
                101
            )
        }
        findViewById<Button>(R.id.btn_society).setOnClickListener {
            newsViewModel.fetchNewsByCategory(
                102
            )
        }
        findViewById<Button>(R.id.btn_culture).setOnClickListener {
            newsViewModel.fetchNewsByCategory(
                103
            )
        }
        findViewById<Button>(R.id.btn_world).setOnClickListener {
            newsViewModel.fetchNewsByCategory(
                104
            )
        }
        findViewById<Button>(R.id.btn_it).setOnClickListener {
            newsViewModel.fetchNewsByCategory(105) }

        findViewById<ImageView>(R.id.backButton1).setOnClickListener {
            finish()
        }


    }

    override fun onNewsClick(newsArticle: NewsArticle) {
        val intent = Intent(this, WebViewActivity2::class.java).apply {
            putExtra("url", newsArticle.url)
        }
        startActivity(intent)
    }

}