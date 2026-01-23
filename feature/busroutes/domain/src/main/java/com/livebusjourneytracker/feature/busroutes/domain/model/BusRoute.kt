package com.livebusjourneytracker.feature.busroutes.domain.model

data class BusRoute(
    val id: String,
    val name: String,
    val lineStatuses: List<LineStatus> = emptyList(),
    val routeSections: List<RouteSection> = emptyList(),
    val serviceTypes: List<ServiceType> = emptyList()
)

data class LineStatus(
    val id: String,
    val statusSeverity: Int,
    val statusSeverityDescription: String,
    val reason: String? = null
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