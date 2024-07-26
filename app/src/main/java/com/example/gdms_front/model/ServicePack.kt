package com.example.gdms_front.model

data class ServicePack (
    val packId: Int,
    val packName: String,
    val packBrief: String,
    val packDescrip: String,
    val monthlyPrice: Int,
    val packStatus: Boolean
)