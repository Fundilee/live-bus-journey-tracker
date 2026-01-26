package com.livebusjourneytracker.core.domain.model

data class BusArrival(
    val naptanId: String,
    val vehicleId: Double,
    val timeToStation: Double,
    val stationName: String,
    val modeName: String,
    val expectedArrival: String,
    val lat: Double? = null,
    val lon: Double? = null
)