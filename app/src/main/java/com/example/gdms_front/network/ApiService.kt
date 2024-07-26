package com.example.gdms_front.network

import com.example.gdms_front.model.JoinRequest
import com.example.gdms_front.model.JoinResponse
import com.example.gdms_front.model.LoginRequest
import com.example.gdms_front.model.LoginResponse
import com.example.gdms_front.model.NewsArticle
import com.example.gdms_front.model.PayRequest
import com.example.gdms_front.model.PayResponse
import com.example.gdms_front.model.ServicePack
import com.example.gdms_front.model.Subscription
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    
    
    @POST("/api/login") // 로그인
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("/api/join") // 회원가입
    suspend fun join(@Body joinRequest: JoinRequest): Response<JoinResponse>

    @POST("/api/map/pay") // 결제
    suspend fun pay(@Body payRequest: PayRequest): Response<PayResponse>

    @GET("/api/news")
    fun getAllNews(): Call<List<NewsArticle>>

    @GET("/api/news/category/{categoryId}")
    fun getNewsByCategory(@Path("categoryId") categoryId: Int): Call<List<NewsArticle>>

    @GET("/api/subscriptions/current/{userId}")
    fun getCurrentSubscriptions(@Path("userId") userId: String): Call<List<Subscription>>

    @GET("/api/servicePacks")
    fun getServicePacks() : Call<List<ServicePack>>

    
    
}