package com.livebusjourneytracker.core.data.dto

import com.google.gson.annotations.SerializedName

data class ArrivalsBusStopsDto(
    @SerializedName("naptanId")
    val naptanId: String,
    @SerializedName("vehicleId")
    val vehicleId: Double,
    @SerializedName("timeToStation")
    val timeToStation: Double,
    @SerializedName("stationName")
    val stationName: String
)