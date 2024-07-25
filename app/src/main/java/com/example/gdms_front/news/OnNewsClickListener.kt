package com.example.gdms_front.news

import com.example.gdms_front.model.NewsArticle

interface OnNewsClickListener {
    fun onNewsClick(newsArticle: NewsArticle)
}