package com.example.gdms_front.news

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.gdms_front.model.NewsArticle
import com.example.gdms_front.network.RetrofitClient
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class NewsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NewsRepository(RetrofitClient.apiService)
    
    //UI에 표시할 뉴스 목록을 담고 있는 LiveData 객체
    val newsList: MutableLiveData<List<NewsArticle>> = MutableLiveData()

    //모든 뉴스를 가져와서 LiveData에 저장
    fun fetchAllNews() {
        repository.getAllNews().enqueue(object : Callback<List<NewsArticle>> {
            override fun onResponse(call: Call<List<NewsArticle>>, response: Response<List<NewsArticle>>) {
                newsList.postValue(response.body())
            }
            override fun onFailure(call: Call<List<NewsArticle>>, t: Throwable) {
                // Handle failure
            }
        })
    }

    //특정 카테고리의 뉴스를 가져와서 LiveData에 저장
    fun fetchNewsByCategory(categoryId: Int) {
        repository.getNewsByCategory(categoryId).enqueue(object : Callback<List<NewsArticle>> {
            override fun onResponse(call: Call<List<NewsArticle>>, response: Response<List<NewsArticle>>) {
                newsList.postValue(response.body())
            }
            override fun onFailure(call: Call<List<NewsArticle>>, t: Throwable) {
                // Handle failure
            }
        })
    }
}