package com.example.gdms_front.network

import com.example.gdms_front.model.GetPointRequest
import com.example.gdms_front.model.GetPointResponse
import com.example.gdms_front.model.JoinRequest
import com.example.gdms_front.model.JoinResponse
import com.example.gdms_front.model.ShopModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MapApiService {

    @GET("/api/map/getNearbyShops")
    fun getNearbyShops(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): Call<List<ShopModel>>

    @POST("/api/map/visit") // 회원가입
    suspend fun getPointByVisit(@Body getPointRequest: GetPointRequest): Response<GetPointResponse>

//    @POST("/api/map/visit")
//    fun getPointByVisit(
//        @Field("userId") userId: String,
//        @Field("bizNo") bizNo: String
//    ): Call<GetPointResponse>
}