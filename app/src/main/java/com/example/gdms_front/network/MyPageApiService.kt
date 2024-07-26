package com.example.gdms_front.network

import com.example.gdms_front.model.MemberInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MyPageApiService {
    @GET("api/memberInfo/{userId}")
    fun getMemberInfo(@Path("userId") userId: String): Call<MemberInfoResponse>
}