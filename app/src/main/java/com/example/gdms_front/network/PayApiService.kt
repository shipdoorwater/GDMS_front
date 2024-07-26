package com.example.gdms_front.network

import com.example.gdms_front.model.MyPointResponse
import com.example.gdms_front.model.WithdrawRequest
import com.example.gdms_front.model.WithdrawResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PayApiService {
    @GET("/api/totalPoints/{userId}")
    fun getMyPoint(@Path("userId") userId: String): Call<MyPointResponse>

    @POST("/api/withdrawPoints")
    fun withdrawPoints(@Body request: WithdrawRequest): Call<WithdrawResponse>
}