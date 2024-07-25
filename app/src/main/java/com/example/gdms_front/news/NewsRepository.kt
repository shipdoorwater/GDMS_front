package com.example.gdms_front.news


import com.example.gdms_front.model.NewsArticle
import com.example.gdms_front.network.ApiService
import retrofit2.Call

// 여기는 API 호출을 담당하는 클래스 임

class NewsRepository(private val apiService: ApiService) {
    // 모든 뉴스를 가져오는 훵션
    fun getAllNews(): Call<List<NewsArticle>> = apiService.getAllNews()
    
    //카테고리별 뉴스를 가져오는 훵션
    fun getNewsByCategory(categoryId: Int): Call<List<NewsArticle>> = apiService.getNewsByCategory(categoryId)
}
