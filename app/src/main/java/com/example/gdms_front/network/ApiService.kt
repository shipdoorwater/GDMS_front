package com.example.gdms_front.network

import com.example.gdms_front.model.JoinRequest
import com.example.gdms_front.model.JoinResponse
import com.example.gdms_front.model.LoginRequest
import com.example.gdms_front.model.LoginResponse
import com.example.gdms_front.model.PayRequest
import com.example.gdms_front.model.PayResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    
    
    @POST("/api/login") // 로그인
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("/api/join") // 회원가입
    suspend fun join(@Body joinRequest: JoinRequest): Response<JoinResponse>

    @POST("/api/map/pay") // 결제
    suspend fun pay(@Body payRequest: PayRequest): Response<PayResponse>

    
    
}