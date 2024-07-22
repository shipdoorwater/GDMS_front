package com.example.gdms_front.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/api/login") // 실제 로그인 엔드포인트 경로로 변경
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
}