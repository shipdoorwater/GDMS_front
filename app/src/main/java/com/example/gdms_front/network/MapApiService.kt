package com.example.gdms_front.network

import com.example.gdms_front.model.ShopModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MapApiService {

    @GET("/api/map/getNearbyShops")
    fun getNearbyShops(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): Call<List<ShopModel>>

}