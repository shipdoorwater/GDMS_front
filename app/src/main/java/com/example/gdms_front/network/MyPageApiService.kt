package com.example.gdms_front.network

import com.example.gdms_front.model.MemberInfoResponse
import com.example.gdms_front.model.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface MyPageApiService {
    @GET("api/memberInfo/{userId}")
    fun getMemberInfo(@Path("userId") userId: String): Call<MemberInfoResponse>

    @Multipart
    @POST("/api/uploadProfileImage")
    fun uploadProfileImage(
        @Part("userId") userId: RequestBody,
        @Part("title") title: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<UploadResponse>
}