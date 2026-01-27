package com.livebusjourneytracker.core.domain.model

data class BusArrival(
    val naptanId: String,
    val vehicleId: String,
    val timeToStation: Double,
    val stationName: String,
    val modeName: String,
    val expectedArrival: String,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val lines: List<String> = emptyList()

)