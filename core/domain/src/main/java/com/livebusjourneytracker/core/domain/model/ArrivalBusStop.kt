package com.livebusjourneytracker.core.domain.model

data class ArrivalBusStop(
    val naptanId: String,
    val vehicleId: Double,
    val timeToStation: Double,
    val stationName: String
)