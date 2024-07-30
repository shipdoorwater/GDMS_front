package com.example.gdms_front.network

import com.example.gdms_front.model.NoticeRequest
import com.example.gdms_front.model.NoticeResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NoticeApiService {

    @GET("api/board/{subId}")
    fun getNotices(@Path("subId") subId: String): Call<List<NoticeResponse>>

    @GET("api/board/{subId}/{boardId}")
    fun getNoticeDetail(@Path("subId") subId: String, @Path("boardId") boardId: Int): Call<NoticeResponse>
}