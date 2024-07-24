package com.example.gdms_front.model

import com.google.gson.annotations.SerializedName

data class ShopModel (
    @SerializedName("bizNo") val bizNo: String,
    @SerializedName("shopName") val shopName: String,
    @SerializedName("shopDescript") val shopDescript: String,
    @SerializedName("storeCode") val storeCode: String,
    @SerializedName("storeLatitude") val storeLatitude: Double?,
    @SerializedName("storeLongitude") val storeLongitude: Double?,
    @SerializedName("shopScore") val shopScore: String,
    @SerializedName("shopReviewNum") val shopReviewNum: Int,
    @SerializedName("webImgUrl") val webImgUrl: String,
    @SerializedName("naverMapUrl") val naverMapUrl: String,
)
