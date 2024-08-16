package com.example.gdms_front.news

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.adapter.NewsAdapter
import com.example.gdms_front.model.NewsArticle
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

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
            newsAdapter = NewsAdapter(news, this)
            recyclerView.adapter = newsAdapter
        })

        // Fetch all news initially
        newsViewModel.fetchAllNews()

        // Set up category chips
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.chip_all -> newsViewModel.fetchAllNews()
                R.id.chip_politics -> newsViewModel.fetchNewsByCategory(100)
                R.id.chip_economy -> newsViewModel.fetchNewsByCategory(101)
                R.id.chip_society -> newsViewModel.fetchNewsByCategory(102)
                R.id.chip_culture -> newsViewModel.fetchNewsByCategory(103)
                R.id.chip_world -> newsViewModel.fetchNewsByCategory(104)
                R.id.chip_it -> newsViewModel.fetchNewsByCategory(105)
            }
        }

        // Set "All" chip as default selected
        findViewById<Chip>(R.id.chip_all).isChecked = true

        findViewById<ImageButton>(R.id.backButton1).setOnClickListener {
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