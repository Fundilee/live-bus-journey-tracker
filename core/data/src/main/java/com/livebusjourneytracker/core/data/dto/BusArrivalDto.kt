package com.livebusjourneytracker.core.data.dto

import com.google.gson.annotations.SerializedName

data class BusArrivalDto(
    @SerializedName("naptanId")
    val naptanId: String,
    @SerializedName("vehicleId")
    val vehicleId: Double,
    @SerializedName("timeToStation")
    val timeToStation: Double,
    @SerializedName("stationName")
    val stationName: String,
    @SerializedName("modeName")
    val modeName: String,
    @SerializedName("expectedArrival")
    val expectedArrival: String
)