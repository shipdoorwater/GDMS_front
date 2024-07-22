package com.example.gdms_front.network

import com.example.gdms_front.model.LoginRequest
import com.example.gdms_front.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    
    
    @POST("/api/login") // 로그인
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    
    
}