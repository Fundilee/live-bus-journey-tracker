package com.livebusjourneytracker.feature.busroutes.data.dto

import com.google.gson.annotations.SerializedName

data class BusStopDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("commonName")
    val commonName: String,
    @SerializedName("distance")
    val distance: Double? = null,
    @SerializedName("bearing")
    val bearing: String? = null,
    @SerializedName("naptanId")
    val naptanId: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("stopType")
    val stopType: String,
    @SerializedName("zone")
    val zone: String? = null,
    @SerializedName("lines")
    val lines: List<String>? = null
)

data class StopPointDto(
    @SerializedName("naptanId")
    val naptanId: String,
    @SerializedName("commonName")
    val commonName: String,
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("modes")
    val modes: List<String>,
    @SerializedName("icsCode")
    val icsCode: String,
    @SerializedName("smsCode")
    val smsCode: String? = null,
    @SerializedName("stopType")
    val stopType: String,
    @SerializedName("accessibilitySummary")
    val accessibilitySummary: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("lines")
    val lines: List<BusLineDto>? = null
)

data class BusLineDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("uri")
    val uri: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("routeType")
    val routeType: String,
    @SerializedName("status")
    val status: String
)