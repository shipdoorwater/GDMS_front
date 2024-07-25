package com.example.gdms_front.model

data class NewsArticle(
    val id: Int,
    val title: String,
    val url: String,
    val description: String,
    val publishedDate: String,
    val category: String,
    val imageUrl: String
)