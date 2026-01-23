package com.livebusjourneytracker.feature.busroutes.domain.model

data class BusStop(
    val id: String,
    val name: String,
    val commonName: String,
    val distance: Double? = null,
    val bearing: String? = null,
    val naptanId: String,
    val lat: Double,
    val lon: Double,
    val stopType: String,
    val zone: String? = null,
    val lines: List<String> = emptyList()
)

data class StopPoint(
    val naptanId: String,
    val commonName: String,
    val distance: Double,
    val modes: List<String>,
    val icsCode: String,
    val smsCode: String? = null,
    val stopType: String,
    val accessibilitySummary: String,
    val lat: Double,
    val lon: Double,
    val lines: List<BusLine>
)

data class BusLine(
    val id: String,
    val name: String,
    val uri: String,
    val fullName: String,
    val type: String,
    val routeType: String,
    val status: String
)