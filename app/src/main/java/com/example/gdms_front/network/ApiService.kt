package com.example.gdms_front.network

import com.example.gdms_front.model.Coupon
import com.example.gdms_front.model.JoinRequest
import com.example.gdms_front.model.JoinResponse
import com.example.gdms_front.model.KakaoLoginRequest
import com.example.gdms_front.model.KakaoLoginResponse
import com.example.gdms_front.model.KakaoPayRequest
import com.example.gdms_front.model.KakaoPayResponse
import com.example.gdms_front.model.LoginRequest
import com.example.gdms_front.model.LoginResponse
import com.example.gdms_front.model.NewsArticle
import com.example.gdms_front.model.PayHistory
import com.example.gdms_front.model.PayHistoryResponse
import com.example.gdms_front.model.PayRequest
import com.example.gdms_front.model.PayResponse
import com.example.gdms_front.model.ServicePack
import com.example.gdms_front.model.SubscribeRequest
import com.example.gdms_front.model.SubscribeResponse
import com.example.gdms_front.model.Subscription
import com.example.gdms_front.model.cancelSubRequest
import com.example.gdms_front.model.cancelSubResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    
    
    @POST("/api/login") // 로그인
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    // 카카오 로그인
    @POST("/api/kakaoLogin")
    fun kakaoLogin(@Body request: KakaoLoginRequest): Call<KakaoLoginResponse>


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

    @POST("/api/subscribe")
    suspend fun subscribe(@Body subscribeRequest: SubscribeRequest): Response<SubscribeResponse>

    @POST("/api/cancelSubscription")
    suspend fun cancelSubscription(@Body cancelSubRequest: cancelSubRequest): Response<cancelSubResponse>

    @GET("/api/payHistory/dateRange/{userId}")
    fun getPayHistory(
        @Path("userId") userId: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Call<PayHistoryResponse>

    @GET("/api/kakaoPaySuccess")
    fun kakaoPayRequest(@Query("pg_token") pgToken: String,
                        @Query("tid") tid: String,
                        @Query("userId") userId: String,
                        @Query("packId") packId: Int,
                        @Query("amountPaid") amountPaid: Int)
    : Call<KakaoPayResponse>

    @GET("/api/getCouponStatus/{userId}")
    fun getCoupons(@Path("userId") userId: String): Call<List<Coupon>>


}



