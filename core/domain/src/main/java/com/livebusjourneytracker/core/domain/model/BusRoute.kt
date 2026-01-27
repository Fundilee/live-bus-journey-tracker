package com.livebusjourneytracker.core.domain.model

data class BusRoute(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val lineStatuses: List<LineStatus> = emptyList(),
    val routeSections: List<RouteSection> = emptyList(),
    val serviceTypes: List<ServiceType> = emptyList(),
    val stations:  List<Stations> = emptyList(),
    val lines: List<String> = emptyList()
)

data class LineStatus(
    val id: String,
    val statusSeverity: Int,
    val statusSeverityDescription: String,
    val reason: String? = null
)


data class Stations(
    val status: Boolean,
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val stopType: String
)

data class RouteSection(
    val name: String,
    val direction: String,
    val originator: String,
    val destination: String,
    val serviceType: String
)

data class ServiceType(
    val name: String,
    val uri: String
)