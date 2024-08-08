package com.example.gdms_front.network

import com.example.gdms_front.model.TokenUpdate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApiService {
    @POST("update-token")
    suspend fun updateToken(@Body tokenUpdate: TokenUpdate): Response<Void>
}